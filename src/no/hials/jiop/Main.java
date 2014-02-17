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

import no.hials.jiop.utils.ArrayUtil;
import java.util.Arrays;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.algorithms.swarm.pso.PSO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import no.hials.jiop.base.MLAlgorithm;
import no.hials.jiop.base.candidates.encoding.factories.DoubleArrayEncodingFactory;
import no.hials.jiop.base.candidates.encoding.factories.DoubleArrayParticleEncodingFactory;
import no.hials.jiop.base.algorithms.evolutionary.de.DE;
import no.hials.jiop.base.algorithms.evolutionary.de.DoubleArrayDifferentialCrossover;
import no.hials.jiop.base.algorithms.evolutionary.ga.crossover.DoubleArrayCrossover;
import no.hials.jiop.base.algorithms.evolutionary.ga.mutation.DoubleArrayMutation;
import no.hials.jiop.base.algorithms.evolutionary.ga.GA;
import no.hials.jiop.base.algorithms.evolutionary.ga.selection.StochasticUniversalSampling;
import no.hials.jiop.base.algorithms.physical.GeometricAnnealingSchedule;
import no.hials.jiop.base.algorithms.physical.SA;
import no.hials.jiop.base.algorithms.physical.SAalt;
import no.hials.jiop.base.algorithms.swarm.abs.ABS;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class Main {

    public static final int dim = 5;
    public static double[] desired = ArrayUtil.randomD(dim);

    public static void main(String[] args) throws InterruptedException {

        System.out.println(Arrays.toString(desired));

        MLAlgorithm<double[]>[] methods = new MLAlgorithm[]{
            new DE<>(30, new DoubleArrayDifferentialCrossover(0.8, 0.9), new DoubleArrayEncodingFactory(dim), new MyEvaluator()),
            new PSO<>(30, 2, 0.9, 0.9, new DoubleArrayParticleEncodingFactory(dim), new MyEvaluator()),
            new SA<>(100, new GeometricAnnealingSchedule(0.85), new DoubleArrayEncodingFactory(dim), new MyEvaluator()),
            new SAalt(100, new GeometricAnnealingSchedule(0.85), new DoubleArrayEncodingFactory(dim), new MyEvaluator()),
            new GA<>(80, 0.1f, 0.5f, 0.2f, new StochasticUniversalSampling(), new DoubleArrayCrossover(), new DoubleArrayMutation(0.01, 0), new DoubleArrayEncodingFactory(dim), new MyEvaluator()),
            new ABS(60, 6, new DoubleArrayEncodingFactory(dim), new MyEvaluator())};

        for (final MLAlgorithm method : methods) {
            method.warmUp(1000);
            EvaluatedCandidate run = method.runFor(0.0000000001, 1000);
            System.out.println(run);

            final JFrame frame = new JFrame(method.getName());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    frame.getContentPane().add(method.getPlot());
                    frame.setVisible(true);
                    frame.setSize(500, 500);
                }
            });
        }
    }

    static class MyEvaluator extends Evaluator<double[]> {

        @Override
        public double evaluate(double[] encoding) {

            double cost = 0;
            int i = 0;
            for (double d : encoding) {
                cost += Math.abs(d - desired[i++]);
            }
            return cost;
        }

    }
    
   
}
