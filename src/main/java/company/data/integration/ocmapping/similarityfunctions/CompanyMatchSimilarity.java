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
package company.data.integration.ocmapping.similarityfunctions;

import company.data.integration.ocmapping.CompanyEntity;
import company.data.integration.ocmapping.nlp.TextCorpus;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author vasgat
 */
public class CompanyMatchSimilarity extends EntitySimilarity {

    TextCorpus companies_corpus;
    TextCorpus address_corpus;

    public CompanyMatchSimilarity() {
        HashSet companies = new HashSet();

        try (BufferedReader br = new BufferedReader(new FileReader("D:\\NetBeans\\company-data-integration\\src\\main\\java\\company\\data\\integration\\ocmapping\\data\\list_of_company_names.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                companies.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        HashSet addresses = new HashSet();

        try (BufferedReader br = new BufferedReader(new FileReader("D:\\NetBeans\\company-data-integration\\src\\main\\java\\company\\data\\integration\\ocmapping\\data\\address_corpus.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                addresses.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.companies_corpus = new TextCorpus.Builder(companies).build();
        this.address_corpus = new TextCorpus.Builder(addresses).build();
    }

    @Override
    public Double calculate(CompanyEntity company1, CompanyEntity company2) {
        if (company1.address == null || company2.address == null || company1.address.equals("null") || company2.address.equals("null")) {
            return companies_corpus.similarity(company1.company_name, company2.company_name) * 0.95;
        }

        return companies_corpus.similarity(company1.company_name, company2.company_name) * 0.95 + address_corpus.similarity(company1.address.toString(), company2.address.toString()) * 0.05;

    }
}
