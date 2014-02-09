/*
 * Copyright (c) 2014, LarsIvar
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
package no.hials.jiop.tsp;

import java.awt.Color;
import java.util.Random;
import javax.swing.JFrame;
import no.hials.jiop.base.MLAlgorithm;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.base.candidates.containers.CandidateArrayContainer;
import no.hials.jiop.base.candidates.containers.CandidateListContainer;
import no.hials.jiop.base.candidates.encoding.factories.DoubleArrayEncodingFactory;
import no.hials.jiop.base.candidates.encoding.factories.DoubleArrayParticleEncodingFactory;
import no.hials.jiop.evolutionary.DE;
import no.hials.jiop.evolutionary.DoubleArrayDifferentialCrossover;
import no.hials.jiop.evolutionary.ga.DoubleArrayCrossover;
import no.hials.jiop.evolutionary.ga.DoubleArrayMutation;
import no.hials.jiop.evolutionary.ga.GA;
import no.hials.jiop.evolutionary.ga.selection.StochasticUniversalSampling;
import no.hials.jiop.physical.GeometricAnnealingSchedule;
import no.hials.jiop.physical.SA;
import no.hials.jiop.physical.SAalt;
import no.hials.jiop.swarm.ABS;
import no.hials.jiop.swarm.PSO;
import no.hials.jiop.utils.NormUtil;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author LarsIvar
 */
public class TSPMain {

    private static int numCities = 5;
    public static final City[] cities = new City[numCities];

    public static void main(String[] args) {
        
//        cities[0] = new City(0, -2);
//        cities[1] = new City(-2, 0);
//        cities[2] = new City(0, 2);
//        cities[3] = new City(2, 0);
//        cities[4] = new City(0, 0);

        final Random rng = new Random();
        for (int i = 0; i < numCities; i++) {
            int x = rng.nextInt(4);
            int y = rng.nextInt(4);
            City c = new City(x, y);
            cities[i] = c;
        }
        MLAlgorithm<double[]>[] methods = new MLAlgorithm[]{
            new DE<>(new DoubleArrayDifferentialCrossover(0.8, 0.9), new DoubleArrayEncodingFactory(numCities), new CandidateArrayContainer(30), new TSPEvaluator()),
            new PSO<>(2, 0.9, 0.9, new DoubleArrayParticleEncodingFactory(numCities), new CandidateArrayContainer(30), new TSPEvaluator()),
            new SA<>(100, new GeometricAnnealingSchedule(0.85), new DoubleArrayEncodingFactory(numCities), new TSPEvaluator()),
            new SAalt(100, new GeometricAnnealingSchedule(0.85), new DoubleArrayEncodingFactory(numCities), new TSPEvaluator()),
            new GA<>(0.1f, 0.5f, 0.2f, new StochasticUniversalSampling<double[]>(), new DoubleArrayCrossover(), new DoubleArrayMutation(0.01, 0), new DoubleArrayEncodingFactory(numCities), new CandidateListContainer(60), new TSPEvaluator()),
            new ABS(6, new DoubleArrayEncodingFactory(numCities), new CandidateListContainer<>(60), new TSPEvaluator())};

        for (MLAlgorithm<double[]> m : methods) {
            m.warmUp(100l);
            EvaluatedCandidate<double[]> runFor = m.runFor(100l);
            System.out.println(runFor);
            plotTour(runFor.getVariables());
        }
    }

    public static int decode(double var) {
        return (int) new NormUtil(1, 0, numCities - 1, 0).normalize(var);
    }

    public static void plotTour(double[] tour) {
        Plot2DPanel plot = new Plot2DPanel();

        for (int i = 0; i < tour.length - 1; i++) {
            City c1 = cities[decode(tour[i])];
            City c2 = cities[decode(tour[i + 1])];
            System.out.println(c1 + " " + c2);
            plot.addLinePlot("", new double[]{c1.x, c1.y}, new double[]{c2.x, c2.y});

        }
        for (int i = 0; i < tour.length; i++) {
            plot.addLabel("X", Color.RED, new double[]{cities[i].x, cities[i].y});
        }
        
        plot.setFixedBounds(0, -4, 4);
        plot.setFixedBounds(1, -4, 4);
        final JFrame frame = new JFrame();
        frame.getContentPane().add(plot);
        frame.setVisible(true);
        frame.setSize(500, 500);
    }
}
