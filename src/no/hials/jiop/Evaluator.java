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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.candidates2.Candidate;
import no.hials.jiop.candidates2.Encoding;

/**
 * Interface used for evaluating performance of the candidates
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public abstract class Evaluator<E> {

    private final int dimension;

    private ExecutorService pool;
    private ExecutorCompletionService completionService;

    public Evaluator(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    private ExecutorCompletionService<Candidate<E>> getCompletionService() {
        if (pool == null) {
            this.pool = Executors.newCachedThreadPool();
            this.completionService = new ExecutorCompletionService(pool);
        }
        return completionService;
    }

    
     public Candidate<E> evaluate(Encoding<E> encoding) {
        return new Candidate<>(encoding, getCost(encoding.getElements()));
    }

    public List<Candidate<E>> evaluate(Collection<Encoding<E>> encodings, boolean multiThreaded) {
        List<Candidate<E>> evaluatedCandidates = new ArrayList<>(encodings.size());

        for (Encoding<E> e : encodings) {
            if (!multiThreaded) {
                evaluatedCandidates.add(evaluate(e));
            } else {
                getCompletionService().submit(() -> evaluate(e));
            }
        }
        if (multiThreaded) {
            for (int i = 0; i < encodings.size(); i++) {
                try {
                    evaluatedCandidates.add(getCompletionService().take().get());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return Collections.unmodifiableList(evaluatedCandidates);
    }

    public abstract double getCost(E elements);
}
