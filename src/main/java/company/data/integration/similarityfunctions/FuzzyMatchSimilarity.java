/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package company.data.integration.similarityfunctions;

import certh.iti.mklab.jSimilarity.documentUtils.BasicTokenizer;
import certh.iti.mklab.jSimilarity.documentUtils.Corpus;
import certh.iti.mklab.jSimilarity.documentUtils.TextDocument;
import certh.iti.mklab.jSimilarity.tfidf.idf.InverseDocumentFrequency;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class FuzzyMatchSimilarity {

    static List<HashMap<String, Double>> idfs;
    static List<Double> average_weights;
    List<ArrayList<String>> reference_set;

    public FuzzyMatchSimilarity(List<ArrayList<String>> reference_set) {
        this.reference_set = reference_set;
        calculate_idfs_per_column();
        calc_avg_weights();
    }

    public Double calculate(ArrayList u, ArrayList v) {
        if (!reference_set.contains(v)) {
            return null;
        }
        double fms = 1;

        double sum_tc = sum_tc(u, v);

        double res = sum_tc / sum_of_weights(u);

        if (res > 1) {
            res = 1.0;
        }

        return fms - res;
    }

    private static Double sum_tc(ArrayList u, ArrayList v) {
        double transforming_costs = 0;

        Iterator it = u.iterator();
        Iterator it2 = v.iterator();
        it.next();
        it2.next();
        int attr = 0;

        BasicTokenizer tokenizer = new BasicTokenizer();

        while (it.hasNext()) {
            String current_input_attribute = (String) it.next();
            String current_reference_attribute = (String) it2.next();

            String[] u_tokens = tokenizer.tokenize(current_input_attribute);
            String[] v_tokens = tokenizer.tokenize(current_reference_attribute);

            transforming_costs += transforming_cost(u_tokens, v_tokens, attr);
            attr++;
        }

        return transforming_costs;
    }

    private static Double transforming_cost(String[] u_tokens, String[] v_tokens, int attr) {
        HashMap<String, Double> idf = idfs.get(attr);

        double temp[][] = new double[u_tokens.length + 1][v_tokens.length + 1];

        temp[0][0] = 0.0;
        for (int i = 1; i < v_tokens.length + 1; i++) {
            temp[0][i] = 0.5 * idf.get(v_tokens[i - 1]);
        }

        for (int i = 1; i < u_tokens.length + 1; i++) {
            try {
                temp[i][0] = idf.get(u_tokens[i - 1]);
            } catch (NullPointerException ex) {
                temp[i][0] = average_weights.get(attr);
            }
        }

        for (int i = 1; i <= u_tokens.length; i++) {
            for (int j = 1; j <= v_tokens.length; j++) {
                if (u_tokens[i - 1].equals(v_tokens[j - 1])) {
                    temp[i][j] = temp[i - 1][j - 1];
                } else {
                    temp[i][j] = min(
                            temp[i][j - 1] + 0.5 * idf.get(v_tokens[j - 1]),
                            temp[i - 1][j] + idf.get(v_tokens[j - 1]),
                            temp[i - 1][j - 1] + EditDistance.calculate(u_tokens[i - 1], v_tokens[j - 1]) * idf.get(v_tokens[j - 1])
                    );
                }
            }
        }
        return temp[u_tokens.length][v_tokens.length];

    }

    private static double min(double a, double b, double c) {
        double l = Math.min(a, b);
        return Math.min(l, c);
    }

    private Double sum_of_weights(ArrayList u) {
        double sum = 0;

        Iterator it = u.iterator();
        it.next();
        int attr = 0;

        while (it.hasNext()) {
            String current_input_attribute = (String) it.next();

            BasicTokenizer tokenizer = new BasicTokenizer();
            String[] u_tokens = tokenizer.tokenize(current_input_attribute);
            if (u_tokens != null) {
                for (int i = 0; i < u_tokens.length; i++) {
                    if (idfs.get(attr).containsKey(u_tokens[i])) {
                        sum += idfs.get(attr).get(u_tokens[i]);
                    } else {
                        sum += 1;
                    }
                }
            } else {
                sum += 1;
            }
            attr++;
        }
        return sum;
    }

    private void calculate_idfs_per_column() {
        idfs = new ArrayList();

        for (int j = 1; j < reference_set.get(0).size(); j++) {
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
