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
package no.hials.jiop.heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.jiop.Algorithm;
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;

/**
 * Amoeba Optimization based on an article by James McCaffrey:
 * http://msdn.microsoft.com/en-us/magazine/dn201752.aspx
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public class AmoebaOptimization<E> extends Algorithm<E> {

    private int size;
    private double alpha = 1.0;  // Reflection
    private double beta = 0.5;   // Contraction
    private double gamma = 2.0;  // Expansion

    private Amoeba candidates;

    public AmoebaOptimization(Class<?> clazz, int size) {
        super(clazz, "Amoeba Optimization");
        this.size = size;
    }

    @Override
    public Candidate<E> subInit() {
        this.candidates = new Amoeba(size);
        Collections.sort(candidates);
        return candidates.get(0);
    }

    @Override
    public Candidate<E> subInit(List<E> seeds) {
        this.candidates = new Amoeba(size - seeds.size());
        for (E seed : seeds) {
            candidates.add((NumericCandidate< E>) newCandidate(seed));
        }
        Collections.sort(candidates);
        return candidates.get(0);
    }

    @Override
    protected void singleIteration() {
        NumericCandidate<E> centroid = centroid();
        NumericCandidate<E> reflected = reflected(centroid);
        if (reflected.getCost() < candidates.get(0).getCost()) {
            NumericCandidate<E> expanded = expanded(reflected, centroid);
            if (expanded.getCost() < candidates.get(0).getCost()) {
                replaceWorst(expanded);
            } else {
                replaceWorst(reflected);
            }
            setBestCandidateIfBetter(candidates.get(0));
            return;
        }
        if (isWorseThanAllButWorst(reflected) == true) {
            if (reflected.getCost() <= candidates.get(size - 1).getCost()) {
                replaceWorst(reflected);
            }
            NumericCandidate<E> contracted = contracted(centroid);
            if (contracted.getCost() > candidates.get(size - 1).getCost()) {
                shrink();
            } else {
                replaceWorst(contracted);
            }
            setBestCandidateIfBetter(candidates.get(0));
            return;
        }
        replaceWorst(reflected);
        setBestCandidateIfBetter(candidates.get(0));
    }

    public NumericCandidate<E> centroid() {
        NumericCandidate<E> c = (NumericCandidate<E>) newCandidate();
        for (int i = 0; i < size - 1; ++i) {
            for (int j = 0; j < getDimension(); ++j) {
                c.set(j, c.get(j).doubleValue() + candidates.get(i).get(j).doubleValue());
            }
        }
        // Accumulate sum of each component
        for (int j = 0; j < getDimension(); ++j) {
            c.set(j, c.get(j).doubleValue() / (size - 1));
        }
        c.clamp(0, 1);
        evaluateAndUpdate(c);
        return c;
    }

    public NumericCandidate<E> reflected(NumericCandidate<E> centroid) {
        NumericCandidate<E> r = (NumericCandidate<E>) newCandidate();
        NumericCandidate<E> worst = candidates.get(size - 1);
        for (int j = 0; j < getDimension(); ++j) {
            r.set(j, ((1 + alpha) * centroid.get(j).doubleValue()) - (alpha * worst.get(j).doubleValue()));
        }
        r.clamp(0, 1);
        evaluateAndUpdate(r);
        return r;
    }

    public NumericCandidate<E> expanded(NumericCandidate<E> reflected, NumericCandidate<E> centroid) {
        NumericCandidate<E> e = (NumericCandidate<E>) newCandidate();
        for (int j = 0; j < getDimension(); ++j) {
            e.set(j, (gamma * reflected.get(j).doubleValue()) + ((1 - gamma) * centroid.get(j).doubleValue()));
        }
        e.clamp(0, 1);
        evaluateAndUpdate(e);
        return e;
    }

    public NumericCandidate<E> contracted(NumericCandidate<E> centroid) {
        NumericCandidate<E> v = (NumericCandidate<E>) newCandidate();
        NumericCandidate<E> worst = candidates.get(size - 1);
        for (int j = 0; j < getDimension(); ++j) {
            v.set(j, (beta * worst.get(j).doubleValue()) + ((1 - beta) * centroid.get(j).doubleValue()));
        }
        v.clamp(0, 1);
        evaluateAndUpdate(v);
        return v;
    }

    public void replaceWorst(NumericCandidate<E> newSolution) {
        candidates.set(size - 1, (NumericCandidate<E>) (newSolution)).copy();
        Collections.sort(candidates);
    }

    public void shrink() {
        for (int i = 1; i < size; ++i) // start at [1]
        {
            for (int j = 0; j < getDimension(); ++j) {
                double value = (candidates.get(i).get(j).doubleValue() + candidates.get(0).get(j).doubleValue()) / 2d;
                if (value < 0) {
                    value = 0;
                } else if (value > 1) {
                    value = 1;
                }
                candidates.get(i).set(j, value);
            }
            evaluateAndUpdate(candidates.get(i));
        }
        Collections.sort(candidates);
    }

    public boolean isWorseThanAllButWorst(NumericCandidate<E> reflected) {
        for (int i = 0; i < size - 1; ++i) {
            if (reflected.getCost() <= candidates.get(i).getCost()) // Found worse solution
            {
                return false;
            }
        }
        return true;
    }

//    @Override
//    public int getNumberOfFreeParameters() {
//        return 4;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(size, alpha, beta, gamma);
//    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    private class Amoeba extends ArrayList<NumericCandidate<E>> {

        public Amoeba(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add((NumericCandidate<E>) newCandidate());
            }
        }
    }
}
