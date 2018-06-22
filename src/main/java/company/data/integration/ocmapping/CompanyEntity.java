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

import java.util.UUID;

/**
 *
 * @author vasgat
 */
public class CompanyEntity {

    public String company_name;
    public String initial_company_name;
    public String country;
    public String state;
    public String address;
    public String id;

    private CompanyEntity(Builder builder) {

        if (builder.id == null) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = builder.id;
        }
        this.initial_company_name = builder.company_name;

        this.company_name = builder.company_name.replace(",", "").replaceAll("\\s+", " ").trim().toLowerCase();;

        this.state = builder.state;

        this.address = builder.address;

        this.country = builder.country;
    }

    public static class Builder {

        private String id;
        private String company_name;
        private String state;
        private String country;
        private String address;

        public Builder(String company_name) {
            this.company_name = company_name;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder country(String country) {
            this.country = country.toLowerCase();
            return this;
        }

        public Builder state(String state) {
            this.state = state.toLowerCase();
            return this;
        }

        public Builder address(String address) {
            this.address = address.toLowerCase();
            return this;
        }

        public CompanyEntity build() {
            return new CompanyEntity(this);
        }
    }
}
