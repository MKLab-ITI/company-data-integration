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
import java.net.URLEncoder;
import org.json.JSONObject;

/**
 *
 * @author vasgat
 */
public class OCSearchCompany {

    public static class Builder {

        private HTTPRequest request;

        public Builder(String q) throws UnsupportedEncodingException {
            this.request = new HTTPRequest("https://api.opencorporates.com/v0.4.6");
            request.addParameter("q", URLEncoder.encode(q, "UTF-8") + "*");
        }

        public Builder status(String status) {
            request.addParameter("status", status);
            return this;
        }

        public Builder country_code(String country_code) {
            request.addParameter("country_code", country_code);
            return this;
        }

        public Builder jurisdiction_code(String jurisdiction_code) {
            request.addParameter("jurisdiction_code", jurisdiction_code);
            return this;
        }

        public Builder company_type(String company_type) {
            request.addParameter("company_type", company_type);
            return this;
        }

        public Builder current_status(String current_status) {
            request.addParameter("current_status", current_status);
            return this;
        }

        public Builder industry_codes(String industry_codes) {
            request.addParameter("industry_codes", industry_codes);
            return this;
        }

        public Builder registered_address(String registered_address) {
            request.addParameter("registered_address", registered_address);
            return this;
        }

        public Builder created_since(String created_since) {
            request.addParameter("created_since", created_since);
            return this;
        }

        public Builder inactive(Boolean inactive) {
            request.addParameter("inactive", inactive + "");
            return this;
        }

        public Builder branch(Boolean branch) {
            request.addParameter("branch", branch + "");
            return this;
        }

        public Builder nonprofit(Boolean nonprofit) {
            request.addParameter("nonprofit", nonprofit.toString());
            return this;
        }

        public Builder fields(String fields) {
            request.addParameter("fields", fields);
            return this;
        }

        public Builder normalise_company_name(Boolean normalise_company_name) {
            request.addParameter("normalise_company_name", normalise_company_name.toString());
            return this;
        }

        public Builder api_token(String api_token) {
            request.addParameter("api_token", api_token);
            return this;
        }

        public Builder order(String order) {
            request.addParameter("order", order);
            return this;
        }

        public Builder per_page(int per_page) {
            request.addParameter("per_page", per_page + "");
            return this;
        }

        public JSONObject build() throws UnsupportedEncodingException {
            request.GET("companies/search");
            return new JSONObject(request.getResponse());
        }
    }

}
