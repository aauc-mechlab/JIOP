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
package no.hials.jiop.physical;

import java.util.List;
import no.hials.jiop.Algorithm;
import no.hials.jiop.util.CandidateStructure;
import no.hials.jiop.util.NumericCandidateStructure;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class SimulatedAnnealing<E> extends Algorithm<E> {

    private final double startingTemperature;
    private double temperature, alpha;
    private NumericCandidateStructure current, bestCandidate;

    public SimulatedAnnealing(Class<?> clazz, double startingTemperature, double alpha) {
        super(clazz, "Simulated Annealing");
        this.startingTemperature = startingTemperature;
        this.alpha = alpha;
    }

    @Override
    public void subInit() {
        this.temperature = startingTemperature;
        this.current = (NumericCandidateStructure) random();
        this.bestCandidate = (NumericCandidateStructure) copy(current);
    }

    @Override
    protected CandidateStructure<E> singleIteration() {
        CandidateStructure<E> newSample = current.neighbor(bestCandidate.getCost() / 5);
        newSample.setCost(getEvaluator().evaluate(newSample.getElements()));
        if (doAccept(current, newSample)) {
            current = (NumericCandidateStructure) copy(newSample);
        }
        if (newSample.getCost() < bestCandidate.getCost()) {
            bestCandidate = (NumericCandidateStructure) copy(newSample);
        }
        temperature *= alpha;
        return (NumericCandidateStructure) copy(bestCandidate);
    }

    @Override
    protected void subInit(List<E> seeds) {
        this.temperature = startingTemperature;
        this.current = (NumericCandidateStructure) newCandidate(seeds.get(0));
        this.bestCandidate = (NumericCandidateStructure) copy(current);
    }

    

    /**
     * Should we accept the new solution based on the Metropolis criteria?
     *
     * @param current the current solution
     * @param newSample the new solution
     * @return whether or not the new solution should be accepted
     */
    private boolean doAccept(CandidateStructure<E> current, CandidateStructure<E> newSample) {
        return newSample.getCost() < current.getCost() | Math.exp(-(newSample.getCost() - current.getCost()) / temperature) > Math.random();
    }

//    @Override
//    public int getNumberOfFreeParameters() {
//       return 2;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        this.startingTemperature = new NormUtil(1, 0, 1000, 10).normalize(array.get(0));
//        this.alpha = new NormUtil(1, 0, 0.995, 0.8).normalize(array.get(1));
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(startingTemperature, alpha);
//    }
//
//    @Override
//    protected CandidateStructure singleIteration() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
