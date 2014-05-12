///*
// * Copyright (c) 2014, Lars Ivar
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * * Redistributions of source code must retain the above copyright notice, this
// *   list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
//package no.hials.jiop;
//
//import java.util.ArrayList;
//import java.util.List;
//import no.hials.jiop.candidates.CandidateSolution;
//import no.hials.jiop.temination.TerminationCriteria;
//import no.hials.jiop.temination.TimeElapsedCriteria;
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.ApplicationFrame;
//
///**
// *
// * @author Lars Ivar
// */
//public class AlgorithmCollection<E> extends ArrayList<AbstractAlgorithm<E>> {
//
//    private final XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
//
//    public void warmUp(long millisPerAlgorithm) {
//        //Warming up the JVM
//        for (AbstractAlgorithm alg : this) {
//            alg.init();
//            alg.compute(new TimeElapsedCriteria(millisPerAlgorithm));
//            alg.clearHistory();
//        }
//    }
//
//    public void computeAll(TerminationCriteria... criterias) {
//        for (AbstractAlgorithm<E> alg : this) {
//            alg.clearHistory();
//            alg.init();
//            CandidateSolution<E> compute = alg.compute(criterias);
//            System.out.println(alg.getName());
//            System.out.println(compute);
//            System.out.println("---------------------");
//        }
//    }
//
//    public void computeAll(List<E> seed, TerminationCriteria... criterias) {
//        for (AbstractAlgorithm<E> alg : this) {
//            alg.clearHistory();
//            alg.init(seed);
//            CandidateSolution<E> compute = alg.compute(criterias);
//            System.out.println(alg.getName());
//            System.out.println(compute);
//            System.out.println("---------------------");
//        }
//    }
//
//    public void plotResults() {
//        for (AbstractAlgorithm<E> alg : this) {
//             xySeriesCollection.addSeries(alg.getSeries());
//        }
//        if (xySeriesCollection.getSeriesCount() > 0) {
//            ApplicationFrame frame = new ApplicationFrame("");
//            final JFreeChart chart = ChartFactory.createXYLineChart("", "Time[s]", "Cost", xySeriesCollection);
//            final ChartPanel chartPanel = new ChartPanel(chart);
//            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//            frame.setContentPane(chartPanel);
//            frame.setVisible(true);
//            frame.pack();
//        }
//    }
//
//}
