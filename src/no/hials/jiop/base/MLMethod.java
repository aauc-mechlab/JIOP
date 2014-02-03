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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.candidates.BasicEncoding;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.EvaluatedCandidate;
import no.hials.jiop.exceptions.NoAverageException;
import no.hials.jiop.exceptions.NotInitializedException;
import no.hials.jiop.factories.AbstractCandidateFactory;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class MLMethod<E> extends ArrayList<Candidate<E>> {

    private final MLHistory history, avgHistory;
    protected final int size, candidateLength;
    private final AbstractCandidateFactory<E> factory;

    private final boolean hasAverage;

    private Candidate<E> bestCandidate = null;
    private boolean initialized = false;

    public MLMethod(int size, int candiateLength, AbstractCandidateFactory<E> factory) {
        this.size = size;
        this.factory = factory;
        this.candidateLength = candiateLength;
        this.history = new MLHistory();
        this.hasAverage = size > 1;
        this.avgHistory = hasAverage ? new MLHistory() : null;
    }

    protected abstract void doIteration();

    public abstract String getName();

    public void warmUp(long millis) {
        reset(true);
        runFor(millis);
        reset(true);
    }

    public void setBestCandidate(Candidate<E> bestCandidate) {
        this.bestCandidate = new Candidate<>(bestCandidate);
    }

    public MLMethod<E> sortCandidates() {
        if (!initialized) {
            throw new NotInitializedException("Container not yet initialized!");
        }
        Collections.sort(this);
        return this;
    }

    public MLMethod<E> updateCost() {
        if (!initialized) {
            throw new NotInitializedException("Container not yet initialized!");
        }
        factory.updateCost(this);
        setBestCandidate(sortCandidates().get(0));
        return this;
    }

    public double getAverageCost() {
        if (!initialized) {
            throw new NotInitializedException("Container not yet initialized!");
        }
        double avg = 0;
        for (Candidate<E> c : this) {
            avg += c.getCost();
        }
        return avg / size();
    }

    public Candidate<E> getBestCandidate() {
        if (!initialized) {
            throw new NotInitializedException("Container not yet initialized!");
        }
        return new Candidate<>(bestCandidate);
    }

    public AbstractCandidateFactory<E> getFactory() {
        return factory;
    }

    public double evaluate(E encoding) {
        return getFactory().evaluate(encoding);
    }

    public MLHistory getHistory() {
        return history;
    }

    public MLHistory getAvgHistory() {
        if (!hasAverage) {
            throw new NoAverageException("The current method does not contain an average!");
        }
        return avgHistory;
    }

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
        } while (getBestCandidate().getCost() > error);
        return new EvaluatedCandidate<>(getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public void iteration() {
        long t0 = System.nanoTime();
        doIteration();
        long t = System.nanoTime() - t0;
        history.add(getBestCandidate().getCost(), t);
        if (hasAverage) {
            avgHistory.add(getAverageCost(), t);
        }
    }

    public void reset(boolean clearHistory) {
        super.clear();
        if (clearHistory) {
            history.clear();
            if (hasAverage) {
                avgHistory.clear();
            }
        }
        initialize(size);
    }

    public void reset(List<E> initials, boolean clearHistory) {
        super.clear();
        if (clearHistory) {
            history.clear();
            if (hasAverage) {
                avgHistory.clear();
            }
        }
        initialize(initials, size);
    }

    public boolean hasAverage() {
        return hasAverage;
    }

    private MLMethod<E> initialize(int howMany) {
        clear();
        addAll(factory.createCandidates(howMany, candidateLength));
        initialized = true;
        setBestCandidate(sortCandidates().get(0));
        return this;
    }

    private MLMethod<E> initialize(List<E> initials, int howMany) {
        clear();
        int i = 0;
        for (; i < initials.size(); i++) {
            Candidate<E> cand = factory.createCandidate(initials.get(i));
            this.add(cand);
        }
        for (; i < howMany; i++) {
            Candidate<E> cand = factory.generateRandom(candidateLength);
            this.add(cand);
        }
        initialized = true;
        setBestCandidate(sortCandidates().get(0));
        return this;
    }

    public void dumpHistoryToFile(String dir, String fileName) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        try (
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "//" + fileName)))) {
            for (int i = 0; i < history.size(); i++) {
                if (hasAverage) {
                    bw.write(history.getIterations()[i] + "\t" + history.getTimestampsL()[i] + "\t" + history.getCosts()[i] + "\t" + avgHistory.getCosts()[i] + "\n");
                } else {
                    bw.write(history.getIterations()[i] + "\t" + history.getTimestampsL()[i] + "\t" + history.getCosts()[i] + "\n");
                }

            }
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i != size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

}
