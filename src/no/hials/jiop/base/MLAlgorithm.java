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
package no.hials.jiop.base;

import no.hials.jiop.base.MLHistory.MLHistory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.base.candidates.CandidateFactory;
import no.hials.jiop.base.candidates.encoding.factories.EncodingFactory;
import org.math.plot.Plot2DPanel;

/**
 * Base class for all JIOP algorithms
 *
 * @author Lars Ivar Hatledal
 */
public abstract class MLAlgorithm<E> {

    private final Object mutex = new Object();
    private final ExecutorService pool;

    private Evaluator<E> evaluator;
    private final CandidateFactory<E> candidateFactory;
    private final EncodingFactory<E> encodingFactory;
    protected final MLHistory history;

    protected boolean trackHistory = false;
    private Candidate<E> bestCandidate;

    /**
     * Initializes the MLAlgorithm
     *
     * @param encodingFactory the EncodingFactory in charge of creating new
     * Encodings
     * @param evaluator the Evaluator in charge of evaluation
     * @see Evaluator
     * @see EncodingFactory
     */
    public MLAlgorithm(EncodingFactory<E> encodingFactory, Evaluator<E> evaluator) {
        this.history = new MLHistory();
        this.encodingFactory = encodingFactory;
        this.evaluator = evaluator;
        this.candidateFactory = new CandidateFactory<>(encodingFactory, evaluator);
        this.pool = Executors.newCachedThreadPool();
    }

    /**
     * The general contract of this method is that implementations does a single
     * optimization run. No while loops etc. This is handles by the base class.
     */
    protected abstract void internalIteration();

    /**
     * Implementations should return the name of the algorithm
     *
     * @return the name of the algorithm
     */
    public abstract String getName();

    /**
     * Runs the algorithm for the specified number of iterations
     *
     * @param iterations the number of iterations to run
     * @return the result
     */
    public EvaluatedCandidate<E> runFor(int iterations) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
        } while (it++ < iterations);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    /**
     * Runs the algorithm for the specified amount of time
     *
     * @param time how long the algorithm should run (measured in milliseconds)
     * @return the result
     */
    public EvaluatedCandidate<E> runFor(long time) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
            it++;
        } while (System.currentTimeMillis() - t0 < time);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    /**
     * Runs the algorithm as long as the error is above the threshold (or the
     * timeout kicks in)
     *
     * @param error the error threshold
     * @param timeOut how long we allow the algorithm to run before returning
     * @return the result
     */
    public EvaluatedCandidate<E> runFor(double error, long timeOut) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
            it++;
        } while (getBestCandidate().getCost() > error & (System.currentTimeMillis() - t0) < timeOut);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    /**
     * Does a single iteration of the algorithm
     *
     * @return the time it took to do the iteration
     */
    public double iteration() {
        long t0 = System.nanoTime();
        internalIteration();
        double t = (double) (System.nanoTime() - t0) / 1000000;
        if (trackHistory) {
            history.add(getBestCandidate().getCost(), t);
        }
        return t;
    }

    /**
     * Resets the algorithm
     *
     */
    public void reset() {
        this.bestCandidate = null;
        setBestCandidate(getCandidateFactory().getRandomCandidate());
    }

    /**
     * Clears the history
     */
    public void clearHistory() {
        history.clear();
    }

    /**
     * Should we record the history?
     *
     * @param doTrack whether or not we should
     */
    public void doTrackHistory(boolean doTrack) {
        trackHistory = doTrack;
    }

    /**
     * Getter for the tracking status
     *
     * @return true if we are storing the history, false otherwise
     */
    public boolean isTrackingHistory() {
        return trackHistory;
    }

    /**
     * Resets the algorithm and
     *
     * @param seed the seed to use
     */
    public void reset(List<E> seed) {
        this.bestCandidate = null;
        setBestCandidate(getCandidateFactory().toCandidate(seed.get(0)));
    }

    /**
     * Getter for the best candidate (synchronized action)
     *
     * @return the best candidate
     */
    public Candidate<E> getBestCandidate() {
        synchronized (mutex) {
            return new Candidate<>(bestCandidate);
        }
    }

    /**
     * Sets the best candidate (synchronized action)
     *
     * @param candidate the new best candidate
     */
    public void setBestCandidate(Candidate<E> candidate) {
        synchronized (mutex) {
            if (this.bestCandidate == null) {
                this.bestCandidate = new Candidate<>(candidate);
            } else if (candidate.getCost() < this.bestCandidate.getCost()) {
                this.bestCandidate = new Candidate<>(candidate);
            }
        }
    }

    /**
     * Evaluates and updates all the supplied candidates (multi-threaded
     * operation)
     *
     * @param candidates the candidates to evaluate
     */
    public void evaluateAll(Iterable<Candidate<E>> candidates) {
        List<Runnable> jobs = new LinkedList<>();

        for (final Candidate<E> c : candidates) {
            jobs.add((Runnable) () -> {
                double evaluate = getEvaluator().evaluate(c);
                c.setCost(evaluate);
            });
        }
        submitJobs(jobs);
    }

    /**
     * Get a plot showing the data from the MLHistory
     *
     * @return a JPanel instance with the plot
     */
    public Plot2DPanel getPlot() {
        Plot2DPanel plot = new Plot2DPanel();
        plot.addLegend("INVISIBLE");
        plot.addLinePlot("", getHistory().getTimestamps(), getHistory().getCosts());
        return plot;
    }

    /**
     * Warms up the JVM by running the algorithm for the specified time. The
     * algorithm is reset before and after.
     *
     * @param millis how long we run the algorithm
     */
    public void warmUp(long millis) {
        reset();
        runFor(millis);
        reset();
    }

    /**
     * Getter for the canidateFactory
     *
     * @return the CandidateFactory used by this instance
     * @see CandidateFactory
     */
    public CandidateFactory getCandidateFactory() {
        return candidateFactory;
    }

    /**
     * Getter for the encodingFactory
     *
     * @return the EncodingFactory used by this instance
     * @see EncodingFactory
     */
    public EncodingFactory<E> getEncodingFactory() {
        return encodingFactory;
    }

    /**
     * Getter for the
     *
     * @return
     */
    public MLHistory getHistory() {
        return history;
    }

    /**
     * Getter for the evaluator
     *
     * @return the Evaluator used by this instance
     */
    public Evaluator<E> getEvaluator() {
        return evaluator;
    }

    /**
     * Sets the evaluator
     *
     * @param evaluator the new evaluator to be used
     */
    public void setEvaluator(Evaluator<E> evaluator) {
        this.evaluator = evaluator;
        this.candidateFactory.setEvaluator(evaluator);
    }

    /**
     * Uses a thread pool to finish all jobs
     *
     * @param runnables jobs that can be multi-threaded
     */
    public void submitJobs(final List<Runnable> runnables) {
        final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
        for (Runnable job : runnables) {
            completionService.submit(job, true);
        }
        for (Runnable job : runnables) {
            try {
                completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MLAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            for (int i = 0; i < history.size(); i++) {
                sb.append(history.getIterations()[i]).append("\t").append(history.getTimestamps()[i]).append("\t").append(history.getCosts()[i]).append("\n");
            }
            bw.write(sb.toString());
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
