/*
 * Copyright 2018 vasgat.
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

/**
 *
 * @author vasgat
 */
public class OCSearchTermFormatter {

    public static String format(String search_term) {
        if (search_term.contains("inc") && !search_term.contains("incorporated")) {
            return search_term.replace("inc", "incorporated")
                    .replace("incorporated.", "incorporated").trim();
        } else if (search_term.contains("incorporated")) {
            return search_term.replace("incorporated", "inc").trim();
        } else if (search_term.contains("corporation")) {
            return search_term.replace("corporation", "corp").trim();
        } else if (search_term.startsWith("the ")) {
            return search_term.replaceAll("^the ", "").trim();
        } else {
            return search_term;
        }
    }

    public static String removeType(String search_term) {
        return search_term.replaceAll("\\bincorporated\\b", "")
                .replaceAll("\\binc\\b", "")
                .replaceAll("\\bcorporation\\b", "")
                .replaceAll("\\bcorp\\b", "")
                .replaceAll("\\blimited\\b", "")
                .replaceAll("\\bltd\\b", "")
                .replaceAll("\\bplc\\b", "")
                .replaceAll("\\blp\\b", "")
                .replaceAll("\\bco\\b", "")
                .replaceAll("\\bcompany\\b", "")
                .replace(".", "")
                .trim();
    }
}
