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
package no.hials.jiop.base.candidates.containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import no.hials.jiop.base.candidates.Candidate;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class CandidateArrayContainer<E> extends CandidateContainer<E> {

    private  Candidate<E>[] candidates;

    public CandidateArrayContainer(int size) {
        super(size);
        this.candidates = new Candidate[size];
    }
    
    @Override
    public List<Candidate<E>> getCandidates() {
        List<Candidate<E>> list = new ArrayList<>(candidates.length);
        list.addAll(Arrays.asList(candidates));
        return list;
    }

    @Override
    public CandidateContainer<E> sort() {
        Arrays.sort(candidates);
        return this;
    }

    @Override
    public Candidate<E> get(int i) {
        return candidates[i];
    }

    @Override
    public void set(int index, Candidate<E> candidate) {
        candidates[index] = candidate;
    }

    @Override
    public int indexOf(Candidate<E> candidate) {
        for (int i = 0; i < candidates.length; i++) {
            if (get(i) == candidate) {
                return i;
            }
        }
        throw new IllegalStateException("No such Candidate in the container!");
    }

    @Override
    public Iterator<Candidate<E>> iterator() {
        return new MyIterator();
    }

    @Override
    public void clearAndAddAll(List<Candidate<E>> candidates) {
        if (candidates.size() != size) {
            throw new IllegalArgumentException("");
        }
        this.candidates = candidates.toArray(new Candidate[candidates.size()]);
    }


    private class MyIterator implements Iterator<Candidate<E>> {

        private int i = 0;

        @Override
        public boolean hasNext() {
            return i != candidates.length;
        }

        @Override
        public Candidate<E> next() {
            return candidates[i++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
