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

import java.util.Comparator;

/**
 *
 * @author vasgat
 */
public class LengthComparator implements Comparator<String> {

    public LengthComparator() {
        super();
    }

    public int compare(String s1, String s2) {
        return s2.length() - s1.length();
    }
}
