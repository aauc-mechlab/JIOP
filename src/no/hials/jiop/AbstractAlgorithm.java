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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.candidates2.Candidate;
import no.hials.jiop.candidates2.Encoding;
import no.hials.jiop.candidates2.Solution;
import no.hials.jiop.factories.EncodingFactory;
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

    private String name;
    private Evaluator<E> evaluator;
    private EncodingFactory<E> encodingFactory;

    private MLHistory timeSeries;

    private ExecutorService pool;
    private ExecutorCompletionService completionService;

    private Candidate<E> bestCandidate;
    protected int size;
    protected boolean multiThreaded;

    protected List<Candidate<E>> candidates = new ArrayList<>();

    public AbstractAlgorithm(EncodingFactory<E> encodingFactory, Evaluator<E> evaluator, String name) {
        this(1, encodingFactory, evaluator, name);
    }

    public AbstractAlgorithm(int size, EncodingFactory<E> encodingFactory, Evaluator<E> evaluator, String name) {
        this(size, encodingFactory, evaluator, name, false);
    }

    public AbstractAlgorithm(int size, EncodingFactory<E> encodingFactory, Evaluator<E> evaluator, String name, boolean multiThreaded) {
        this.name = name;
        this.size = size;
        this.encodingFactory = encodingFactory;
        this.evaluator = evaluator;
        this.timeSeries = new MLHistory();
        this.multiThreaded = multiThreaded;
    }

    public final void init() {
        List<Encoding<E>> generateInitialPopulation = encodingFactory.generateInitialPopulation(size, evaluator.getDimension());
        List<Candidate<E>> evaluate = getEvaluator().evaluate(generateInitialPopulation, true);
        candidates.addAll(evaluate);
        Collections.sort(candidates);
        this.bestCandidate = candidates.get(0);
    }

    public final void init(List<E> seeds) {
        if (seeds == null || seeds.isEmpty()) {
            init();
        } else {
            List<Encoding<E>> generateInitialPopulation = encodingFactory.generateInitialPopulation(size, evaluator.getDimension(), seeds);
            List<Candidate<E>> evaluate = getEvaluator().evaluate(generateInitialPopulation, true);
            candidates.addAll(evaluate);
            Collections.sort(candidates);
            this.bestCandidate = candidates.get(0);
        }
    }

//    protected abstract void candidateUpdate();
    protected abstract void candidateUpdate(Candidate<E> c);

//    protected abstract Candidate<E> subInit();
//
//    protected abstract Candidate<E> subInit(List<E> seeds);
    /**
     * Evaluates the cost of the given candidate and returns it This is the same
     * as to call getEvaluator().evaluate(candidate) Note: This call does not
     * update the candidates affiliated cost.
     *
     * @param candidate the candidate to evaluate
     * @return the candidates cost
     */
    public double evaluate(E candidate) {
        return getEvaluator().getCost(candidate);
    }

    /**
     * Evaluates and updates the cost of the given candidate. This is the same
     * as to call candidate.setCost(getEvaluator().evaluate(candidate))
     *
     * @param encoding
     * @return the updated candidate instance
     */
    public Candidate<E> evaluateAndUpdate(Encoding<E> encoding) {
        return getEvaluator().evaluate(encoding);
    }

    /**
     * Creates and returns a new candidate. Uses reflection to construct a new
     * instance of the template class
     *
     * @return a new Candidate instance initialized with random values
     */
    public Candidate<E> newCandidate() {
        return evaluator.evaluate(encodingFactory.generateRandom(evaluator.getDimension()));
    }

    public double getAverage() {
        if (candidates.isEmpty()) {
            return 0;
        } else {
            double cost = 0;
            for (Candidate<E> c : candidates) {
                cost += c.getCost();
            }
            return cost / candidates.size();
        }
    }

//    public Candidate<E> newCandidate(E e) {
//        try {
//            Constructor<?> constructor = templateClass.getConstructor(e.getClass());
//            Candidate<E> newInstance = (Candidate) constructor.newInstance(e);
//            return evaluateAndUpdate(newInstance);
//        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    public void clearHistory() {
        this.timeSeries = new MLHistory();
    }

    public Candidate<E> getBestCandidate() {
        return bestCandidate;
    }

//    public synchronized double getBestCost() {
//        return bestCandidate.getCost();
//    }
    public synchronized void setBestCandidateIfBetter(Candidate<E> candidate) {
        if (bestCandidate == null) {
            bestCandidate = candidate;
        } else {
            if (candidate.getCost() < bestCandidate.getCost()) {
                this.bestCandidate = candidate;
            }
        }
    }

    protected CompletionService getCompletionService() {
        if (pool == null) {
//            pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            pool = Executors.newCachedThreadPool();
            completionService = new ExecutorCompletionService(pool);
        }
        return completionService;
    }

    public Solution<E> compute(TerminationCriteria... criterias) {
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

            if (multiThreaded) {
                for (Candidate<E> c : candidates) {
                    getCompletionService().submit(() -> candidateUpdate(c), null);
                }
                for (Candidate<E> c : candidates) {
                    try {
                        getCompletionService().take().get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                for (Candidate<E> c : candidates) {
                    candidateUpdate(c);
                }
            }

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
        return new Solution<>(getBestCandidate(), timeElapsed, numIterations);
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
        XYSeries series = new XYSeries(getName());
        double[] timestamps = timeSeries.getTimestamps();
        double[] costs = timeSeries.getCosts();
        for (int i = 0; i < timeSeries.size(); i++) {
            series.add(timestamps[i], costs[i]);
        }
        return series;
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
