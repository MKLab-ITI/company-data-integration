/*
 * Copyright 2017 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package company.data.integration.ocmapping;

import company.data.integration.ocmapping.OCUtils.CountryCodes;
import company.data.integration.ocmapping.OCUtils.Jurisdictions;
import company.data.integration.ocmapping.OCUtils.StateCodes;
import company.data.integration.ocmapping.OpenCorporatesAPI.CompanyStateOfIncorporation;
import company.data.integration.ocmapping.OpenCorporatesAPI.OCSearchCompany;
import company.data.integration.ocmapping.OpenCorporatesAPI.OCSearchTermFormatter;
import company.data.integration.ocmapping.similarityfunctions.EntitySimilarity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import javafx.util.Pair;
import org.bson.Document;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public class CompanyMapper {

    private StateCodes state_codes;
    private CountryCodes country_codes;
    private OpenCorporatesClient client;
    private EntitySimilarity similarity;

    public CompanyMapper(OpenCorporatesClient client, Jurisdictions jurisdictions, EntitySimilarity similarity) {
        this.client = client;
        this.state_codes = new StateCodes(jurisdictions);
        this.country_codes = new CountryCodes(jurisdictions);
        this.similarity = similarity;
    }

    public Document findMatch(CompanyEntity query) throws UnsupportedEncodingException {
        Pair<String, String> codes = getCodes(query);
        if (codes.getKey() == null) {
            return null;
        }
        if (codes.getKey().equals("us") && codes.getKey() == null) {
            return null;
        }

        String search_term = query.company_name;
        ArrayList<Document> results = getQueryResults(search_term, codes);

        Document mapped_entity = search_in_results(query, results);

        if (mapped_entity != null) {
            if (mapped_entity.getBoolean("is_branch") == false) {
                mapped_entity.remove("is_branch");
                mapped_entity.append("incorporation_jurisdiction_code", mapped_entity.getString("jurisdiction_code"));
            } else {
                mapped_entity.remove("is_branch");
                mapped_entity.append("incorporation_jurisdiction_code", CompanyStateOfIncorporation.get(mapped_entity.getString("jurisdiction_code"), mapped_entity.getString("company_number")));
            }
            return mapped_entity;
        }

        if (!search_term.equals(OCSearchTermFormatter.format(query.company_name))) {
            results = getQueryResults(OCSearchTermFormatter.format(query.company_name), codes);
            mapped_entity = search_in_results(query, results);
        }

        if (mapped_entity == null && !search_term.equals(OCSearchTermFormatter.removeType(search_term))) {
            results = getQueryResults(search_term, codes);
            mapped_entity = search_in_results(query, results);
        }

        if (mapped_entity != null && mapped_entity.getBoolean("is_branch") == false) {
            mapped_entity.remove("is_branch");
            mapped_entity.append("incorporation_jurisdiction_code", mapped_entity.getString("jurisdiction_code"));
        } else {
            mapped_entity.remove("is_branch");
            mapped_entity.append("incorporation_jurisdiction_code", CompanyStateOfIncorporation.get(mapped_entity.getString("jurisdiction_code"), mapped_entity.getString("company_number")));
        }

        return mapped_entity;
    }

    private Document search_in_results(CompanyEntity query, ArrayList<Document> results) {

        HashMap<Integer, Double> candidates = new HashMap();
        double max = 0;

        for (int i = 0; i < results.size(); i++) {
            Document company = results.get(i).get("company", Document.class);

            ArrayList<Document> previous_names = company.get("previous_names", ArrayList.class);

            String candidate_name = company.getString("name");

            CompanyEntity candidate_match = new CompanyEntity.Builder(candidate_name)
                    .address(
                            company.getString("registered_address_in_full")
                    )
                    .build();

            double sim = similarity.calculate(query, candidate_match);

            for (int j = 0; j < previous_names.size(); j++) {
                candidate_match = new CompanyEntity.Builder(previous_names.get(j).getString("company_name"))
                        .address(
                                company.getString("registered_address_in_full")
                        )
                        .build();

                double temp_sim = similarity.calculate(query, candidate_match);
                if (temp_sim > sim) {
                    sim = temp_sim;
                }
            }

            if (max < sim) {
                max = sim;
            }

            candidates.put(i, sim);
        }

        Document mapped_entity = null;
        candidates.values().retainAll(Collections.singleton(max));

        if (max >= 0.45) {
            Set<Integer> keys = candidates.keySet();
            for (Integer index : keys) {
                Document temp_company = results.get(index).get("company", Document.class);

                if (temp_company.get("branch_status") == null) {
                    if (mapped_entity == null) {
                        mapped_entity = temp_company;
                        break;
                    }
                }
            }
            if (mapped_entity == null) {
                mapped_entity = results.get(keys.iterator().next()).get("company", Document.class);
            }
        }

        if (mapped_entity != null) {
            Document matchedCompany = new Document("opencorporates_url", mapped_entity.getString("opencorporates_url"))
                    .append("jurisdiction_code", mapped_entity.getString("jurisdiction_code"))
                    .append("company_number", mapped_entity.getString("company_number"))
                    .append("industry_codes", mapped_entity.get("industry_codes", ArrayList.class))
                    .append("name", mapped_entity.getString("name"));
            if (mapped_entity.get("branch_status") == null) {
                matchedCompany.append("is_branch", false);
                return matchedCompany;
            } else {
                matchedCompany.append("is_branch", true);
                return matchedCompany;
            }
        }
        return null;
    }

    private ArrayList<Document> getQueryResults(String search_term, Pair<String, String> codes) throws UnsupportedEncodingException {
        Document result = null;

        if (codes.getKey() != null && codes.getValue() != null) {
            String jurisdiction_code = codes.getKey() + "_" + codes.getValue();
            result = new OCSearchCompany.Builder(search_term)
                    .inactive(false)
                    .jurisdiction_code(jurisdiction_code)
                    .normalise_company_name(true)
                    .order("score")
                    .nonprofit(false)
                    .per_page(100)
                    .api_token(client.api_token)
                    .build();
        } else if (codes.getValue() == null && codes.getKey() != null) {
            String country_code = codes.getKey();
            result = new OCSearchCompany.Builder(search_term)
                    .inactive(false)
                    .country_code(country_code)
                    .normalise_company_name(true)
                    .order("score")
                    .nonprofit(false)
                    .per_page(100)
                    .api_token(client.api_token)
                    .build();
        } else {
            result = new OCSearchCompany.Builder(search_term)
                    .inactive(false)
                    .normalise_company_name(true)
                    .order("score")
                    .nonprofit(false)
                    .per_page(100)
                    .api_token(client.api_token)
                    .build();
        }

        return result.get("results", Document.class).get("companies", ArrayList.class);
    }

    private Pair<String, String> getCodes(CompanyEntity query) {
        if (query.country == null && query.state == null && query.address != null) {
            query.country = country_codes.findCountryInText(query.address);
            query.state = state_codes.findStateInText(query.address);
        }

        if (query.country != null && query.state != null) {
            return new Pair(
                    country_codes.findCode(query.country),
                    state_codes.findCode(query.state)
            );
        }

        if (query.country != null && !query.country.toLowerCase().equals("united states")) {
            return new Pair(
                    country_codes.findCode(query.country),
                    null
            );
        }

        if (query.country == null && query.state == null) {
            return searchWikipedia(query.initial_company_name);
        }

        if (query.country.toLowerCase().equals("united states") && query.state == null) {
            Pair<String, String> codes = searchWikipedia(query.initial_company_name);
            if (codes.getKey() != null && codes.getKey().equals("us")) {
                return codes;
            } else {
                return new Pair("us", null);
            }
        }
        return new Pair(null, null);
    }

    private Pair<String, String> searchWikipedia(String company_name) {
        try {
            String url_query = "http://en.wikipedia.org/wiki/"
                    + URLEncoder.encode(company_name.replace(" ", "_"), "UTF-8");

            Connection connection = Jsoup.connect(
                    new URI(url_query)
                    .toASCIIString()
            ).followRedirects(true)
                    .userAgent("Mozilla/37.0")
                    .timeout(60000);

            org.jsoup.nodes.Document document = connection.get();

            Elements rows = document.select(".infobox tr");

            for (int i = 0; i < rows.size(); i++) {
                Element contents = rows.get(i);

                if (contents.select("th") != null) {
                    if ((contents.select("th").text().toLowerCase().equals("location")
                            || contents.select("th").text().toLowerCase().equals("headquarters"))) {

                        String location = contents.select("td").text().toLowerCase().replace(".", "").replace(",", "");

                        String country_code = country_codes.findCountryInText(location);

                        if (country_code != null && country_code.equals("us")) {
                            String state_code = state_codes.findStateInText(location);

                            return new Pair(country_code, state_code);
                        }

                        return new Pair(country_code, null);
                    }
                }
            }
        } catch (IOException ex) {
            return new Pair(null, null);
        } catch (URISyntaxException ex) {
            return new Pair(null, null);
        }
        return new Pair(null, null);
    }
}
