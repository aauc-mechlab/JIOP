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
package no.hials.jiop.base.algorithms.evolutionary.de;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.jiop.base.candidates.Candidate;
import java.util.Random;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.PopulationBasedMLAlgorithm;
import no.hials.jiop.base.candidates.encoding.Encoding;
import no.hials.jiop.base.candidates.encoding.factories.EncodingFactory;

/**
 * A Differential Evolution implementation
 *
 * @author Lars Ivar Hatledal
 * @param <E> the type
 */
public class DE<E> extends PopulationBasedMLAlgorithm<E> {

    private final Random rng = new Random();
    private final DifferentialCrossover<E> crossover;

    public DE(int size, DifferentialCrossover<E> crossover, EncodingFactory<E> factory, Evaluator<E> evaluator) {
        super(size, factory, evaluator);
        this.crossover = crossover;
    }

    @Override
    public void internalIteration() {
        final List<Runnable> jobs = new ArrayList<>(getContainer().size());
        List<Candidate<E>> synchronizedList = Collections.synchronizedList(getContainer());
        synchronizedList.stream().forEach((c) -> {
            jobs.add((Runnable) () -> {
                E p = c.getVariables();
                E p1, p2, p3;
                do {
                    int rand = rng.nextInt(synchronizedList.size());
                    p1 = synchronizedList.get(rand).getVariables();
                } while (p1 == c);
                do {
                    int rand = rng.nextInt(synchronizedList.size());
                    p2 = synchronizedList.get(rand).getVariables();
                } while (p2 == c && p2 == p1);
                do {
                    int rand = rng.nextInt(synchronizedList.size());
                    p3 = synchronizedList.get(rand).getVariables();
                } while (p3 == c && p3 == p1 && p3 == p2);
                Encoding<E> differentiate = crossover.crossover(p, p1, p2, p3);
                Candidate<E> sample = getCandidateFactory().toCandidate(differentiate);
                if (sample.getCost() < c.getCost()) {
                    synchronizedList.set(synchronizedList.indexOf(c), sample);
                    if (sample.getCost() < getBestCandidate().getCost()) {
                        setBestCandidate(sample);
                    }
                }
            });
        });
        submitJobs(jobs);
    }

    @Override
    public String getName() {
        return "Differential Evolution";
    }

}
