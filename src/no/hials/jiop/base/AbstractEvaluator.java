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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.Encoding;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public abstract class AbstractEvaluator<E> {

    private final ExecutorService pool;
    private final ExecutorCompletionService completionService;

    private final boolean multiThreaded;

    public AbstractEvaluator() {
        this(1);
    }

    public AbstractEvaluator(int numThreads) {
        if (numThreads == 1 | numThreads < 1) {
            this.completionService = null;
            this.multiThreaded = false;
            this.pool = null;
        } else {
            this.multiThreaded = true;
            this.pool = Executors.newFixedThreadPool(numThreads);
            this.completionService = new ExecutorCompletionService(pool);
        }
    }

    public abstract double evaluate(E variables);

    public double evaluate(Candidate<E> candidate) {
        return evaluate(candidate.getVariables());
    }

    public double evaluate(Encoding<E> encoding) {
        return evaluate(encoding.getVariables());
    }

    public void evaluateAll(Iterable<Candidate<E>> candidates) {
        if (!multiThreaded) {
            for (Candidate<E> candidate : candidates) {
                candidate.setCost(evaluate(candidate));
            }
        } else {
            for (Candidate<E> candidate : candidates) {
                completionService.submit(new EvaluateCall(candidate), true);
            }
            for (Candidate<E> candidate : candidates) {
                try {
                    completionService.take().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AbstractEvaluator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class EvaluateCall implements Runnable {

        private final Candidate<E> candidate;

        public EvaluateCall(Candidate<E> candidate) {
            this.candidate = candidate;
        }

        @Override
        public void run() {
            candidate.setCost(evaluate(candidate));
        }

    }

}
