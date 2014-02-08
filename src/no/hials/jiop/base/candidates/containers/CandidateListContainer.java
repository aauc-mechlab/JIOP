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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import no.hials.jiop.base.candidates.Candidate;

/**
 *
 * @author Lars Ivar
 */
public class CandidateListContainer<E> extends CandidateContainer<E> {

    private List<Candidate<E>> candidates;

    public CandidateListContainer(int size) {
        super(size);
        this.candidates = new ArrayList<>(size);
    }

    @Override
    public List<Candidate<E>> getCandidates() {
        return candidates;
    }

    @Override
    public void set(int index, Candidate<E> candidate) {
        candidates.set(index, candidate);
    }

    @Override
    public Candidate<E> get(int i) {
        return candidates.get(i);
    }

    @Override
    public int indexOf(Candidate<E> candidate) {
        return candidates.indexOf(candidate);
    }

    @Override
    public CandidateContainer<E> sort() {
        Collections.sort(candidates);
        return this;
    }

    @Override
    public void clearAndAddAll(List<Candidate<E>> candidates) {
        if (candidates.size() != size) {
            throw new IllegalArgumentException("Expected " +size + " candidates! Was: " + candidates.size());
        }
        this.candidates = candidates;
    }

    @Override
    public Iterator<Candidate<E>> iterator() {
        return candidates.iterator();
    }

}
