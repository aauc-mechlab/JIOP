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
package no.hials.jiop;
import no.hials.utilities.DoubleArray;

/**
 *
 * @author LarsIvar
 */
public class Candidate extends DoubleArray implements Comparable<Candidate> {

    private double cost;

    public Candidate(DoubleArray array) {
        this(array, Double.MAX_VALUE);
    }

    protected Candidate(Candidate candidate) {
        super(candidate);
        this.cost = candidate.getCost();
    }

    public Candidate(DoubleArray array, double cost) {
        super(array.getArray());
        this.cost = cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setVariables(double[] variables) {
        super.array = variables;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public Candidate copy() {
        return new Candidate(this);
    }

    @Override
    public int compareTo(Candidate c) {
        if (this.cost == c.getCost()) {
            return 0;
        } else if (this.cost < c.getCost()) {
            return -1;
        } else {
            return 1;
        }
    }

    public static Candidate randomCandidate(int length, Evaluator eval) {
        DoubleArray random = DoubleArray.random(length);
        return new Candidate(random, eval.evaluate(random));
    }
    
    public static Candidate neighborCandidate(Candidate candidate, double factor, Evaluator eval) {
        DoubleArray array = DoubleArray.neighbor(candidate, factor);
        Candidate neighbor = new Candidate(array, eval.evaluate(array));
        neighbor.clamp(0, 1);
        return neighbor;
    }

}
