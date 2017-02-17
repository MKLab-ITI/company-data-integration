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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
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

    public CompanyMapper() {
        this.state_codes = new StateCodes();
        this.country_codes = new CountryCodes();
    }

    public String findMatch(CompanyQuery query) {
        String country_code = "";
        String state_code = "";

        if (query.country == null && query.state == null) {
            Object location = searchWikipedia(query.company_name);
            if (location != null) {
                if (location instanceof String) {
                    country_code = (String) location;
                } else {
                    country_code = ((Pair) location).getKey().toString();
                    state_code = ((Pair) location).getValue().toString();
                }
            }
        }
        return null;
    }

    private Object searchWikipedia(String company_name) {
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

                    return country_code;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CompanyMapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(CompanyMapper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return null;
        }
    }
}
