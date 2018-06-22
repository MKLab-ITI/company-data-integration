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
package company.data.integration.ocmapping.OCUtils;

import certh.iti.mklab.jSimilarity.stringsimilarities.JaroWinklerDistance;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class CountryCodes {

    private Map<String, String> countries;

    public CountryCodes(Jurisdictions jurisdictions) {
        this.countries = jurisdictions.getCountryCodes();
    }

    public String findCode(String state) {
        if (countries.containsKey(state.toLowerCase())) {
            return countries.get(state.toLowerCase());
        }

        if (countries.containsValue(state.toLowerCase())) {
            return state.toLowerCase();
        }

        JaroWinklerDistance jw = new JaroWinklerDistance();
        double max_sim = 0;
        String candidate_country = "";

        for (String current_state : countries.keySet()) {
            double jw_sim = jw.calculate(state, current_state);
            if (jw_sim > max_sim) {
                max_sim = jw_sim;
                candidate_country = current_state;
            }
        }

        if (max_sim > 0.75) {
            return countries.get(candidate_country);
        }

        return null;
    }

    public String findCountryInText(String text) {
        for (Map.Entry<String, String> entry : countries.entrySet()) {
            if (text.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }

        }

        if (!text.replaceAll("\\bus\\b", "").equals(text) || !text.replaceAll("\\busa\\b", "").equals(text)) {
            return "us";
        }

        if (!text.replaceAll("\\buk\\b", "").equals(text)) {
            return "gb";
        }
        return null;
    }
}
