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
package company.data.integration.ocmapping.nlp;

import certh.iti.mklab.jSimilarity.documentUtils.CompanyDocument;
import certh.iti.mklab.jSimilarity.documentUtils.Corpus;
import certh.iti.mklab.jSimilarity.tfidf.TFIDF;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author vasgat
 */
public class CompaniesCorpus {

    private Corpus corpus;
    private TFIDF tfidf;

    private CompaniesCorpus(Builder builder) {
        this.corpus = builder.corpus;

        System.out.println(corpus.size());
        this.tfidf = new TFIDF(corpus);
        this.tfidf.calculate();
    }

    public double similarity(String name1, String name2) {
        CompanyDocument document = new CompanyDocument.Builder(name1)
                .id("candidate1")
                .build();

        CompanyDocument document2 = new CompanyDocument.Builder(name2)
                .id("candidate2")
                .build();

        tfidf.calculate(document);
        tfidf.calculate(document2);

        return tfidf.similarity("candidate1", "candidate2");
    }

    public static class Builder {

        private Corpus corpus;
        private HashSet<String> companies;

        public Builder(HashSet<String> companies) {
            this.companies = companies;
        }

        public CompaniesCorpus build() {
            corpus = new Corpus();
            int i = 0;
            for (String company : companies) {
                CompanyDocument document = new CompanyDocument.Builder(company)
                        .build();

                corpus.addDocument(document);
            }

            return new CompaniesCorpus(this);
        }
    }
}
