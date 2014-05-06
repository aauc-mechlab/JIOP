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
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.CandidateSolution;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public abstract class Algorithm<E> implements Serializable {

    protected final static int availableProcessors = Runtime.getRuntime().availableProcessors();

    private String name;
    private Evaluator<E> evaluator;
    private final Class<?> templateClass;

    private XYSeries timeSeries;
    protected final Random rng = new Random();

    private ExecutorService pool;
    private ExecutorCompletionService completionService;

    private Candidate<E> bestCandidate;

    public Algorithm(Class<?> clazz, String name) {
        this(clazz, null, name);
    }

    public Algorithm(Class<?> templateClass, Evaluator<E> evaluator, String name) {
        this.name = name;
        this.templateClass = templateClass;
        this.evaluator = evaluator;
        this.timeSeries = new XYSeries(name);
    }

    /**
     * Evaluates the cost of the given candidate and returns it This is the same
     * as to call getEvaluator().evaluate(candidate) Note: This call does not
     * update the candidates affiliated cost.
     *
     * @param candidate the candidate to evalaute
     * @return the candidates cost
     */
    public double evaluate(Candidate<E> candidate) {
        return getEvaluator().evaluate(candidate.getElements());
    }

    /**
     * Evaluates and updates the cost of the given candidate. This is the same
     * as to call candidate.setCost(getEvaluator().evaluate(candidate))
     *
     * @param candidate the candidate to evaluate
     * @return the updated candidate intance
     */
    public Candidate<E> evaluateAndUpdate(Candidate<E> candidate) {
        candidate.setCost(getEvaluator().evaluate(candidate.getElements()));
        return candidate;
    }

    /**
     * Creates and returns a new candidate. Uses reflection to construct a new
     * instance of the template class
     *
     * @return a new Candidate instance initialized with random values
     */
    public Candidate<E> newCandidate() {
        try {
            Constructor<?> constructor = templateClass.getConstructor(int.class);
            Candidate<E> newInstance = (Candidate) constructor.newInstance(getEvaluator().getDimension());
            return evaluateAndUpdate(newInstance);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Candidate<E> newCandidate(E e) {
        try {
            Constructor<?> constructor = templateClass.getConstructor(e.getClass(), double.class);
            Candidate<E> newInstance = (Candidate) constructor.newInstance(e, getEvaluator().evaluate(e));
            return newInstance;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public final void init() {
        this.bestCandidate = null;
        this.timeSeries = new XYSeries(name);
        setBestCandidateIfBetter(subInit());
    }

    public final void init(List<E> seeds) {
        this.bestCandidate = null;
        if (seeds == null || seeds.isEmpty()) {
            init();
        } else {
            this.timeSeries = new XYSeries(name);
            setBestCandidateIfBetter(subInit(seeds));
        }
    }

    public synchronized Candidate<E> getBestCandidate() {
        return bestCandidate.copy();
    }

    public synchronized void setBestCandidateIfBetter(Candidate<E> candidate) {
        if (bestCandidate == null) {
            bestCandidate = candidate;
        } else {
            if (candidate.getCost() < bestCandidate.getCost()) {
                this.bestCandidate = candidate.copy();
            }
        }
    }

    protected abstract Candidate<E> subInit();

    protected abstract Candidate<E> subInit(List<E> seeds);

    protected abstract Candidate<E> singleIteration();

    protected CompletionService<Candidate<E>> getCompletionService() {
        if (pool == null) {
            pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            completionService = new ExecutorCompletionService(pool);
        }
        return completionService;
    }

    public CandidateSolution compute(long timeOut) {
        long t0 = System.currentTimeMillis();
        long t;
        int it = 0;
        do {
            setBestCandidateIfBetter(singleIteration().copy());
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = getBestCandidate().getCost();
            timeSeries.add(x, y);
            it++;
        } while ((t = System.currentTimeMillis() - t0) < timeOut);

        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), it, t);
    }

    public CandidateSolution compute(double error, long timeOut) {
        long t;
        long t0 = System.currentTimeMillis();
        int it = 0;
        do {
            setBestCandidateIfBetter(singleIteration().copy());
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = getBestCandidate().getCost();
            timeSeries.add(x, y);
            it++;
        } while (((t = System.currentTimeMillis() - t0) < timeOut) && (getBestCandidate().getCost() > error));

        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), it, t);
    }

    public CandidateSolution compute(int iterations) {
        long t0 = System.currentTimeMillis();
        int it = 0;
        do {
            setBestCandidateIfBetter(singleIteration().copy());
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = getBestCandidate().getCost();
            timeSeries.add(x, y);
        } while (it++ < iterations);

        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), it, System.currentTimeMillis() - t0);
    }

    public CandidateSolution compute(int iterations, long timeOut) {
        long t0 = System.currentTimeMillis();
        int it = 0;
        do {
            setBestCandidateIfBetter(singleIteration().copy());
            double x = (double) (System.currentTimeMillis() - t0) / 1000;
            double y = getBestCandidate().getCost();
            timeSeries.add(x, y);
        } while (((System.currentTimeMillis() - t0) < timeOut) && (it++ < iterations));

        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), it, System.currentTimeMillis() - t0);
    }

//    /**
//     * Writes the MLHistory data to file. If the directory does not exist, a new
//     * one will be created.
//     *
//     * @param dir the directory
//     * @param fileName the name of the file
//     */
//    public void dumpHistoryToFile(String dir, String fileName) {
//        File file = new File(dir);
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        StringBuilder sb = new StringBuilder();
//        try (
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "//" + fileName)))) {
//            for (int i = 0; i < series; i++) {
//                sb.append(history.getIterations()[i]).append("\t").append(history.getTimestamps()[i]).append("\t").append(history.getCosts()[i]).append("\n");
//            }
//            bw.write(sb.toString());
//            bw.flush();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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

    public void setName(String name) {
        this.name = name;
    }

    public int getDimension() {
        return evaluator.getDimension();
    }

    @Override
    public String toString() {
        return name;
    }

}
