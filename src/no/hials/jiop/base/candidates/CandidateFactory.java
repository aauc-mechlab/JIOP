/*
 * Copyright (c) 2014, Lars Ivar
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
package no.hials.jiop.base.candidates;

import no.hials.jiop.base.candidates.encoding.factories.EncodingFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.AbstractEvaluator;
import no.hials.jiop.base.MLAlgorithm;
import no.hials.jiop.base.candidates.encoding.Encoding;

/**
 *
 * @author Lars Ivar
 */
public class CandidateFactory<E> {

    private final ExecutorService pool;
    private final AbstractEvaluator<E> evaluator;
    private final EncodingFactory<E> encodingFactory;

    public CandidateFactory(EncodingFactory<E> encodingFactory, AbstractEvaluator<E> evaluator) {
        this.evaluator = evaluator;
        this.encodingFactory = encodingFactory;
        this.pool = Executors.newCachedThreadPool();
    }

    public Candidate<E> getRandomCandidate() {
        Encoding<E> random = encodingFactory.getRandomEncoding();
        return new Candidate<>(random, evaluator.evaluate(random.getVariables()));
    }

    public Candidate<E> getNeighborCandidate(Candidate<E> original, double proximity) {
        Encoding<E> neighbor = encodingFactory.getNeighborEncoding(original.getVariables(), proximity);
        return new Candidate<>(neighbor, evaluator.evaluate(neighbor.getVariables()));
    }

    public Candidate<E> toCandidate(Encoding<E> encoding) {
        return new Candidate<>(encoding, evaluator.evaluate(encoding.getVariables()));
    }

    public Candidate<E> toCandidate(E variables) {
        Encoding<E> encoding = encodingFactory.getWrapVariables(variables);
        return new Candidate<>(encoding, evaluator.evaluate(variables));
    }

    public List<Candidate<E>> getRandomCandidateList(int size) {
        final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
        final List<Candidate<E>> candidates = Collections.synchronizedList(new ArrayList<Candidate<E>>(size));
        for (int i = 0; i < size; i++) {
            completionService.submit(new Runnable() {

                @Override
                public void run() {
                        candidates.add(getRandomCandidate());
                }
            }, true);
        }
        for (int i = 0; i < size; i++) {
            try {
                completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MLAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return candidates;
    }

    public List<Candidate<E>> getNeighborCandidateList(final Candidate<E> original, final double proximity, int size) {
        final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
        final List<Candidate<E>> candidates = Collections.synchronizedList(new ArrayList<Candidate<E>>(size));
        for (int i = 0; i < size; i++) {
            completionService.submit(new Runnable() {

                @Override
                public void run() {
                    candidates.add(getNeighborCandidate(original, proximity));
                }
            }, true);
        }
        for (int i = 0; i < size; i++) {
            try {
                completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MLAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return candidates;
    }

    public List<Candidate<E>> toCandidateList(List<E> variables) {
        final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
        final List<Candidate<E>> candidates = (new ArrayList<>(variables.size()));
        for (final E e : variables) {
            completionService.submit(new Runnable() {

                @Override
                public void run() {
                    synchronized(candidates) {
                    candidates.add(toCandidate(e));
                    }
                }
            }, true);
        }
        for (int i = 0; i < variables.size(); i++) {
            try {
                completionService.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(MLAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return candidates;
    }
}
