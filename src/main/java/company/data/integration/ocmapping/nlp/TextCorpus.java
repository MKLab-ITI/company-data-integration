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
import certh.iti.mklab.jSimilarity.documentUtils.TextDocument;
import certh.iti.mklab.jSimilarity.tfidf.TFIDF;
import java.util.HashSet;

/**
 *
 * @author vasgat
 */
public class TextCorpus {

    private Corpus corpus;
    private TFIDF tfidf;

    private TextCorpus(Builder builder) {
        this.corpus = builder.corpus;

        this.tfidf = new TFIDF(corpus);
        this.tfidf.calculate();
    }

    public double similarity(String string1, String string2) {
        CompanyDocument document = new CompanyDocument.Builder(string1)
                .id("candidate1")
                .build();

        CompanyDocument document2 = new CompanyDocument.Builder(string2)
                .id("candidate2")
                .build();

        tfidf.calculate(document);
        tfidf.calculate(document2);

        return tfidf.similarity("candidate1", "candidate2");
    }

    public static class Builder {

        private Corpus corpus;
        private HashSet<String> strings;

        public Builder(HashSet<String> strings) {
            this.strings = strings;
        }

        public TextCorpus build() {
            corpus = new Corpus();

            for (String company : strings) {
                TextDocument document = new TextDocument.Builder(company)
                        .build();

                corpus.addDocument(document);
            }

            return new TextCorpus(this);
        }
    }
}
