/*
 * Copyright 2018 vasgat.
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

import company.data.integration.ocmapping.http.HTTPRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author vasgat
 */
public class Jurisdictions {

    private ArrayList<Document> jurisdictions;

    public Jurisdictions() throws UnsupportedEncodingException {
        HTTPRequest request = new HTTPRequest("https://api.opencorporates.com/v0.4.6");
        request.addParameter("api_token", "MCS2yzvPpwycExpJUAFM");
        request.GET("jurisdictions");

        jurisdictions = Document.parse(request.getResponse()).get("results", Document.class).get("jurisdictions", ArrayList.class);

    }

    public Map<String, String> getCountryCodes() {
        HashMap<String, String> country_codes = new HashMap();
        for (Document j : jurisdictions) {
            Document jurisdiction = j.get("jurisdiction", Document.class);
            if (!jurisdiction.getString("code").contains("_")) {
                country_codes.put(jurisdiction.getString("country").toLowerCase(), jurisdiction.getString("code"));
            } else {
                country_codes.put(jurisdiction.getString("country").toLowerCase(), jurisdiction.getString("code").replaceAll("_.*", ""));
            }
        }
        return country_codes;
    }

    public Map<String, String> getStateCodes() {
        HashMap<String, String> state_codes = new HashMap();
        for (Document j : jurisdictions) {
            Document jurisdiction = j.get("jurisdiction", Document.class);
            if (jurisdiction.getString("code").contains("_")) {
                state_codes.put(jurisdiction.getString("name").toLowerCase(), jurisdiction.getString("code").replaceAll(".*_", ""));
            }
        }
        return state_codes;
    }

}
