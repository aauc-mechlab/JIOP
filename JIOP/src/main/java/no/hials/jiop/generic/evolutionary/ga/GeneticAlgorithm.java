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
package no.hials.jiop.generic.evolutionary.ga;

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.generic.Evaluator;
import no.hials.jiop.generic.GeneralPopulationBasedAlgorithm;
import no.hials.jiop.generic.candidates.Candidate;
import no.hials.jiop.generic.evolutionary.ga.crossover.AbstractCrossoverOperator;
import no.hials.jiop.generic.evolutionary.ga.mutation.AbstractMutationOperator;
import no.hials.jiop.generic.evolutionary.ga.selection.AbstractSelectionOperator;
import no.hials.jiop.generic.factories.CandidateFactory;
import no.hials.jiop.generic.tuning.Optimizable;
import no.hials.jiop.util.NormalizationUtility;

/**
 * A Continuous Genetic Algorithm implementation
 * @author Lars Ivar Hatledal
 */
public class GeneticAlgorithm<E> extends GeneralPopulationBasedAlgorithm<E> implements Optimizable{

    private AbstractCrossoverOperator<E> crossover;
    private AbstractSelectionOperator<E> selection;
    private AbstractMutationOperator<E> mutation;

    private double elitism;
    
    public GeneticAlgorithm(int size, double elitism, AbstractSelectionOperator selection, AbstractCrossoverOperator<E> crossover, AbstractMutationOperator mutation, CandidateFactory<E> candidateFactory, Evaluator<E> evaluator) {
        this(size, elitism, selection, crossover, mutation, candidateFactory, evaluator, "Genetic Algorithm");
    }

    public GeneticAlgorithm(int size, double elitism, AbstractSelectionOperator selection, AbstractCrossoverOperator<E> crossover, AbstractMutationOperator mutation, CandidateFactory<E> candidateFactory, Evaluator<E> evaluator, String name) {
        super(size, candidateFactory, evaluator, name);
        this.selection = selection;
        this.crossover = crossover;
        this.mutation = mutation;
        this.elitism = elitism;
    }

    @Override
    protected void singleIteration() {
        List<Candidate<E>> eliteCandidates = copySubrange(0, (int) (size() * elitism));
        List<Candidate<E>> selectedCandidates = selection.selectCandidates(getPopulation());
//        System.out.println("1");
        List<E> offspring = crossover.mateCandidates(selectedCandidates, (size() - eliteCandidates.size() ) / 2);
//        System.out.println("2");
        List<Candidate<E>> offspringCandidates = new ArrayList<>(offspring.size());
        for (E e : offspring) {
            offspringCandidates.add(getCandidateFactory().generateFromElements(e));
        }

        mutation.mutateCandidates(offspringCandidates);
//        mutation.mutateCandidates(selectedCandidates);

        getPopulation().clear();
        getPopulation().addAll(eliteCandidates);
//        getPopulation().addAll(selectedCandidates);
        getPopulation().addAll(offspringCandidates);
        evaluateAll();
        sortCandidates();
        if (size() > size) {
            getPopulation().subList(size, size()).clear();
        } else if (size() < size) {
            getPopulation().add(randomCandidate());
        }
        setBestCandidateIfBetter(population.get(0));

    }

    public double getElitism() {
        return elitism;
    }

    public void setElitism(double elitism) {
        this.elitism = elitism;
    }

    public AbstractCrossoverOperator<E> getCrossover() {
        return crossover;
    }

    public void setCrossover(AbstractCrossoverOperator<E> crossover) {
        this.crossover = crossover;
    }

    public AbstractSelectionOperator<E> getSelection() {
        return selection;
    }

    public void setSelection(AbstractSelectionOperator<E> selection) {
        this.selection = selection;
    }

    public AbstractMutationOperator<E> getMutation() {
        return mutation;
    }

    public void setMutation(AbstractMutationOperator<E> mutation) {
        this.mutation = mutation;
    }

    @Override
    public int getNumberOfFreeParameters() {
        return 6;
    }

    @Override
    public void setFreeParameters(double[] array) {
        this.size = (int) new NormalizationUtility(1, 0, 160, 40).normalize(array[0]);
        this.elitism = new NormalizationUtility(1, 0, 0.4, 0).normalize(array[1]);
        this.selection.setSelectionRate(new NormalizationUtility(1, 0, 0.8, 0.01).normalize(array[2]));
        this.crossover.setCrossoverProbability(new NormalizationUtility(1, 0, 0.8, 0.01).normalize(array[3]));
        this.mutation.setMutProb(new NormalizationUtility(1, 0, 0.5, 0.01).normalize(array[4]));
        this.mutation.setMutChange(new NormalizationUtility(1, 0, 0.5, 0.001).normalize(array[5]));
    }

    @Override
    public double[] getFreeParameters() {
       return new double[]{size, elitism, selection.getSelectionRate(), crossover.getCrossoverProbability(), mutation.getMutProb(), mutation.getMutChange()}; 
    }
}
