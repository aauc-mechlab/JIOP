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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.util.CandidateStructure;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Lars Ivar Hatledal
 */
public abstract class Algorithm<E> implements Serializable {

    private final String name;
    private Evaluator<E> evaluator;
    private final Class<?> clazz;

    private XYSeries timeSeries;

    protected final Random rng = new Random();

    public Algorithm(Class<?> clazz, String name) {
        this.name = name;
        this.clazz = clazz;
        this.timeSeries = new XYSeries(name);
    }

    public CandidateStructure<E> random() {
        try {
            CandidateStructure<E> newCandidate = newCandidate();
            newCandidate.randomize();
            evaluator.evaluate((E) newCandidate.getElements());
            return newCandidate;
        } catch (SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public CandidateStructure<E> newCandidate() {
        try {
            Constructor<?> constructor = clazz.getConstructor(int.class);
            CandidateStructure newInstance = (CandidateStructure) constructor.newInstance(getEvaluator().getDimension());
            getEvaluator().evaluate((E) newInstance.getElements());
            return newInstance;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public CandidateStructure<E> newCandidate(E e) {
        try {
            Constructor<?> constructor = clazz.getConstructor(e.getClass());
            CandidateStructure newInstance = (CandidateStructure) constructor.newInstance(e);
            newInstance.setCost(getEvaluator().evaluate((E) newInstance.getElements()));
            return newInstance;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     public CandidateStructure<E> copy(CandidateStructure<E> candidate) {
        try {
            Constructor<?> constructor = clazz.getConstructor(candidate.getElements().getClass());
            CandidateStructure copy = (CandidateStructure) constructor.newInstance(candidate.getElements());
            copy.setCost(candidate.getCost());
            return copy;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final void init() {
        this.timeSeries = new XYSeries(name);
        subInit();
    }

    public final void init(List<E> seeds) {
        if (seeds == null || seeds.isEmpty()) {
            init();
        } else {
            this.timeSeries = new XYSeries(name);
            subInit(seeds);
        }
    }

    protected abstract CandidateStructure<E> singleIteration();

    protected abstract void subInit();

    protected abstract void subInit(List<E> seeds);

    public SolutionData compute(long timeOut) {
        CandidateStructure<E> solution = null;
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
        CandidateStructure<E> solution = null;
        long t;
        long t0 = System.currentTimeMillis();
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
        CandidateStructure<E> solution = null;
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

    public SolutionData compute(int iterations, long timeOut) {
        CandidateStructure<E> solution = null;
        long t;
        long t0 = System.currentTimeMillis();
        int it = 0;
        do {
            solution = singleIteration();
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = solution.getCost();
            timeSeries.add(x, y);
        } while (((t = System.currentTimeMillis() - t0) < timeOut) && (it++ < iterations));

        return new SolutionData(solution, solution.getCost(), it, System.currentTimeMillis() - t0);
    }

//    public void optimizeFreeParameters(double error, long timeOut) {
//        DoubleArray freeParameters = getFreeParameters();
//        AlgorithmOptimizer optimizer = new AlgorithmOptimizer(this);
//        SolutionData optimize = optimizer.optimize(error, timeOut);
//        System.out.println(optimize);
//        System.out.println("Variables changed");
//        System.out.println("Was: " + freeParameters);
//        setFreeParameters(optimize.solution);
//        System.out.println("Is: " + getFreeParameters());
//        ApplicationFrame frame = new ApplicationFrame("");
//        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
//        xySeriesCollection.addSeries(optimizer.algorithm.timeSeries);
//        xySeriesCollection.addSeries(optimizer.optimizable.timeSeries);
//        final JFreeChart chart = ChartFactory.createXYLineChart("", "Time[s]", "Cost", xySeriesCollection);
//        final ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//        frame.setContentPane(chartPanel);
//        frame.setVisible(true);
//        frame.pack();
//
//    }
    public XYSeries getSeries() {
        return timeSeries;
    }

    public Evaluator<E> getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return evaluator.getDimension();
    }

    @Override
    public String toString() {
        return name;
    }

}
