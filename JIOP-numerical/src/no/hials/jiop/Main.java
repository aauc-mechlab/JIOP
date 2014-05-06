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
import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.candidates.DoubleArrayBacteriaCandidate;
import no.hials.jiop.candidates.DoubleArrayCandidate;
import no.hials.jiop.candidates.DoubleArrayParticleCandidate;
import no.hials.jiop.evolutionary.DifferentialEvolution;
import no.hials.jiop.heuristic.AmoebaOptimization;
import no.hials.jiop.physical.SimulatedAnnealing;
import no.hials.jiop.swarm.ArtificialBeeColony;
import no.hials.jiop.swarm.BacterialForagingOptimization;
import no.hials.jiop.swarm.MultiSwarmOptimization;
import no.hials.jiop.swarm.ParticleSwarmOptimization;
import no.hials.jiop.candidates.CandidateSolution;
import no.hials.jiop.temination.CostCriteria;
import no.hials.jiop.temination.TimeElapsedCriteria;
import no.hials.utilities.NormUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 * Main class
 *
 * @author Lars Ivar Hatledal
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<AbstractAlgorithm<double[]>> algorithms = new ArrayList<>();

        algorithms.add(new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, true));
        algorithms.add(new DifferentialEvolution(DoubleArrayCandidate.class, 30, 0.9, 0.7, false));
        algorithms.add(new ParticleSwarmOptimization(DoubleArrayParticleCandidate.class, 40, false));
        algorithms.add(new ParticleSwarmOptimization(DoubleArrayParticleCandidate.class, 40, true));
        algorithms.add(new MultiSwarmOptimization(DoubleArrayParticleCandidate.class, 5, 30, false));
        algorithms.add(new MultiSwarmOptimization(DoubleArrayParticleCandidate.class, 5, 30, true));
        algorithms.add(new ArtificialBeeColony(DoubleArrayCandidate.class, 60, 12));
        algorithms.add(new AmoebaOptimization(DoubleArrayCandidate.class, 50));
        algorithms.add(new SimulatedAnnealing(DoubleArrayCandidate.class, 20, 0.995));
        algorithms.add(new BacterialForagingOptimization(DoubleArrayBacteriaCandidate.class, 100, false));
        algorithms.add(new BacterialForagingOptimization(DoubleArrayBacteriaCandidate.class, 100, true));

        //Warming up the JVM
        for (AbstractAlgorithm alg : algorithms) {
            alg.setEvaluator(new ExampleEvaluator(4));
            alg.init();
            alg.compute(new TimeElapsedCriteria(100l));
        }

        //The actual run
        for (AbstractAlgorithm alg : algorithms) {
            alg.setEvaluator(new ExampleEvaluator(5));
            alg.init();
            CandidateSolution<double[]> compute = alg.compute(new TimeElapsedCriteria(100l), new CostCriteria(0d));
            System.out.println(alg.toString() + "\n" + compute);
            xySeriesCollection.addSeries(alg.getSeries());
        }

        if (xySeriesCollection.getSeriesCount() > 0) {
            ApplicationFrame frame = new ApplicationFrame("");
            final JFreeChart chart = ChartFactory.createXYLineChart("", "Time[s]", "Cost", xySeriesCollection);
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            frame.setContentPane(chartPanel);
            frame.setVisible(true);
            frame.pack();
        }
    }

    public static class ExampleEvaluator implements Evaluator<double[]> {

        private final int dimension;

        public ExampleEvaluator(int dimension) {
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
}
