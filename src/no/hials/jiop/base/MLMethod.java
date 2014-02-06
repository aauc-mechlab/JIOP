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
import no.hials.jiop.base.candidates.containers.CandidateContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.base.candidates.factories.CandidateFactory;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class MLMethod<E> {

    private final Object mutex = new Object();
    private final ExecutorCompletionService completionService = new ExecutorCompletionService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    private final CandidateFactory<E> factory;
    private final CandidateContainer<E> container;
    private final MLHistory history, avgHistory;

    private final AbstractEvaluator<E> evaluator;

    private Candidate<E> bestCandidate;

    public MLMethod(CandidateFactory<E> factory, CandidateContainer<E> container, AbstractEvaluator<E> evaluator) {
        this.avgHistory = container.size() > 1 ? new MLHistory() : null;
        this.history = new MLHistory();
        this.evaluator = evaluator;
        this.factory = factory;
        this.container = container;
        this.factory.setOwner(this);

    }

    public MLMethod(CandidateFactory<E> factory, AbstractEvaluator<E> evaluator) {
        this.history = new MLHistory();
        this.evaluator = evaluator;
        this.factory = factory;
        this.container = null;
        this.avgHistory = null;
        this.factory.setOwner(this);
    }

    public abstract void internalIteration();

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

    public void iteration() {
        long t0 = System.nanoTime();
        internalIteration();
        double t = (double)(System.nanoTime() - t0)/1000000;
        history.add(getBestCandidate().getCost(), t);
        if (container != null) {
            avgHistory.add(container.getAverage(), t);
        }
    }

    public void reset(boolean clearHistory) {
        this.bestCandidate = null;
        if (clearHistory) {
            history.clear();
            if (avgHistory != null) {
                avgHistory.clear();
            }
        }
        if (container != null) {
            getContainer().clearAndAddAll(getFactory().randomCandidates(getContainer().size()));
            setBestCandidate(getContainer().sort().get(0));
        } else {
            setBestCandidate(getFactory().randomCandidate());
        }
    }

    public void reset(List<E> seed, boolean clearHistory) {
        this.bestCandidate = null;
        if (clearHistory) {
            history.clear();
            if (avgHistory != null) {
                avgHistory.clear();
            }
        }
        if (container != null) {
            List<Candidate<E>> candidates = new ArrayList<>(getContainer().size());
            candidates.addAll(getFactory().toCandidates(seed));
            candidates.addAll(getFactory().randomCandidates(getContainer().size() - candidates.size()));
            getContainer().clearAndAddAll(candidates);
            setBestCandidate(getContainer().sort().get(0));
        } else {
            setBestCandidate(getFactory().toCandidate(seed.get(0)));
        }
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

    public void warmUp(long millis) {
        reset(true);
        runFor(millis);
        reset(true);
    }

    public CandidateFactory getFactory() {
        return factory;
    }

    public CandidateContainer<E> getContainer() {
        return container;
    }

    public int encodingLength() {
        return factory.getEncodingLength();
    }

    public MLHistory getHistory() {
        return history;
    }

    public MLHistory getAvgHistory() {
        return avgHistory;
    }

    public AbstractEvaluator<E> getEvaluator() {
        return evaluator;
    }

    public ExecutorCompletionService getCompletionService() {
        return completionService;
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
                if (avgHistory != null) {
                    sb.append(history.getIterations()[i]).append("\t").append(history.getTimestampsD()[i]).append("\t").append(history.getCosts()[i]).append("\t").append(avgHistory.getCosts()[i]).append("\n");
                } else {
                    sb.append(history.getIterations()[i]).append("\t").append(history.getTimestampsD()[i]).append("\t").append(history.getCosts()[i]).append("\n");
                }

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
