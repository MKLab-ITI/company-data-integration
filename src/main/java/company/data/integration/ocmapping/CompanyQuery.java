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

/**
 *
 * @author vasgat
 */
public class CompanyQuery {

    public String company_name;
    public String country;
    public String state;

    private CompanyQuery(String company_name, String country, String state) {
        this.company_name = company_name;
        this.country = country;
        this.state = state;
    }

    public static class Builder {

        private String company_name;
        private String country;
        private String state;

        public Builder(String company_name) {
            this.company_name = company_name;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public CompanyQuery build() {
            return new CompanyQuery(company_name, country, state);
        }
    }
}
