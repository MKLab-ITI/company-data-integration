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
import company.data.integration.ocmapping.OCUtils.StateCodes;
import company.data.integration.ocmapping.OpenCorporatesAPI.OCSearchCompany;
import company.data.integration.ocmapping.nlp.CompaniesCorpus;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private CompaniesCorpus corpus;

    public CompanyMapper() {
        this.state_codes = new StateCodes();
        this.country_codes = new CountryCodes();
        HashSet companies = new HashSet();

        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\java\\company\\data\\integration\\ocmapping\\data\\list_of_company_names.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                companies.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.corpus = new CompaniesCorpus.Builder(companies).build();
    }

    public CompanyMapper(OpenCorporatesClient client) {
        this.state_codes = new StateCodes();
        this.country_codes = new CountryCodes();
        this.client = client;
        HashSet companies = new HashSet();

        try (BufferedReader br = new BufferedReader(new FileReader("src\\main\\java\\company\\data\\integration\\ocmapping\\data\\list_of_company_names.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                companies.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.corpus = new CompaniesCorpus.Builder(companies).build();
    }

    public String findMatch(CompanyQuery query) throws UnsupportedEncodingException {
        Pair<String, String> codes = getCodes(query);

        System.out.println(codes);

        JSONArray results = getQueryResults(query, codes);

        String search_term = query.company_name.replace(".", "").replace(",", "");

        HashMap<Integer, Double> candidates = new HashMap();
        double max = 0;
        for (int i = 0; i < results.length(); i++) {
            JSONObject company = ((JSONObject) results.get(i)).getJSONObject("company");

            String candidate_name = company.getString("name");

            double sim = corpus.similarity(search_term, candidate_name);

            if (max < sim) {
                max = sim;
            }
            candidates.put(i, sim);

        }

        String mapped_entity = null;
        candidates.values().retainAll(Collections.singleton(max));

        if (max > 0.6) {
            Set<Integer> keys = candidates.keySet();
            for (Integer index : keys) {
                JSONObject temp_company = ((JSONObject) results.get(index)).getJSONObject("company");

                if (temp_company.get("branch_status") == null) {

                    if (mapped_entity == null) {
                        mapped_entity = ((JSONObject) results.get(index)).getJSONObject("company")
                                .getString("company_number");
                    }

                    if (temp_company.getString("current_status") != null && (temp_company.getString("current_status").contains("Active") || temp_company.getString("current_status").contains("Good Standing"))) {
                        mapped_entity = ((JSONObject) results.get(index)).getJSONObject("company")
                                .getString("company_number");
                        break;
                    }
                }
            }
            if (mapped_entity == null) {
                mapped_entity = ((JSONObject) results.get(keys.iterator().next())).getJSONObject("company")
                        .getString("company_number");
            }
        }

        return mapped_entity;
    }

    private JSONArray getQueryResults(CompanyQuery query, Pair<String, String> codes) throws UnsupportedEncodingException {
        JSONArray results = new JSONArray();
        String search_term = null;

        if (client == null) {
            search_term = query.company_name.replace(".", "").replace(",", "");
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
                        .build();
            } else {
                result = new OCSearchCompany.Builder(search_term)
                        .inactive(false)
                        .normalise_company_name(true)
                        .order("score")
                        .nonprofit(false)
                        .per_page(100)
                        .build();
            }
            results = result.getJSONObject("results").getJSONArray("companies");
        } else {
            search_term = query.company_name.replace(".", "").replace(",", "");
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
            results = result.getJSONObject("results").getJSONArray("companies");
        }

        return results;
    }

    private Pair<String, String> getCodes(CompanyQuery query) {
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
            if (codes.getKey().equals("us")) {
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
            Logger.getLogger(CompanyMapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(CompanyMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Pair(null, null);
    }
}
