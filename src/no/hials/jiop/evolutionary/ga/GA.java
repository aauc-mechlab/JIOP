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
package no.hials.jiop.evolutionary.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.AbstractEvaluator;
import no.hials.jiop.base.candidates.containers.CandidateContainer;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.factories.CandidateFactory;

/**
 *
 * @author LarsIvar
 * @param <E>
 */
public class GA<E> extends MLMethod<E> {
    
    private final SelectionOperator<E> selection;
    private final CrossoverOperator<E> crossover;
    private final MutationOperator<E> mutation;
    
    private final int numElites;
    private final int numSelection;
    private final int numMutations;
    
    public GA(int numElites, int numSelection, int numMutations, SelectionOperator<E> selection, CrossoverOperator<E> crossover, MutationOperator<E> mutation, CandidateFactory<E> factory, CandidateContainer<E> container, AbstractEvaluator<E> evaluator) {
        super(factory, container, evaluator);
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.numElites = numElites;
        this.numSelection = numSelection;
        this.numMutations = numMutations;
    }
    
    public GA(float elitism, float keep, float mutrate, SelectionOperator<E> selection, CrossoverOperator<E> crossover, MutationOperator<E> mutation, CandidateFactory<E> factory, CandidateContainer<E> container, AbstractEvaluator<E> evaluator) {
        super(factory, container, evaluator);
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.numElites = Math.round(getContainer().size() * elitism);
        this.numSelection = Math.round(getContainer().size() * keep);
        this.numMutations = Math.round((getContainer().size() - numElites) * mutrate);
    }
    
    @Override
    public void internalIteration() {
        List<Candidate<E>> elites = getContainer().getBestCandidates(numElites);
        final List<Candidate<E>> selected = selection.selectCandidates(getContainer().getCandidates(), numSelection);
        final List<Candidate<E>> offspring = crossover.createoffspring(selected, Math.round((getContainer().size() - numElites - numSelection) / 2));
        final List<Candidate<E>> newpop = new ArrayList<>(getContainer().size());
        newpop.addAll(selected);
        newpop.addAll(offspring);
        mutation.mutateCandidates(newpop, numMutations);
        newpop.addAll(elites);
        if (getContainer().size() > newpop.size()) {
            newpop.addAll(randomCandidates(getContainer().size()-newpop.size()));
        }
        getContainer().clearAndAddAll(newpop);
        evaluateAll(getContainer());
        setBestCandidate(getContainer().sort().get(0));
    }

    @Override
    public String getName() {
        return "Genetic Algorithm";
    }
}
