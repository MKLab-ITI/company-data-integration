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
package company.data.integration.ocmapping;

import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author vasgat
 */
public class OpenCorpCompany {

    String company_number;
    String jurisdiction_code;
    String jurisdiction_code_of_incorporation;
    ArrayList<JSONObject> industry_codes;

    public OpenCorpCompany(String url) {
        company_number = url.replaceAll(".*/", "");
        jurisdiction_code = url.replaceAll(".*companies/", "").replaceAll("/.*", "");
    }

    public void setJurisdictionCodeOfIncorporation(String j_code) {
        jurisdiction_code_of_incorporation = j_code;
    }

    public void setIndustryCodes(ArrayList<JSONObject> i_codes) {
        industry_codes = i_codes;
    }
}
