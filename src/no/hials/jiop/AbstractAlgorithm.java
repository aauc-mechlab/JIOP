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

import no.hials.jiop.history.MLHistory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
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
import no.hials.jiop.factories.CandidateFactory;
import no.hials.jiop.temination.TerminationCriteria;
import no.hials.jiop.temination.TerminationData;
import no.hials.jiop.temination.TimeElapsedCriteria;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public abstract class AbstractAlgorithm<E> implements Serializable {

    protected final static int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final CandidateFactory<E> candidateFactory;

    private String name;
    private Evaluator<E> evaluator;

    private MLHistory timeSeries;
    protected final Random rng = new Random();

    private ExecutorService pool;
    private ExecutorCompletionService completionService;

    private Candidate<E> bestCandidate;

    public AbstractAlgorithm(CandidateFactory<E> candidateFactory, Evaluator<E> evaluator, String name) {
        this.name = name;
        this.candidateFactory = candidateFactory;
        this.evaluator = evaluator;
        this.timeSeries = new MLHistory();
    }

    public final void init() {
        this.bestCandidate = null;
        setBestCandidateIfBetter(subInit());
    }

    public final void init(List<E> seeds) {
        if (seeds == null) {
            init();
        } else if (seeds.isEmpty()) {
            init();
        } else {
            this.bestCandidate = null;
            setBestCandidateIfBetter(subInit(seeds));
        }
    }

    protected abstract Candidate<E> subInit();

    protected abstract Candidate<E> subInit(List<E> seeds);

    protected abstract void singleIteration();

    /**
     * Evaluates the cost of the given candidate and returns it This is the same
     * as to call getEvaluator().evaluate(candidate) Note: This call does not
     * update the candidates affiliated cost.
     *
     * @param candidate the candidate to evaluate
     * @return the candidates cost
     */
    public Candidate<E> evaluate(Candidate<E> candidate) {
        return getEvaluator().evaluate(candidate);
    }

    public List<Candidate<E>> evaluateAll(List<Candidate<E>> candidates) {
        return getEvaluator().evaluateAll(candidates);
    }

    /**
     * Creates and returns a new candidate. Uses reflection to construct a new
     * instance of the template class
     *
     * @return a new Candidate instance initialized with random values
     */
    public Candidate<E> randomCandidate() {
        return candidateFactory.generateRandom(getEvaluator().getDimension());
    }

    public Candidate<E> generateFromElements(E e) {
        return candidateFactory.generateFromElements(e);
    }

    public void clearHistory() {
        this.timeSeries = new MLHistory();
    }

    public synchronized Candidate<E> getBestCandidate() {
        return bestCandidate.copy();
    }

    public synchronized double getBestCost() {
        return bestCandidate.getCost();
    }

    public synchronized void setBestCandidateIfBetter(Candidate<E> candidate) {
        if (bestCandidate == null) {
            bestCandidate = candidate.copy();
        } else {
            if (candidate.getCost() < bestCandidate.getCost()) {
                this.bestCandidate = candidate.copy();
            }
        }
    }

    protected CompletionService<Candidate<E>> getCompletionService() {
        if (pool == null) {
//            pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            pool = Executors.newCachedThreadPool();
            completionService = new ExecutorCompletionService(pool);
        }
        return completionService;
    }

    public CandidateSolution compute(TerminationCriteria... criterias) {
        if (criterias == null) {
            criterias = new TerminationCriteria[]{new TimeElapsedCriteria(100l)};
        } else if (criterias.length == 0) {
            criterias = new TerminationCriteria[]{new TimeElapsedCriteria(100l)};
        }
        long t0 = System.currentTimeMillis();
        long timeElapsed = 0;
        double bestCost;
        int numIterations = 0;
        boolean terminate = false;
        while (!terminate) {
            long start = System.nanoTime();
            singleIteration();
            double end = (double) (System.nanoTime() - start) / 1000000000;
            timeElapsed = (System.currentTimeMillis() - t0);
            bestCost = getBestCandidate().getCost();
            numIterations++;
            timeSeries.add(end, bestCost);
            for (TerminationCriteria tc : criterias) {
                if (tc.souldTerminate(new TerminationData(bestCost, timeElapsed, numIterations))) {
                    terminate = true;
                }
            }
        }
        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), numIterations, timeElapsed);
    }

    /**
     * Writes the MLHistory data to file. If the directory does not exist, a new
     * one will be created.
     *
     * @param dir the directory
     * @param fileName the name of the file
     */
    public void dumpHistoryToFile(String dir, String fileName) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        StringBuilder sb = new StringBuilder();
        try (
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "//" + fileName)))) {
            for (int i = 0; i < timeSeries.size(); i++) {
                sb.append(timeSeries.getIterations()[i]).append("\t").append(timeSeries.getTimestamps()[i]).append("\t").append(timeSeries.getCosts()[i]).append("\n");
            }
            bw.write(sb.toString());
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        XYSeries series = new XYSeries(getName());
        double[] timestamps = timeSeries.getTimestamps();
        double[] costs = timeSeries.getCosts();
        for (int i = 0; i < timeSeries.size(); i++) {
            series.add(timestamps[i], costs[i]);
        }
        return series;
    }

    public CandidateFactory<E> getCandidateFactory() {
        return candidateFactory;
    }

    public int getDimension() {
        return evaluator.getDimension();
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

    @Override
    public String toString() {
        return name;
    }

}
