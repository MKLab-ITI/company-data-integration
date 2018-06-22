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
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author vasgat
 */
public class StateCodes {

    private Map<String, String> states;

    public StateCodes(Jurisdictions jurisdictions) {
        this.states = jurisdictions.getStateCodes();
    }

    public String findCode(String state) {
        if (states.containsKey(state.toLowerCase())) {
            return states.get(state.toLowerCase());
        }

        if (states.containsValue(state.toLowerCase())) {
            return state.toLowerCase();
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
}
