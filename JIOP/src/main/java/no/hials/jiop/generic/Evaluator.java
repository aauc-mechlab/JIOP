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

package no.hials.jiop.generic;

import java.util.List;
import no.hials.jiop.generic.candidates.Candidate;


/**
 * Interface used for evaluating performance of the candidates
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public abstract class Evaluator<E> {
    private final int dimension;

    /**
     * Default constructor
     * @param dimension the problem dimensionality
     */
    public Evaluator(int dimension) {
        this.dimension = dimension;
    }

    /**
     * Getter for the problem dimensionality
     * @return the problem dimensionality
     */
    public int getDimension() {
        return dimension;
    }
    
    /**
     * Evaluates the candidate
     * @param candidate the candidate to evaluate
     * @return the same candiate instance, where the cost have been updated
     */
    public Candidate<E> evaluate(Candidate<E> candidate) {
        candidate.setCost(getCost(candidate.getElements()));
        return candidate;
    }
    
    /**
     * Evaluates all the candidates in the list
     * @param candidates the candidates to evaluate
     * @return the updated candidates
     */
    public List<Candidate<E>> evaluateAll(List<Candidate<E>> candidates) {
        for (Candidate<E> c : candidates) {
            evaluate(c);
        }
        return candidates;
    }
    
    /**
     * Calcualtes the cost of the element
     * @param elements the element calculate the cost of
     * @return the cost of the element
     */
    public abstract double getCost(E elements);
}
