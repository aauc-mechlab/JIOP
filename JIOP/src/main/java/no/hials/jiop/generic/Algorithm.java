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
package no.hials.jiop.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.generic.candidates.Candidate;
import no.hials.jiop.generic.candidates.CandidateSolution;
import no.hials.jiop.generic.factories.CandidateFactory;
import no.hials.jiop.history.MLHistory;
import no.hials.jiop.generic.temination.TerminationCriteria;
import no.hials.jiop.generic.temination.TerminationData;
import no.hials.jiop.generic.temination.TimeElapsedCriteria;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E> the type
 */
public abstract class Algorithm<E> implements Serializable {

    protected final Random rng = new Random();
    protected final static int availableProcessors = Runtime.getRuntime().availableProcessors();

    private final CandidateFactory<E> candidateFactory;

    private String name;
    private boolean trackCandidatePerformance;
    private Evaluator<E> evaluator;

    private MLHistory timeSeries;

    private ExecutorService pool;
    private ExecutorCompletionService completionService;

    private Candidate<E> bestCandidate;

    public Algorithm(CandidateFactory<E> candidateFactory, Evaluator<E> evaluator, String name) {
        this.name = name;
        this.candidateFactory = candidateFactory;
        this.evaluator = evaluator;
        this.timeSeries = new MLHistory();
    }

    protected abstract Candidate<E> subInit();

    protected abstract Candidate<E> subInit(List<E> seeds);

    protected abstract void singleIteration();

    /**
     * Initializes the algorithm. One of the init() methods must be invoked
     * before the algorithm can be used
     */
    public final void init() {
        this.bestCandidate = null;
        setBestCandidateIfBetter(subInit());
    }

    /**
     * Initializes the algorithm as usual, but implementations should make use
     * of the seeds One of the init() methods must be invoked before the
     * algorithm can be used
     *
     * @param seeds seeds to be inserted into the population
     */
    public final void init(E... seeds) {
        if (seeds == null) {
            init();
        } else if (seeds.length == 0) {
            init();
        } else {
            this.bestCandidate = null;
            setBestCandidateIfBetter(subInit(Arrays.asList(seeds)));
        }
    }

    /**
     * Initializes the algorithm as usual, but implementations should make use
     * of the seeds One of the init() methods must be invoked before the
     * algorithm can be used
     *
     * @param seeds seeds to be inserted into the population
     */
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

    /**
     * Evaluates all the candidates in the list
     *
     * @param candidates candidates to evaluate
     * @return the same list, where all of the candudates have gotten their cost
     * updated
     */
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

    /**
     * Clears the performance history of the candidated
     */
    public void clearHistory() {
        this.timeSeries = new MLHistory();
    }

    /**
     * Getter for the current best candidate
     *
     * @return the best found candidate yet
     */
    public synchronized Candidate<E> getBestCandidate() {
        return bestCandidate.copy();
    }

    /**
     * Getter for the current best cost
     *
     * @return the best found cost yet
     */
    public synchronized double getBestCost() {
        return bestCandidate.getCost();
    }

    /**
     * Given a candidate, updates the best candidate if the new one is better
     *
     * @param candidate the candidate to test
     */
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

    /**
     * Tries to minimize the cost function suppiled to the algorithm. The
     * stopping criterias decides when the algorithm should return. If no such
     * criteria is supplied, a default TimeElapsedCriteria is used. It will then
     * return after approx. 100 ms.
     *
     * @param criterias stopping criterias
     * @return the best found solution
     */
    public CandidateSolution compute(TerminationCriteria... criterias) {
        if (evaluator == null) {
            throw new RuntimeException("Error: No evaluator supplied yet!");
        }
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
            timeElapsed = (System.currentTimeMillis() - t0);
            bestCost = getBestCandidate().getCost();
            numIterations++;
            for (TerminationCriteria tc : criterias) {
                if (tc.souldTerminate(new TerminationData(bestCost, timeElapsed, numIterations))) {
                    terminate = true;
                }
            }
            long end = (System.nanoTime() - start);
            if (trackCandidatePerformance) {
                timeSeries.add(end, bestCost);
            }
        }
        return new CandidateSolution(getBestCandidate(), getBestCandidate().getCost(), numIterations, timeElapsed);
    }

    /**
     * Writes the MLHistory data to file. If the directory does not exist, a new
     * one will be created.
     *
     * @param dir the directory
     */
    public void dumpHistoryToFile(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM_dd_HH-mm");
        Calendar calendar = new GregorianCalendar();
        String fileName = dir + "//" + toString() + " " + sdf.format(calendar.getTime()) + ".csv";

        StringBuilder sb = new StringBuilder();
        try (
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            for (int i = 0; i < timeSeries.size(); i++) {
                sb.append(timeSeries.getIterations()[i]).append("\t").append(timeSeries.getTimestamps()[i]).append("\t").append(timeSeries.getCosts()[i]).append("\n");
            }
            bw.write(sb.toString());
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Algorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public XYSeries getSeries() {
        XYSeries series = new XYSeries(toString());
        if (timeSeries.isEmpty()) {
            System.err.println("No history have been recorded! Have you invoked setTrackCandidatePerformance(true)?");
            return series;
        }

        double[] timestamps = timeSeries.getTimestamps();
        double[] costs = timeSeries.getCosts();
        for (int i = 0; i < timeSeries.size(); i++) {
            series.add(timestamps[i], costs[i]);
        }
        return series;
    }

    /**
     * Getter for the candidate factory
     *
     * @return the algorithms candidate factory
     */
    public CandidateFactory<E> getCandidateFactory() {
        return candidateFactory;
    }

    /**
     * Getter for the dimensionalty of the problem to be solved
     *
     * @return problem dimensionality
     */
    public int getDimension() {
        return evaluator.getDimension();
    }

    /**
     * Getter for the evaluator
     *
     * @return the evaluator
     */
    public Evaluator<E> getEvaluator() {
        return evaluator;
    }

    /**
     * Sets a new evaluator.
     *
     * @param evaluator the new evalutor
     */
    public void setEvaluator(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
    }

    public boolean isTrackingCandidatePerformance() {
        return trackCandidatePerformance;
    }

    public void setTrackCandidatePerformance(boolean trackCandidatePerformance) {
        this.trackCandidatePerformance = trackCandidatePerformance;
    }

    /**
     * Sets a new name of the algorithm
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
