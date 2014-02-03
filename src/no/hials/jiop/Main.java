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

import java.util.Arrays;
import no.hials.jiop.factories.DoubleArrayParticleFactory;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.base.swing.MLHistoryPlot;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.factories.DoubleArrayCandidateFactory;
import no.hials.jiop.swarm.PSO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.evolutionary.DoubleArrayDE;
import no.hials.jiop.evolutionary.ga.DoubleArrayCrossover;
import no.hials.jiop.evolutionary.ga.DoubleArrayMutation;
import no.hials.jiop.evolutionary.ga.GA;
import no.hials.jiop.evolutionary.ga.selection.StochasticUniversalSampling;
import no.hials.jiop.evolutionary.ga.selection.TournamentSelection;

/**
 *
 * @author LarsIvar
 */
public class Main {

    public static final int dim = 6;
    public static double[] desired = ArrayUtil.randomD(dim);

    public static void main(String[] args) {

        System.out.println(Arrays.toString(desired));
        
        MLMethod[] methods = new MLMethod[]{new DoubleArrayDE(30, 0.8, 0.9, dim, new DoubleArrayCandidateFactory(new MyEvaluator())),
            new PSO<>(40, 0.5, 0.9, 0.9, dim, new DoubleArrayParticleFactory(new MyEvaluator())),
            new GA(20, 0.1f, 0.5f, 0.1f, dim, new StochasticUniversalSampling(), new DoubleArrayCrossover(), new DoubleArrayMutation(0.01, 0.5), new DoubleArrayCandidateFactory(new MyEvaluator())),
            new GA(80, 0.1f, 0.5f, 0.2f, dim, new TournamentSelection<>(0.85), new DoubleArrayCrossover(), new DoubleArrayMutation(0.001, 1), new DoubleArrayCandidateFactory(new MyEvaluator()))};

        for (final MLMethod method : methods) {
//            method.getFactory().setNumThreads(4);
            method.warmUp(250);
            EvaluatedCandidate run = method.runFor(100l);
            System.out.println(run);

            final JFrame frame = new JFrame(method.getName());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    frame.getContentPane().add(new MLHistoryPlot(method));
                    frame.setVisible(true);
                    frame.setSize(500, 500);
                }
            });

        }
    }

    static class MyEvaluator implements Evaluator<double[]> {

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
