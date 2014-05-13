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
import java.util.List;
import no.hials.jiop.candidates.bacterium.DoubleArrayBacteriaFactory;
import no.hials.jiop.candidates.particles.DoubleArrayParticleFactory;
import no.hials.jiop.evolutionary.de.DifferentialEvolution;
import no.hials.jiop.factories.DoubleArrayCandidateFactory;
import no.hials.jiop.factories.DoubleListCandidateFactory;
import no.hials.jiop.heuristic.amoeba.AmoebaOptimization;
import no.hials.jiop.physical.sa.SimulatedAnnealing;
import no.hials.jiop.swarm.abs.ArtificialBeeColony;
import no.hials.jiop.swarm.bfo.BacterialForagingOptimization;
import no.hials.jiop.swarm.pso.MultiSwarmOptimization;
import no.hials.jiop.swarm.pso.ParticleSwarmOptimization;
import no.hials.jiop.temination.TimeElapsedCriteria;
import no.hials.utilities.NormUtil;

/**
 * Main class
 *
 * @author Lars Ivar Hatledal
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        Evaluator<double[]> deval = new Deval(5);
        Evaluator<List<Double>> deval2 = new Deval2(5);
        AlgorithmCollection<double[]> algorithms = new AlgorithmCollection<>();

        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, new DoubleArrayCandidateFactory(), deval, false));
        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, new DoubleListCandidateFactory(), deval2, "DE -List", false));
        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, new DoubleArrayCandidateFactory(), deval, true));
        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, new DoubleListCandidateFactory(), deval2, "DE -List2", true));
        algorithms.add(new ParticleSwarmOptimization(40, new DoubleArrayParticleFactory(), deval, false));
        algorithms.add(new ParticleSwarmOptimization(40, new DoubleArrayParticleFactory(), deval, true));
        algorithms.add(new MultiSwarmOptimization(3, 40, new DoubleArrayParticleFactory(), deval, false));
        algorithms.add(new MultiSwarmOptimization(4, 40, new DoubleArrayParticleFactory(), deval, true));
        algorithms.add(new ArtificialBeeColony(60, 12, new DoubleArrayCandidateFactory(), deval));
        algorithms.add(new AmoebaOptimization(50, new DoubleArrayParticleFactory(), deval));
        algorithms.add(new SimulatedAnnealing(20, 0.995, new DoubleArrayCandidateFactory(), deval));
        algorithms.add(new BacterialForagingOptimization(100, new DoubleArrayBacteriaFactory(), deval, false));
        algorithms.add(new BacterialForagingOptimization(100, new DoubleArrayBacteriaFactory(), deval, true));
//        DifferentialEvolution de = new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, deval, "DE - Optimized", false);
//        CandidateSolution optimize = new AlgorithmOptimizer(de).optimize(0, 20000);
//        de.setFreeParameters((double[]) optimize.solution.getElements());
//        System.out.println(Arrays.toString(de.getFreeParameters()));
//        algorithms.add(de);

        algorithms.warmUp(100l);
        algorithms.computeAll(new TimeElapsedCriteria(100l));
        algorithms.plotResults();

    }

    public static class Deval extends Evaluator<double[]> {

        public Deval(int dimension) {
            super(dimension);
        }

        @Override
        public double getCost(double[] elements) {
            double cost = 0;
            for (int i = 0; i < elements.length; i++) {
                double xi = new NormUtil(1, 0, 10, -10).normalize(elements[i]);
                cost += (xi * xi) - (10 * Math.cos(2 * Math.PI * xi)) + 10;
            }

            return cost;
        }

    }

    public static class Deval2 extends Evaluator<List<Double>> {

        public Deval2(int dimension) {
            super(dimension);
        }

        @Override
        public double getCost(List<Double> elements) {
            double cost = 0;
            for (int i = 0; i < elements.size(); i++) {
                double xi = new NormUtil(1, 0, 10, -10).normalize(elements.get(i));
                cost += (xi * xi) - (10 * Math.cos(2 * Math.PI * xi)) + 10;
            }

            return cost;
        }

    }
}
