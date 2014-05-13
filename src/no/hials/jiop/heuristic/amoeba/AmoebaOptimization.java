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
package no.hials.jiop.heuristic.amoeba;

import no.hials.jiop.Evaluator;
import no.hials.jiop.GeneralPopBasedAlgorithm;
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;
import no.hials.jiop.factories.NumericCandidateFactory;

/**
 * Amoeba Optimization based on an article by James McCaffrey:
 * http://msdn.microsoft.com/en-us/magazine/dn201752.aspx
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public class AmoebaOptimization<E> extends GeneralPopBasedAlgorithm<E> {

    private double alpha = 1.0;  // Reflection
    private double beta = 0.5;   // Contraction
    private double gamma = 2.0;  // Expansion


    public AmoebaOptimization(int size, NumericCandidateFactory<E> candidateFactory, Evaluator<E> evaluator) {
        super(size, candidateFactory, evaluator, "Amoeba Optimization");
    }

    @Override
    protected void singleIteration() {
        NumericCandidate<E> centroid = centroid();
        NumericCandidate<E> reflected = reflected(centroid);
        if (reflected.getCost() < getPopulation().get(0).getCost()) {
            NumericCandidate<E> expanded = expanded(reflected, centroid);
            if (expanded.getCost() < getPopulation().get(0).getCost()) {
                replaceWorst(expanded);
            } else {
                replaceWorst(reflected);
            }
            setBestCandidateIfBetter(getPopulation().get(0));
            return;
        }
        if (isWorseThanAllButWorst(reflected) == true) {
            if (reflected.getCost() <= getPopulation().get(size() - 1).getCost()) {
                replaceWorst(reflected);
            }
            NumericCandidate<E> contracted = contracted(centroid);
            if (contracted.getCost() > getPopulation().get(size() - 1).getCost()) {
                shrink();
            } else {
                replaceWorst(contracted);
            }
            setBestCandidateIfBetter(getPopulation().get(0));
            return;
        }
        replaceWorst(reflected);
        setBestCandidateIfBetter(getPopulation().get(0));
    }

    public NumericCandidate<E> centroid() {
        NumericCandidate<E> c = (NumericCandidate<E>) randomCandidate();
        for (int i = 0; i < size() - 1; ++i) {
            for (int j = 0; j < getDimension(); ++j) {
                c.set(j, c.get(j).doubleValue() + ((NumericCandidate<E>)getPopulation().get(i)).get(j).doubleValue());
            }
        }
        // Accumulate sum of each component
        for (int j = 0; j < getDimension(); ++j) {
            c.set(j, c.get(j).doubleValue() / (size() - 1));
        }
        c.clamp(0, 1);
        evaluate(c);
        return c;
    }

    public NumericCandidate<E> reflected(NumericCandidate<E> centroid) {
        NumericCandidate<E> r = (NumericCandidate<E>) randomCandidate();
        NumericCandidate<E> worst = (NumericCandidate<E>) getPopulation().get(size() - 1);
        for (int j = 0; j < getDimension(); ++j) {
            r.set(j, ((1 + alpha) * centroid.get(j).doubleValue()) - (alpha * worst.get(j).doubleValue()));
        }
        r.clamp(0, 1);
        evaluate(r);
        return r;
    }

    public NumericCandidate<E> expanded(NumericCandidate<E> reflected, NumericCandidate<E> centroid) {
        NumericCandidate<E> e = (NumericCandidate<E>) randomCandidate();
        for (int j = 0; j < getDimension(); ++j) {
            e.set(j, (gamma * reflected.get(j).doubleValue()) + ((1 - gamma) * centroid.get(j).doubleValue()));
        }
        e.clamp(0, 1);
        evaluate(e);
        return e;
    }

    public NumericCandidate<E> contracted(NumericCandidate<E> centroid) {
        NumericCandidate<E> v = (NumericCandidate<E>) randomCandidate();
        NumericCandidate<E> worst = (NumericCandidate<E>) getPopulation().get(size() - 1);
        for (int j = 0; j < getDimension(); ++j) {
            v.set(j, (beta * worst.get(j).doubleValue()) + ((1 - beta) * centroid.get(j).doubleValue()));
        }
        v.clamp(0, 1);
        evaluate(v);
        return v;
    }

    public void replaceWorst(Candidate<E> newSolution) {
        getPopulation().set(size() - 1,  newSolution.copy());
        sortCandidates();
    }

    public void shrink() {
        for (int i = 1; i < size(); ++i) // start at [1]
        {
            for (int j = 0; j < getDimension(); ++j) {
                double value = (((NumericCandidate<E>)getPopulation().get(i)).get(j).doubleValue() + ((NumericCandidate<E>)getPopulation().get(0)).get(j).doubleValue()) / 2d;
                if (value < 0) {
                    value = 0;
                } else if (value > 1) {
                    value = 1;
                }
                ((NumericCandidate<E>)getPopulation().get(i)).set(j, value);
            }
            evaluate(getPopulation().get(i));
        }
        sortCandidates();
    }

    public boolean isWorseThanAllButWorst(NumericCandidate<E> reflected) {
        for (int i = 0; i < size() - 1; ++i) {
            if (reflected.getCost() <= getPopulation().get(i).getCost()) // Found worse solution
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
}
