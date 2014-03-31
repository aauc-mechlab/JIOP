/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.io.Serializable;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 *
 * @author LarsIvar
 */
public abstract class Algorithm implements Optimizable, Serializable {

    private final String name;
    private final int dimension;
    private Evaluator evaluator;

    private XYSeries timeSeries;

    protected final Random rng = new Random();

    public Algorithm(String name, int dimension, Evaluator evaluator) {
        this.name = name;
        this.dimension = dimension;
        this.evaluator = evaluator;
        this.timeSeries = new XYSeries(name);
    }

    public final void init() {
        this.timeSeries = new XYSeries(name);
        subInit();
    }

    public final void init(DoubleArray... seeds) {
        this.timeSeries = new XYSeries(name);
        subInit(seeds);
    }
    
    public abstract void subInit();
    public abstract void subInit(DoubleArray... seeds);

    public SolutionData compute(long timeOut) {
        Candidate solution = null;
        long t0 = System.currentTimeMillis();
        long t;
        int it = 0;
        do {
            solution = singleIteration();
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = solution.getCost();
            timeSeries.add(x, y);
            it++;
        } while ((t = System.currentTimeMillis() - t0) < timeOut);

        return new SolutionData(solution, solution.getCost(), it, t);
    }

    public SolutionData compute(double error, long timeOut) {
        Candidate solution = null;
        long t0 = System.currentTimeMillis();
        long t;
        int it = 0;
        do {
            solution = singleIteration();
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = solution.getCost();
            timeSeries.add(x, y);
            it++;
        } while (((t = System.currentTimeMillis() - t0) < timeOut) && (solution.getCost() > error));

        return new SolutionData(solution, solution.getCost(), it, t);
    }

    public SolutionData compute(int iterations) {
        Candidate solution = null;
        long t0 = System.currentTimeMillis();
        int it = 0;
        do {
            solution = singleIteration();
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = solution.getCost();
            timeSeries.add(x, y);
        } while (it++ < iterations);

        return new SolutionData(solution, solution.getCost(), it, System.currentTimeMillis() - t0);
    }

    public void optimizeFreeParameters(double error, long timeOut) {
        DoubleArray freeParameters = getFreeParameters();
        AlgorithmOptimizer optimizer = new AlgorithmOptimizer(this);
        SolutionData optimize = optimizer.optimize(error, timeOut);
        System.out.println(optimize);
        System.out.println("Variables changed");
        System.out.println("Was: " + freeParameters);
        setFreeParameters(optimize.solution);
        System.out.println("Is: " + getFreeParameters());
        ApplicationFrame frame = new ApplicationFrame("");
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(optimizer.algorithm.timeSeries);
        xySeriesCollection.addSeries(optimizer.optimizable.timeSeries);
        final JFreeChart chart = ChartFactory.createXYLineChart("", "Time[s]", "Cost", xySeriesCollection);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        frame.setContentPane(chartPanel);
        frame.setVisible(true);
        frame.pack();

    }

    protected abstract Candidate singleIteration();

    public XYSeries getSeries() {
        return timeSeries;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return name;
    }

}
