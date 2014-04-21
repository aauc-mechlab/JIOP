/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.swarm.MultiSwarmOptimization;
import no.hials.jiop.swarm.ParticleSwarmOptimization;
import no.hials.utilities.DoubleArray;
import no.hials.utilities.NormUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

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

            @Override
            public int getDimension() {
                return dim;
            }
        };

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        List<Algorithm> algorithms = new ArrayList<>();

        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, false));
        algorithms.add(new DifferentialEvolution(30, 0.9, 0.7, true));
        algorithms.add(new ParticleSwarmOptimization(40, false));
        algorithms.add(new ParticleSwarmOptimization(40, true));
        algorithms.add(new MultiSwarmOptimization(5, 30, false));
        algorithms.add(new MultiSwarmOptimization(5, 30, true));
        algorithms.add(new ArtificialBeeColony(30, 0.25));
        algorithms.add(new AmoebaOptimization(3));
        algorithms.add(new SimulatedAnnealing(100, 0.995));
        algorithms.add(new BacterialForagingOptimization(100, false));
        algorithms.add(new BacterialForagingOptimization(100, true));

        for (Algorithm alg : algorithms) {
            alg.setEvaluator(myEval);
            alg.init();
            SolutionData compute = alg.compute(0d, 100l);
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
}
