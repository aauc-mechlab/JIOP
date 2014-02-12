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
package no.hials.jiop.swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.PopulationBasedMLAlgorithm;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.containers.CandidateContainer;
import no.hials.jiop.base.candidates.encoding.factories.EncodingFactory;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class ABS<E> extends PopulationBasedMLAlgorithm<E> {

    private final int numOutlookers;

    public ABS(int numOutlookers, EncodingFactory<E> factory, CandidateContainer<E> container, Evaluator<E> evaluator) {
        super(factory, container, evaluator);
        this.numOutlookers = numOutlookers;
    }

    @Override
    public void internalIteration() {
//        final List<Runnable> jobs = new ArrayList<>(getContainer().size());
        final List<Candidate<E>> bestCandidates = getContainer().sort().getBestCandidates(numOutlookers - 1);
        bestCandidates.add(getBestCandidate());
        final List<Candidate<E>> newPop = Collections.synchronizedList(new ArrayList<Candidate<E>>(getContainer().size()));

        for (final Candidate<E> c : bestCandidates) {
//            jobs.add(new Runnable() {
//
//                @Override
//                public void run() {
                    int neighborHoodSize = getContainer().size() / (numOutlookers);
                    List<Candidate<E>> neighborhood = new ArrayList<>(neighborHoodSize);
                    neighborhood.add(c);
                    int remaining = neighborHoodSize - 1;
                    List<Candidate<E>> neighbors = getCandidateFactory().getNeighborCandidateList(c, getBestCandidate().getCost() / 10, remaining);
                    neighborhood.addAll(neighbors);
                    Collections.sort(neighborhood);
                    Candidate<E> best = neighborhood.get(0);
                    newPop.add(best);
                    List<Candidate<E>> randoms = getCandidateFactory().getRandomCandidateList(remaining);
                    newPop.addAll(randoms);
                }
//            });
//        }
//        submitJobs(jobs);
        getContainer().clearAndAddAll(newPop);
        setBestCandidate(getContainer().sort().get(0));
    }

    @Override
    public String getName() {
        return "Artificial Bee Colony";
    }

}
