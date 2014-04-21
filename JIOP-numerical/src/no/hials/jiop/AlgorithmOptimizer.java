/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.io.Serializable;
import no.hials.utilities.DoubleArray;

/**
 *
 * @author LarsIvar
 */
public class AlgorithmOptimizer implements Serializable {

    public final Algorithm algorithm;
    public final Algorithm optimizable;

    public AlgorithmOptimizer(Algorithm optimizable) {
        this.optimizable = optimizable;
        this.algorithm = new SimulatedAnnealing(20, 0.95, optimizable.getNumberOfFreeParameters(), new OptimizerEvalutor());
    }

    public AlgorithmOptimizer(Algorithm algorithm, Algorithm optimizable) {
        this.algorithm = algorithm;
        this.optimizable = optimizable;
    }

    public SolutionData optimize(double error, long timeOut) {
        SolutionData compute = algorithm.compute(error, timeOut);
        optimizable.init();
        return compute;
    }

    private class OptimizerEvalutor implements Evaluator {

        @Override
        public double evaluate(DoubleArray array) {
            double cost = 0;
            optimizable.setFreeParameters(array);
            for (int i = 0; i < 5; i++) {
                optimizable.init();
                cost += optimizable.compute(50).cost;
            }
            return cost;
        }

    }

}
