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
package no.hials.jiop.candidates;

import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class GeneralCandidate<E> implements Candidate<E> {

    protected final Random rng = new Random();

    protected double cost = Double.MAX_VALUE;
    protected E elements;

    public GeneralCandidate(int length) {
        this.elements = randomElements(length);
    }

    public GeneralCandidate(E elements) {
        this.elements = elements;
    }

    public GeneralCandidate(E elements, double cost) {
        this.elements = elements;
        this.cost = cost;
    }

    @Override
    public void setElements(E elements, double cost) {
        this.elements = elements;
        this.cost = cost;
    }

    @Override
    public  double getCost() {
        return cost;
    }

    @Override
    public  void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Get the elements in the structure. Please note that it is crucial that
     * the generic type E is a collection or arn array of some sort.
     *
     * @return the elements in the structure
     */
    @Override
    public E getElements() {
        return elements;
    }

    /**
     * Prints each element in the structure, separated by the delimiter string
     *
     * @param delimiter the separation string
     * @return a string representation of this Object
     */
    public String toString(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i != size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Candidate c) {
        if (getCost() == c.getCost()) {
            return 0;
        } else if (getCost() < c.getCost()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public Iterator iterator() {
        return new CandidateIterator();
    }

    /**
     * In iterator for Structures
     *
     * @author Lars Ivar Hatledal
     */
    public class CandidateIterator implements Iterator {

        private int index;

        @Override
        public boolean hasNext() {
            return index != size();
        }

        @Override
        public Object next() {
            return get(index++);
        }

    }

}
