/*
 * Copyright (c) 2014, Lars Ivar
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
package no.hials.jiop.base.candidates.containers;

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.base.candidates.Candidate;

/**
 *
 * @author Lars Ivar
 */
public abstract class CandidateContainer<E> implements Iterable<Candidate<E>> {

    protected final int size;

    public CandidateContainer(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }

    public abstract List<Candidate<E>> getCandidates();

    public abstract CandidateContainer<E> sort();

    public abstract Candidate<E> get(int i);

    public abstract void set(int index, Candidate<E> candidate);

    public abstract int indexOf(Candidate<E> candidate);

    public abstract void clearAndAddAll(List<Candidate<E>> candidates);

    public double getAverage() {
        double avg = 0;
        for (Candidate<E> c : this) {
            avg += c.getCost();
        }
        return avg / size();
    }

    public List<Candidate<E>> getBestCandidates(int numBest) {
        if (numBest > size) {
            throw new IllegalArgumentException("");
        }
        List<Candidate<E>> elites = new ArrayList<>(numBest);
        for (int i = 0; i < numBest; i++) {
            elites.add(new Candidate<>(get(i)));
        }
        return elites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Candidate c : this) {
            sb.append(c);
            if (i++ != size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
