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
package company.data.integration.similarityfunctions;

/**
 *
 * @author vasgat
 */
public class EditDistance {

    /**
     * Uses bottom up Dynamic Programming to find the edit distance
     */
    public static Double calculate(String string1, String string2) {        
        char[] str1 = string1.toCharArray();
        char[] str2 = string2.toCharArray();
        
        int temp[][] = new int[str1.length + 1][str2.length + 1];

        for (int i = 0; i < temp[0].length; i++) {
            temp[0][i] = i;
        }

        for (int i = 0; i < temp.length; i++) {
            temp[i][0] = i;
        }

        for (int i = 1; i <= str1.length; i++) {
            for (int j = 1; j <= str2.length; j++) {
                if (str1[i - 1] == str2[j - 1]) {
                    temp[i][j] = temp[i - 1][j - 1];
                } else {
                    temp[i][j] = 1 + min(temp[i - 1][j - 1], temp[i - 1][j], temp[i][j - 1]);
                }
            }
        }

        return temp[str1.length][str2.length] * 1.0 / Math.max(str1.length, str2.length);

    }

    private static int min(int a, int b, int c) {
        int l = Math.min(a, b);
        return Math.min(l, c);
    }
}
