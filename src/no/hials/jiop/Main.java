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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import no.hials.jiop.candidates.CandidateSolution;
import no.hials.jiop.candidates.DoubleArrayBacteriaCandidate;
import no.hials.jiop.candidates.DoubleArrayCandidate;
import no.hials.jiop.candidates.DoubleArrayParticleCandidate;
import no.hials.jiop.evolutionary.de.DifferentialEvolution;
import no.hials.jiop.heuristic.AmoebaOptimization;
import no.hials.jiop.physical.SimulatedAnnealing;
import no.hials.jiop.swarm.ArtificialBeeColony;
import no.hials.jiop.swarm.BacterialForagingOptimization;
import no.hials.jiop.swarm.MultiSwarmOptimization;
import no.hials.jiop.swarm.ParticleSwarmOptimization;
import no.hials.jiop.temination.TimeElapsedCriteria;
import no.hials.jiop.tuning.AlgorithmOptimizer;
import no.hials.utilities.NormUtil;

/**
 * Main class
 *
 * @author Lars Ivar Hatledal
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        Evaluator<float[]> feval = new Feval(5);

        Evaluator<double[]> deval = new Deval(5);
        AlgorithmCollection<double[]> algorithms = new AlgorithmCollection<>();

        algorithms.add(new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, deval, false));
        algorithms.add(new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, deval, true));
//        algorithms.add(new ParticleSwarmOptimization(DoubleArrayParticleCandidate.class, 40, deval, false));
//        algorithms.add(new ParticleSwarmOptimization(DoubleArrayParticleCandidate.class, 40, deval, true));
//        algorithms.add(new MultiSwarmOptimization(DoubleArrayParticleCandidate.class, 5, 30, deval, false));
//        algorithms.add(new MultiSwarmOptimization(DoubleArrayParticleCandidate.class, 5, 30, deval, true));
//        algorithms.add(new ArtificialBeeColony(DoubleArrayCandidate.class, 60, 12, deval));
//        algorithms.add(new AmoebaOptimization(DoubleArrayCandidate.class, 50, deval));
//        algorithms.add(new SimulatedAnnealing(DoubleArrayCandidate.class, 20, 0.995, deval));
//        algorithms.add(new BacterialForagingOptimization(DoubleArrayBacteriaCandidate.class, 100, deval, false));
//        algorithms.add(new BacterialForagingOptimization(DoubleArrayBacteriaCandidate.class, 100, deval, true));
//        DifferentialEvolution de = new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, deval, "DE - Optimized", false);
//        CandidateSolution optimize = new AlgorithmOptimizer(de).optimize(0, 20000);
//        de.setFreeParameters((double[]) optimize.solution.getElements());
//        System.out.println(Arrays.toString(de.getFreeParameters()));
//        algorithms.add(de);

        algorithms.warmUp(100l);
        algorithms.computeAll(new TimeElapsedCriteria(100l));
        algorithms.plotResults();

    }

    public static class Deval implements Evaluator<double[]> {

        private final int dimension;

        public Deval(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public double evaluate(double[] elements) {
            double cost = 0;
            for (int i = 0; i < elements.length; i++) {
                double xi = new NormUtil(1, 0, 10, -10).normalize(elements[i]);
                cost += (xi * xi) - (10 * Math.cos(2 * Math.PI * xi)) + 10;
            }

            return cost;
        }

    }

    public static class Feval implements Evaluator<float[]> {

        private final int dimension;

        public Feval(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public double evaluate(float[] elements) {
            double cost = 0;
            for (int i = 0; i < elements.length; i++) {
                double xi = new NormUtil(1, 0, 10, -10).normalize(elements[i]);
                cost += (xi * xi) - (10 * Math.cos(2 * Math.PI * xi)) + 10;
            }

            return cost;
        }

    }
}
