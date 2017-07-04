package company.data.integration.similarityfunctions;

import certh.iti.mklab.jSimilarity.documentUtils.BasicTokenizer;
import certh.iti.mklab.jSimilarity.documentUtils.Corpus;
import certh.iti.mklab.jSimilarity.documentUtils.TextDocument;
import certh.iti.mklab.jSimilarity.stringsimilarities.CosineSimilarity;
import certh.iti.mklab.jSimilarity.tfidf.idf.InverseDocumentFrequency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
/**
 *
 * @author vasgat
 */
public class IDFBasedSimilarity {

    ArrayList<HashMap<String, Double>> idfs;
    List<ArrayList<String>> reference_set;
    List<Double> average_weights;
    List<Double> weights_per_column;

    public IDFBasedSimilarity(List<ArrayList<String>> reference_set, List<Double> weights_per_column) {
        this.reference_set = reference_set;
        this.weights_per_column = weights_per_column;
        calculate_idfs_per_column();
        calc_avg_weights();

    }

    public Double calculate(ArrayList<String> u, ArrayList<String> v) {
        if (!reference_set.contains(v)) {
            return null;
        }

        double similarity = 0.0;
        for (int i = 0; i < u.size(); i++) {
            similarity += weights_per_column.get(i) * column_similarity(u, v, i);
        }

        return similarity;
    }

    private double column_similarity(ArrayList<String> u, ArrayList<String> v, int column) {
        CosineSimilarity cosine = new CosineSimilarity();

        BasicTokenizer tokenizer = new BasicTokenizer();
        String[] u_tokens = tokenizer.tokenize(u.get(column));
        String[] v_tokens = tokenizer.tokenize(v.get(column));

        HashMap<String, Double> u_weights = new HashMap();
        for (int i = 0; i < u_tokens.length; i++) {
            if (idfs.get(column).get(u_tokens[i]) != null) {
                u_weights.put(u_tokens[i], idfs.get(column).get(u_tokens[i]));
            } else {
                u_weights.put(u_tokens[i], average_weights.get(column));
            }
        }

        HashMap<String, Double> v_weights = new HashMap();
        for (int i = 0; i < v_tokens.length; i++) {
            v_weights.put(v_tokens[i], idfs.get(column).get(v_tokens[i]));
        }
        return cosine.calculate(u_weights, v_weights);
    }

    private void calculate_idfs_per_column() {
        idfs = new ArrayList();

        for (int j = 0; j < reference_set.get(0).size(); j++) {
            Corpus column_corpus = new Corpus();
            for (int i = 0; i < reference_set.size(); i++) {
                TextDocument document = new TextDocument.Builder(reference_set.get(i).get(j)).build();
                column_corpus.addDocument(document);
            }
            InverseDocumentFrequency idf = new InverseDocumentFrequency(column_corpus);
            idfs.add(idf.calculate());
        }

        for (int i = 0; i < idfs.size(); i++) {
            for (Map.Entry<String, Double> entry : idfs.get(i).entrySet()) {
                if (entry.getValue() == 0.0) {
                    idfs.get(i).put(entry.getKey(), 0.01);
                }
            }
        }
    }

    private void calc_avg_weights() {
        average_weights = new ArrayList();

        for (int i = 0; i < idfs.size(); i++) {
            Double temp = 0.0;
            for (Map.Entry<String, Double> entry : idfs.get(i).entrySet()) {
                temp += entry.getValue();
            }
            average_weights.add(temp / idfs.get(i).size());
        }
    }
}
