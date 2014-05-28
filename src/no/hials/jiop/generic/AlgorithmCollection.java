/*
 * Copyright (c) 2014, Lars Ivar
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
package no.hials.jiop.generic;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import no.hials.jiop.generic.candidates.CandidateSolution;
import no.hials.jiop.generic.temination.TerminationCriteria;
import no.hials.jiop.generic.temination.TimeElapsedCriteria;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A collection of algorithms. Useful for comparison plots
 * @author Lars Ivar Hatledal
 */
public class AlgorithmCollection<E> extends ArrayList<Algorithm<E>> {

    private final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

    /**
     * Invoking this methods runs each algorithm the specified amount of time.
     * Used for warming up the JVM. Important if you want to get correct
     * comparison values.
     *
     * @param millisPerAlgorithm how long each algorithm should be run
     */
    public void warmUp(long millisPerAlgorithm) {
        //Warming up the JVM
        for (Algorithm alg : this) {
            alg.init();
            alg.compute(new TimeElapsedCriteria(millisPerAlgorithm));
        }
    }

    /**
     * Invokes the compute() method on all the algorithms in the collection
     * @param criterias the termination criterias
     */
    public void computeAll(TerminationCriteria... criterias) {
        for (Algorithm<E> alg : this) {
            alg.setTrackCandidatePerformance(true);
            alg.init();
            CandidateSolution<E> compute = alg.compute(criterias);
            System.out.println(alg);
            System.out.println(compute);
            System.out.println("---------------------");
        }
    }

     /**
     * Invokes the compute(seed) method on all the algorithms in the collection
     * @param seed the seed
     * @param criterias the termination criterias
     */
    public void computeAll(List<E> seed, TerminationCriteria... criterias) {
        for (Algorithm<E> alg : this) {
            alg.clearHistory();
            alg.init(seed);
            CandidateSolution<E> compute = alg.compute(criterias);
            System.out.println(alg);
            System.out.println(compute);
            System.out.println("---------------------");
        }
    }

    /**
     * Plots the performance history of all the algorithm in the same plot window
     */
    public void plotResults() {
        for (Algorithm<E> alg : this) {
            xySeriesCollection.addSeries(alg.getSeries());
        }
        if (xySeriesCollection.getSeriesCount() > 0) {
            JFrame frame = new JFrame("Results");
            final JFreeChart chart = ChartFactory.createXYLineChart("", "Time[ms]", "Cost", xySeriesCollection);
            final ChartPanel chartPanel = new ChartPanel(chart);
            //chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            frame.setContentPane(chartPanel);
            frame.setVisible(true);
            frame.pack();
        }
    }

}
