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
package no.hials.jiop.generic.tuning;

import java.io.Serializable;
import java.util.Arrays;
import no.hials.jiop.generic.Algorithm;
import no.hials.jiop.generic.Evaluator;
import no.hials.jiop.generic.candidates.CandidateSolution;
import no.hials.jiop.generic.evolutionary.de.DifferentialEvolution;
import no.hials.jiop.generic.factories.DoubleArrayCandidateFactory;
import no.hials.jiop.generic.temination.CostCriteria;
import no.hials.jiop.generic.temination.TimeElapsedCriteria;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class AlgorithmOptimizer implements Serializable {

    public final Algorithm algorithm;

    public AlgorithmOptimizer() {
        this.algorithm = new DifferentialEvolution(15, 0.8, 0.9, new DoubleArrayCandidateFactory(), null, true);
    }

    public AlgorithmOptimizer(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void optimize(Optimizable optimizable, double cost, long timeOut) {
        System.out.println("Optimizing " + optimizable);
        this.algorithm.setEvaluator(new OptimizerEvalutor(optimizable));
        this.algorithm.init();
        CandidateSolution compute = algorithm.compute(new CostCriteria(cost), new TimeElapsedCriteria(timeOut));
        Algorithm alg = (Algorithm) optimizable;
        alg.clearHistory();
        optimizable.setFreeParameters((double[]) compute.getSolution().getElements());
        System.out.println("Result: ");
        System.out.println(Arrays.toString(optimizable.getFreeParameters()));
    }

    private class OptimizerEvalutor extends Evaluator<double[]> {
        
        private final Optimizable optimizable;

        public OptimizerEvalutor(Optimizable optimizable) {
            super(optimizable.getNumberOfFreeParameters());
            this.optimizable = optimizable;
        }
        
        @Override
        public double getCost(double[] array) {
            double cost = 0;
            optimizable.setFreeParameters(array);
            for (int i = 0; i < 5; i++) {
                optimizable.init();
                CandidateSolution compute = optimizable.compute(new TimeElapsedCriteria(50l));
                cost += compute.getSolution().getCost();
            }
            return cost;
        }
    }

}
