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
package no.hials.jiop.tuning;

import java.io.Serializable;
import no.hials.jiop.AbstractAlgorithm;
import no.hials.jiop.Evaluator;
import no.hials.jiop.candidates.CandidateSolution;
import no.hials.jiop.candidates.DoubleArrayCandidate;
import no.hials.jiop.evolutionary.de.DifferentialEvolution;
import no.hials.jiop.temination.CostCriteria;
import no.hials.jiop.temination.TimeElapsedCriteria;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class AlgorithmOptimizer implements Serializable {

    public final AbstractAlgorithm algorithm;
    public final Optimizable optimizable;

    public AlgorithmOptimizer(Optimizable optimizable) {
        this.optimizable = optimizable;
        this.algorithm = new DifferentialEvolution(DoubleArrayCandidate.class, 15, 0.8, 0.9, new OptimizerEvalutor(), true);
    }

    public AlgorithmOptimizer(AbstractAlgorithm algorithm, Optimizable optimizable) {
        this.algorithm = algorithm;
        this.optimizable = optimizable;
    }

    public CandidateSolution optimize(double cost, long timeOut) {
        this.algorithm.init();
        CandidateSolution compute = algorithm.compute(new CostCriteria(cost), new TimeElapsedCriteria(timeOut));
        AbstractAlgorithm alg = (AbstractAlgorithm) optimizable;
        alg.clearHistory();
        return compute;
    }

    private class OptimizerEvalutor implements Evaluator<double[]> {

        @Override
        public double evaluate(double[] array) {
            double cost = 0;
            optimizable.setFreeParameters(array);
            for (int i = 0; i < 5; i++) {
                optimizable.init();
                CandidateSolution compute = optimizable.compute(new CostCriteria(0), new TimeElapsedCriteria(50l));
//                cost += compute.millis;
                cost += compute.cost;
            }
            return cost;
        }

        @Override
        public int getDimension() {
           return optimizable.getNumberOfFreeParameters();
        }
    }

}
