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

import no.hials.jiop.base.candidates.containers.CandidateContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.candidates.EvaluatedCandidate;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class MLMethod<E> {

    private final CandidateContainer<E> container;
    private final MLHistory history, avgHistory;

    public MLMethod(CandidateContainer<E> container) {
        this.history = new MLHistory();
        this.avgHistory = container.size() > 1 ? new MLHistory() : null;
        this.container = container;

    }

    protected abstract void doIteration();


    public abstract String getName();

    public void warmUp(long millis) {
        runFor(millis);
        reset(true);
    }

    public CandidateContainer<E> getContainer() {
        return container;
    }

    public double evaluate(E encoding) {
        return getContainer().evaluate(encoding);
    }

    public MLHistory getHistory() {
        return history;
    }

    public MLHistory getAvgHistory() {
        return avgHistory;
    }

    public EvaluatedCandidate<E> runFor(int iterations) {
        int it = 0;
        long t0 = System.nanoTime();
        do {
            iteration();
        } while (it++ < iterations);
        return new EvaluatedCandidate<>(getContainer().getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public EvaluatedCandidate<E> runFor(long time) {
        int it = 0;
        long t0 = System.currentTimeMillis();
        do {
            iteration();
            it++;
        } while (System.currentTimeMillis() - t0 < time);
        return new EvaluatedCandidate<>(getContainer().getBestCandidate(), it, System.currentTimeMillis() - t0);
    }



    public EvaluatedCandidate<E> runFor(double error) {
        int it = 0;
        long t0 = System.nanoTime();
        do {
            iteration();
            it++;
        } while (getContainer().getBestCandidate().getCost() > error);
        return new EvaluatedCandidate<>(getContainer().getBestCandidate(), it, System.currentTimeMillis() - t0);
    }

    public void iteration() {
        long t0 = System.nanoTime();
        doIteration();
        long t = System.nanoTime() - t0;
        history.add(getContainer().getBestCandidate().getCost(), t);
        if (container.size() > 1) {
            avgHistory.add(container.getAverage(), t);
        }
    }

    public void reset(boolean clearHistory) {
        if (clearHistory) {
            history.clear();
            if (avgHistory != null) {
                avgHistory.clear();
            }
        }
        container.initialize();
    }

    public void reset(List<E> seed, boolean clearHistory) {
        if (clearHistory) {
            history.clear();
            if (avgHistory != null) {
                avgHistory.clear();
            }
        }
        container.initialize(seed);
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
                    sb.append(history.getIterations()[i]).append("\t").append(history.getTimestampsL()[i]).append("\t").append(history.getCosts()[i]).append("\t").append(avgHistory.getCosts()[i]).append("\n");
                } else {
                    sb.append(history.getIterations()[i]).append("\t").append(history.getTimestampsL()[i]).append("\t").append(history.getCosts()[i]).append("\n");
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
