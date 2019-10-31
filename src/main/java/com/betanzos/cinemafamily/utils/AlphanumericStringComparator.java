/**
 * Copyright 2019 Eduardo E. Betanzos Morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.betanzos.cinemafamily.utils;

import java.util.Comparator;

/**
 * @author Eduardo Betanzos
 * @since 1.0
 */
public class AlphanumericStringComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        char[] chars1 = o1.toString().toCharArray();
        char[] chars2 = o2.toString().toCharArray();

        char a;
        char b;
        int result = 0;

        for (int i = 0, j = 0; i < chars1.length && j < chars2.length; i++, j++) {
            a = chars1[i];
            b = chars2[j];

            if (Character.isDigit(a) && Character.isDigit(b)) {
                int[] compResult = compareNumSequence(i, chars1, j, chars2);

                if (compResult[0] != 0) {
                    result = compResult[0];
                    break;
                }

                i = compResult[1];
                j = compResult[2];
            } else if (a != b) {
                result = a - b;
                break;
            }
        }

        return result;
    }

    private int[] compareNumSequence(int index1, char[] arr1, int index2, char[] arr2) {
        var sb1 = new StringBuilder();
        var sb2 = new StringBuilder();

        while (index1 < arr1.length && Character.isDigit(arr1[index1])) {
            sb1.append(arr1[index1]);
            index1++;
        }

        while (index2 < arr2.length && Character.isDigit(arr2[index2])) {
            sb2.append(arr2[index2]);
            index2++;
        }

        int num1 = Integer.parseInt(sb1.toString());
        int num2 = Integer.parseInt(sb2.toString());

        return new int[] {num1 - num2, --index1, --index2};
    }
}
