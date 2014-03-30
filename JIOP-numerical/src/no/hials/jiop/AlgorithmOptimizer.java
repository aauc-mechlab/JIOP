/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import no.hials.jiop.swarm.MultiSwarmOptimization;

/**
 *
 * @author LarsIvar
 */
public class AlgorithmOptimizer {

    private final Algorithm algorithm;
    private final Algorithm optimizable;

    public AlgorithmOptimizer(Algorithm optimizable) {
        this.optimizable = optimizable;
        this.optimizable.init();
        this.algorithm = new DifferentialEvolution(20, 0.9, 0.8, optimizable.getNumberOfFreeParameters(), new OptimizerEvalutor(), false);
        this.algorithm.init();
    }

    public AlgorithmOptimizer(Algorithm algorithm, Algorithm optimizable) {
        this.algorithm = algorithm;
        this.algorithm.init();
        this.optimizable = optimizable;
        this.optimizable.init();
    }

    public DoubleArray optimize() {
        SolutionData compute = algorithm.compute(20000l);
        System.out.println(compute);
        return compute.solution;
    }

    private class OptimizerEvalutor implements Evaluator {

        @Override
        public double evaluate(DoubleArray array) {
            double cost = 0;
            optimizable.setFreeParameters(array);
            cost += optimizable.compute(50).cost;
            return cost;
        }

    }

}
