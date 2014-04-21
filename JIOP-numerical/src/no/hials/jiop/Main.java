/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import no.hials.jiop.swarm.MultiSwarmOptimization;
import no.hials.jiop.swarm.ParticleSwarmOptimization;
import no.hials.utilities.NormUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import no.hials.utilities.DoubleArray;

/**
 *
 * @author LarsIvar
 */
public class Main {

    public static void main(String[] args) {

        final int dim = 10;
        Evaluator myEval = new Evaluator() {

            @Override
            public double evaluate(DoubleArray candidateSolution) {
                double cost = 0;
                for (int i = 0; i < candidateSolution.length; i++) {
                    double xi = new NormUtil(1, 0, 5, -5).normalize(candidateSolution.get(i));
                    cost += (xi * xi) - (10 * Math.cos(2 * Math.PI * xi)) + 10;
                }

                return cost;
            }
        };

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();

//        Algorithm bfo = new BacterialForagingOptimization(100, dim, myEval, false);
//        SolutionData bfoCompute = bfo.compute(0d, 100l);
//        System.out.println("BFO " + bfoCompute);
//        xySeriesCollection.addSeries(bfo.getSeries());

//        Algorithm bfo1 = new BacterialForagingOptimization(100, dim, myEval, true);
//        SolutionData bfo1Compute = bfo1.compute(0d, 100l);
//        System.out.println("BFO1 " + bfo1Compute);
//        xySeriesCollection.addSeries(bfo1.getSeries());

        Algorithm de = new DifferentialEvolution(30, 0.9, 0.7, dim, myEval, false);
        SolutionData deCompute = de.compute(0d, 100l);
        System.out.println("DE " + deCompute);
        xySeriesCollection.addSeries(de.getSeries());

        Algorithm de1 = new DifferentialEvolution(30, 0.9, 0.7, dim, myEval, true);
        SolutionData de1Compute = de1.compute(0d, 100l);
        System.out.println("DE1 " + de1Compute);
        xySeriesCollection.addSeries(de1.getSeries());

        Algorithm pso = new ParticleSwarmOptimization(40, dim, myEval, false);
        SolutionData psoCompute = pso.compute(0d, 100l);
        System.out.println("PSO " + psoCompute);
        xySeriesCollection.addSeries(pso.getSeries());

        Algorithm pso1 = new ParticleSwarmOptimization(40, dim, myEval, true);;
        SolutionData pso1Compute = pso1.compute(0d, 100l);
        System.out.println("PSO1 " + pso1Compute);
        xySeriesCollection.addSeries(pso1.getSeries());

        Algorithm mso = new MultiSwarmOptimization(5, 30, dim, myEval, false);
        SolutionData msoCompute = mso.compute(0d, 100l);
        System.out.println("MSO " + msoCompute);
        xySeriesCollection.addSeries(mso.getSeries());

        Algorithm mso1 = new MultiSwarmOptimization(5, 30, dim, myEval, true);
        SolutionData mso1Compute = mso1.compute(0d, 100l);
        System.out.println("MSO1 " + mso1Compute);
        xySeriesCollection.addSeries(mso1.getSeries());

        Algorithm abs = new ArtificialBeeColony(30, 0.25, dim, myEval);
        SolutionData absCompute = abs.compute(0d, 100l);
        System.out.println("ABS " + absCompute);
        xySeriesCollection.addSeries(abs.getSeries());

//        Algorithm sa = new SimulatedAnnealing(10, 0.995, dim, myEval);
//        SolutionData saCompute = sa.compute(0d, 100l);
//        System.out.println("SA " + saCompute);
//        xySeriesCollection.addSeries(sa.getSeries());
//
//        Algorithm amo = new AmoebaOptimization(3, dim, myEval);
//        SolutionData amoCompute = amo.compute(0d, 100l);
//        System.out.println("AMO " + amoCompute);
//        xySeriesCollection.addSeries(amo.getSeries());
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
}
