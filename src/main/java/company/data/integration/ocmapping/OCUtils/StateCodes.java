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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class StateCodes {

    private String DEFAULT_FILE_PATH = "src\\main\\java\\company\\data\\integration\\ocmapping\\data\\state_codes.csv";

    private Map<String, String> states;

    public StateCodes() {
        loadCodes(DEFAULT_FILE_PATH);
    }

    public StateCodes(String filePath) {
        loadCodes(filePath);
    }

    public String findCode(String state) {
        if (states.containsKey(state.toLowerCase())) {
            return states.get(state.toLowerCase());
        }

        JaroWinklerDistance jw = new JaroWinklerDistance();
        double max_sim = 0;
        String candidate_state = "";

        for (String current_state : states.keySet()) {
            double jw_sim = jw.calculate(state, current_state);
            if (jw_sim > max_sim) {
                max_sim = jw_sim;
                candidate_state = current_state;
            }
        }

        if (max_sim > 0.75) {
            return candidate_state;
        }

        return null;
    }

    public String findStateInText(String text) {
        for (Map.Entry<String, String> entry : states.entrySet()) {
            if (text.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void loadCodes(String filePath) {

        states = new HashMap();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] codes = line.split(";");
                states.put(codes[0], codes[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
