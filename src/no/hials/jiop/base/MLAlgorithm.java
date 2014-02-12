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
 *
 * @author Lars Ivar Hatledal
 */
public abstract class MLAlgorithm<E> {

    private final Object mutex = new Object();
    private final ExecutorService pool;

    private final Evaluator<E> evaluator;
    private final CandidateFactory<E> candidateFactory;
    private final EncodingFactory<E> encodingFactory;
    protected final MLHistory history;

    private Candidate<E> bestCandidate;

    public MLAlgorithm(EncodingFactory<E> encodingFactory, Evaluator<E> evaluator) {
        this.history = new MLHistory();
        this.encodingFactory = encodingFactory;
        this.evaluator = evaluator;
        this.candidateFactory = new CandidateFactory<>(encodingFactory, evaluator);
        this.pool = Executors.newCachedThreadPool();
    }

    protected abstract void internalIteration();

    public abstract String getName();

    public EvaluatedCandidate<E> runFor(int iterations) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
        } while (it++ < iterations);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public EvaluatedCandidate<E> runFor(long time) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
            it++;
        } while (System.currentTimeMillis() - t0 < time);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public EvaluatedCandidate<E> runFor(double error) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
            it++;
        } while (getBestCandidate().getCost() > error);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public double iteration() {
        long t0 = System.nanoTime();
        internalIteration();
        double t = (double) (System.nanoTime() - t0) / 1000000;
        history.add(getBestCandidate().getCost(), t);
        return t;
    }

    public void reset(boolean clearHistory) {
        this.bestCandidate = null;
        if (clearHistory) {
            history.clear();
        }
        setBestCandidate(getCandidateFactory().getRandomCandidate());
    }

    public void reset(List<E> seed, boolean clearHistory) {
        this.bestCandidate = null;
        if (clearHistory) {
            history.clear();
        }
        setBestCandidate(getCandidateFactory().toCandidate(seed.get(0)));
    }

    public Candidate<E> getBestCandidate() {
        synchronized (mutex) {
            return new Candidate<>(bestCandidate);
        }
    }

    public void setBestCandidate(Candidate<E> candidate) {
        synchronized (mutex) {
            if (this.bestCandidate == null) {
                this.bestCandidate = new Candidate<>(candidate);
            } else if (candidate.getCost() < this.bestCandidate.getCost()) {
                this.bestCandidate = new Candidate<>(candidate);
            }
        }
    }

    public void evaluateAll(Iterable<Candidate<E>> candidates) {
        List<Runnable> jobs = new LinkedList<>();
        
        for (final Candidate<E> c : candidates) {
            jobs.add(new Runnable() {

                @Override
                public void run() {
                    double evaluate = getEvaluator().evaluate(c);
                    c.setCost(evaluate);
                }
            });
        }
        submitJobs(jobs);
    }

    public Plot2DPanel getPlot() {
        Plot2DPanel plot = new Plot2DPanel();
        plot.addLegend("INVISIBLE");
        plot.addLinePlot("", getHistory().getTimestamps(), getHistory().getCosts());
        return plot;
    }

    public void warmUp(long millis) {
        reset(true);
        runFor(millis);
        reset(true);
    }

    public CandidateFactory getCandidateFactory() {
        return candidateFactory;
    }

    public EncodingFactory<E> getEncodingFactory() {
        return encodingFactory;
    }

    public MLHistory getHistory() {
        return history;
    }

    public Evaluator<E> getEvaluator() {
        return evaluator;
    }

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
