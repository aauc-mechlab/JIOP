package no.hials.jiop;

/* Copyright (c) 2013 the authors listed at the following URL, and/or
 2 the authors of referenced articles or incorporated external code:
 3 http://en.literateprograms.org/Permutations_with_repetition_(Java)?action=history&offset=20080109002711
 4
 5 Permission is hereby granted, free of charge, to any person obtaining
 6 a copy of this software and associated documentation files (the
 7 "Software"), to deal in the Software without restriction, including
 8 without limitation the rights to use, copy, modify, merge, publish,
 9 distribute, sublicense, and/or sell copies of the Software, and to
 10 permit persons to whom the Software is furnished to do so, subject to
 11 the following conditions:
 12
 13 The above copyright notice and this permission notice shall be
 14 included in all copies or substantial portions of the Software.
 15
 16 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 17 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 18 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 19 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 20 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 21 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 22 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 23
 24 Retrieved from: http://en.literateprograms.org/Permutations_with_repetition_(Java)?oldid=11971
 25 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PermutationsWithRepetition implements Serializable{

    private String a;
    private int n;

    public PermutationsWithRepetition(String a, int n) {
        this.a = a;
        this.n = n;
    }

    public List<String> getVariations() {
        // split string
        String[] temp = a.split("-");
        int l = temp.length;
        int permutations = (int) Math.pow(l, n);
        String[][] table = new String[permutations][n];

        for (int x = 0; x < n; x++) {
            int t2 = (int) Math.pow(l, x);
            for (int p1 = 0; p1 < permutations;) {
                for (int al = 0; al < l; al++) {
                    for (int p2 = 0; p2 < t2; p2++) {
                        table[p1][x] = temp[al];
                        p1++;
                    }
                }
            }
        }

        List<String> result = new ArrayList<>();
        for (String[] permutation : table) {
            String tmp = "";
            for (String s : permutation) {
                tmp += s + ":";
            }
            result.add(tmp);
        }
        return result;
    }
}
