/*
 * Copyright (c) 2014, Aalesund University College 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package no.hials.jiop.evolutionary.ga;

import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import no.hials.jiop.base.candidates.Candidate;

/**
 *
 * @author Lars Ivar Hatledal
 */
public abstract class AbstractMutatationOperator<E> implements MutationOperator<E> {

    private final Random rng = new Random();

    @Override
    public void mutateCandidates(List<Candidate<E>> candidates, int numMutations) {
//        System.out.println(candidates.size() + " " + numMutations);
        TreeSet<Integer> rows = new TreeSet<>();
        for (int i = 0; i < numMutations; i++) {
            int row;
            do {
                row = rng.nextInt(candidates.size());
            } while (rows.contains(row));
            rows.add(row);
        }
        for (int i = 0; i < numMutations; i++) {
            Candidate<E> get = candidates.get(rows.pollFirst());
            int c = rng.nextInt(get.getEncoding().size());
            mutate(get, c);

        }
    }

    public abstract void mutate(Candidate<E> chromosome, int geneIndex);
}
