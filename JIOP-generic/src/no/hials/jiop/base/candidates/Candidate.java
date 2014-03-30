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
package no.hials.jiop.base.candidates;

import no.hials.jiop.base.candidates.encoding.Encoding;

/**
 * Candidate class, holds an Encoding instance and an affiliated cost
 * @author Lars Ivar Hatledal
 * @param <E> the type
 */
public class Candidate<E> implements Comparable<Candidate<E>>{

    private final Encoding<E> encoding;
    private double cost;

    /**
     * Creates a new Candidate with the given encoding and cost
     * @param encoding the Encoding
     * @param cost the cost
     */
    public Candidate(Encoding<E> encoding, double cost) {
        this.encoding = encoding;
        this.cost = cost;
    }
    
    /**
     * Creates a new Candidate based on the given Candidate (copies)
     * @param candidate the candiate to copy
     */
    public Candidate(Candidate<E> candidate) {
        this(candidate.encoding.copy(), candidate.cost);
    }

    /**
     * Getter for the Encoding
     * @return the encoding
     */
    public Encoding<E> getEncoding() {
        return encoding;
    }

    /**
     * Getter for the variables.
     * This call is the same as getEncoding().getVariables();
     * @return the variables
     */
    public E getVariables() {
        return encoding.getVariables();
    }

    /**
     * Getter for the cost
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Setter for the cost
     * @param cost the cost of this candidate
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(Candidate<E> c) {
       if (getCost() == c.getCost()) {
           return 0;
       } else if (getCost() < c.getCost()) {
           return -1;
       } else {
           return 1;
       }
    }

    @Override
    public String toString() {
        return "Candidate{" + "cost=" + cost +  ", encoding=" + encoding + '}';
    }

}
