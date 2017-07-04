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

import company.data.integration.ocmapping.similarityfunctions.CompanyMatchSimilarity;
import company.data.integration.ocmapping.OCUtils.CountryCodes;
import company.data.integration.ocmapping.OCUtils.StateCodes;
import company.data.integration.ocmapping.OpenCorporatesAPI.OCSearchCompany;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    private CompanyMatchSimilarity similarity;

    public CompanyMapper(OpenCorporatesClient client, StateCodes state_codes, CountryCodes country_codes, CompanyMatchSimilarity similarity) {
        this.client = client;
        this.state_codes = state_codes;
        this.country_codes = country_codes;
        this.similarity = similarity;
    }

    public String findMatch(CompanyEntity query) throws UnsupportedEncodingException {
        Pair<String, String> codes = getCodes(query);
        JSONArray results = getQueryResults(query, codes);

        String mapped_entity = search_in_results(query, results);

        if (mapped_entity != null) {
            return mapped_entity;
        }

        if (query.company_name.contains("inc") && !query.company_name.contains("incorporated")) {
            query.company_name = query.company_name
                    .replace("inc", "incorporated")
                    .replace("incorporated.", "incorporated");
            results = getQueryResults(query, codes);
            return search_in_results(query, results);
        } else if (query.company_name.contains("incorporated")) {
            query.company_name = query.company_name
                    .replace("incorporated", "inc");
            results = getQueryResults(query, codes);
            return search_in_results(query, results);
        } else if (query.company_name.contains("corporation")) {
            query.company_name = query.company_name
                    .replace("corporation", "corp");
            results = getQueryResults(query, codes);
            return search_in_results(query, results);
        }

        if (mapped_entity == null) {
            query.company_name = query.company_name
                    .toLowerCase()
                    .replace("incorporated", "")
                    .replace("inc.", "")
                    .replace(" inc", "")
                    .replace("corporation", "")
                    .replace(" corp", "")
                    .replace("limited", "")
                    .replace(" ltd", "")
                    .replace("plc", "")
                    .replace(" lp", "")
                    .trim();
            results = getQueryResults(query, codes);
            return search_in_results(query, results);
        }

        return null;
    }

    private String search_in_results(CompanyEntity query, JSONArray results) {
        HashMap<Integer, Double> candidates = new HashMap();
        double max = 0;

        for (int i = 0; i < results.length(); i++) {
            JSONObject company = ((JSONObject) results.get(i)).getJSONObject("company");

            JSONArray previous_names = company.getJSONArray("previous_names");

            String candidate_name = company.getString("name");
            
            CompanyEntity candidate_match = new CompanyEntity.Builder(candidate_name)
                    .address(
                            company.get("registered_address_in_full").toString()
                    )
                    .build();

            double sim = similarity.calculate(query, candidate_match);

            for (int j = 0; j < previous_names.length(); j++) {

                candidate_match = new CompanyEntity.Builder(previous_names.getJSONObject(j).getString("company_name"))
                        .address(
                                company.get("registered_address_in_full").toString()
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

        String mapped_entity = null;
        candidates.values().retainAll(Collections.singleton(max));

        if (max >= 0.425) {
            Set<Integer> keys = candidates.keySet();
            for (Integer index : keys) {
                JSONObject temp_company = ((JSONObject) results.get(index)).getJSONObject("company");

                if (temp_company.get("branch_status") == null) {

                    if (mapped_entity == null) {
                        mapped_entity = ((JSONObject) results.get(index)).getJSONObject("company")
                                .getString("opencorporates_url");
                    }

                    if (temp_company.getString("current_status") != null && (temp_company.getString("current_status").contains("Active") || temp_company.getString("current_status").contains("Good Standing"))) {

                        mapped_entity = ((JSONObject) results.get(index)).getJSONObject("company")
                                .getString("opencorporates_url");
                        break;
                    }
                }
            }
            if (mapped_entity == null) {
                mapped_entity = ((JSONObject) results.get(keys.iterator().next())).getJSONObject("company")
                        .getString("opencorporates_url");
            }
        }
        return mapped_entity;
    }

    private JSONArray getQueryResults(CompanyEntity query, Pair<String, String> codes) throws UnsupportedEncodingException {
        String search_term = query.company_name;
        JSONObject result = null;

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

        return result.getJSONObject("results").getJSONArray("companies");
    }

    private Pair<String, String> getCodes(CompanyEntity query) {
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
            return searchWikipedia(query.company_name);
        }

        if (query.country.toLowerCase().equals("united states") && query.state == null) {
            Pair<String, String> codes = searchWikipedia(query.company_name);
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

            Document document = connection.get();

            Elements rows = document.select(".infobox tr");

            for (int i = 0; i < rows.size(); i++) {
                Element contents = rows.get(i);

                if (contents.select("th") != null
                        && (contents.select("th").text().toLowerCase().equals("location")
                        || contents.select("th").text().toLowerCase().equals("headquarters"))) {

                    String location = contents.select("td").text().toLowerCase();

                    String country_code = country_codes.findCountryInText(location);
                    if (country_code.equals("us")) {
                        String state_code = state_codes.findStateInText(location);
                        return new Pair(country_code, state_code);
                    }

                    return new Pair(country_code, null);
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
