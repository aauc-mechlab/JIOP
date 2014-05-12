///*
// * Copyright (c) 2014, LarsIvar
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * * Redistributions of source code must retain the above copyright notice, this
// *   list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
//package no.hials.jiop.evolutionary.ga;
//
//import no.hials.jiop.evolutionary.ga.mutation.MutationOperator;
//import no.hials.jiop.evolutionary.ga.selection.SelectionOperator;
//import no.hials.jiop.evolutionary.ga.crossover.CrossoverOperator;
//import java.util.List;
//import no.hials.jiop.Evaluator;
//import no.hials.jiop.GeneralPopBasedAlgorithm;
//import no.hials.jiop.candidates.Candidate;
//
///**
// *
// * @author LarsIvar
// */
//public class GeneticAlgorithm<E> extends GeneralPopBasedAlgorithm<E> {
//
//    private CrossoverOperator<E> crossover;
//    private SelectionOperator<E> selection;
//    private MutationOperator<E> mutation;
//
//    private double elitism, selectionRate, mutationRate;
//
//    public GeneticAlgorithm(Class<?> templateClass, int size, double elitism, double selectionRate, double mutationRate, Evaluator<E> evaluator, String name) {
//        super(templateClass, size, evaluator, name);
//        this.elitism = elitism;
//        this.selectionRate = selectionRate;
//        this.mutationRate = mutationRate;
//    }
//
//    @Override
//    protected void candidateUpdate() {
//        List<Candidate<E>> eliteCandidates = copySubrange(0, (int) (getSize() * elitism));
//        List<Candidate<E>> selectedCandidates = selection.performSelection(getPopulation(), (int) (getSize() * selectionRate));
//        List<Candidate<E>> offspringCandidates = crossover.performCrossover(selectedCandidates, getSize() - (selectedCandidates.size() / 2));
//        mutation.performMutation(offspringCandidates, (int) (selectedCandidates.size() * mutationRate));
//
//        getPopulation().clear();
//        getPopulation().addAll(eliteCandidates);
//        getPopulation().addAll(offspringCandidates);
//        evaluateAll();
//        sortCandidates();
//
//    }
//
//    public double getElitism() {
//        return elitism;
//    }
//
//    public void setElitism(double elitism) {
//        this.elitism = elitism;
//    }
//
//    public double getSelectionRate() {
//        return selectionRate;
//    }
//
//    public void setSelectionRate(double selectionRate) {
//        this.selectionRate = selectionRate;
//    }
//
//    public double getMutationRate() {
//        return mutationRate;
//    }
//
//    public void setMutationRate(double mutationRate) {
//        this.mutationRate = mutationRate;
//    }
//
//    public CrossoverOperator<E> getCrossover() {
//        return crossover;
//    }
//
//    public void setCrossover(CrossoverOperator<E> crossover) {
//        this.crossover = crossover;
//    }
//
//    public SelectionOperator<E> getSelection() {
//        return selection;
//    }
//
//    public void setSelection(SelectionOperator<E> selection) {
//        this.selection = selection;
//    }
//
//    public MutationOperator<E> getMutation() {
//        return mutation;
//    }
//
//    public void setMutation(MutationOperator<E> mutation) {
//        this.mutation = mutation;
//    }
//
//}
