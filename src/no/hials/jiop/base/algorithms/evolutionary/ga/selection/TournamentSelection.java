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
package no.hials.jiop.evolutionary.ga.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.evolutionary.ga.SelectionOperator;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class TournamentSelection<E> implements SelectionOperator<E> {

    private final Random rng = new Random();

    private final double k;

    public TournamentSelection(double k) {
        this.k = k;
    }

    @Override
    public List<Candidate<E>> selectCandidates(List<Candidate<E>> candidates, int numSelections) {

        List<Candidate<E>> selectedCandidates = new ArrayList<>(numSelections);
        List<Candidate<E>> selectionPool = new ArrayList<>(candidates);

        for (int i = 0; i < numSelections; i++) {
            Candidate<E> c1, c2;
            do {
                c1 = candidates.get(rng.nextInt(selectionPool.size()));
                c2 = candidates.get(rng.nextInt(selectionPool.size()));
            } while (c1 == c2);

            Candidate<E> winner;
            if (k >= rng.nextDouble()) {
                winner = c1.getCost() < c2.getCost() ? c1 : c2;
            } else {
                winner = c1.getCost() < c2.getCost() ? c2 : c1;
            }
            selectionPool.remove(winner);
            selectedCandidates.add(new Candidate<>(winner));
        }
        return selectedCandidates;
    }

}
