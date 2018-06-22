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
package company.data.integration.ocmapping.OpenCorporatesAPI;

import company.data.integration.ocmapping.http.HTTPRequest;
import java.io.UnsupportedEncodingException;
import org.bson.Document;

/**
 *
 * @author vasgat
 */
public class CompanyStateOfIncorporation {

    public static String get(String jurisdiction_code, String company_number) throws UnsupportedEncodingException {
        HTTPRequest request = new HTTPRequest("http://api.opencorporates.com/v0.4.6/companies/");
        request.addParameter("api_token", "****");
        request.GET(jurisdiction_code + "/" + company_number);

        String response = request.getResponse();

        Document company = Document.parse(response);

        return company.get("results", Document.class).get("company", Document.class).getString("jurisdiction_code");
    }

}
