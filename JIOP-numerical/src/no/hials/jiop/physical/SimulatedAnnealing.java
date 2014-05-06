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
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public class SimulatedAnnealing<E> extends Algorithm<E> {

    private double startingTemperature;
    private double temperature, alpha;
    private Candidate<E> current;

    public SimulatedAnnealing(Class<?> clazz, double startingTemperature, double alpha) {
        super(clazz, "Simulated Annealing");
        this.startingTemperature = startingTemperature;
        this.alpha = alpha;
    }

    @Override
    public Candidate<E> subInit() {
        this.temperature = startingTemperature;
        this.current = (NumericCandidate<E>) newCandidate();
        return current;
    }

    @Override
    protected Candidate<E> subInit(List<E> seeds) {
        this.temperature = startingTemperature;
        this.current = newCandidate(seeds.get(0));
        
        return current;
    }

    @Override
    protected void singleIteration() {
        double prox = rng.nextDouble() * Math.abs(0.25 - 0.00001) + 0.00001;
        Candidate<E> newSample = evaluateAndUpdate(current.neighbor(prox));
        if (doAccept(current, newSample)) {
            current = newSample;
        }
        setBestCandidateIfBetter(newSample);
        temperature *= alpha;

    }

    /**
     * Should we accept the new solution based on the Metropolis criteria?
     *
     * @param current the current solution
     * @param newSample the new solution
     * @return whether or not the new solution should be accepted
     */
    private boolean doAccept(Candidate<E> current, Candidate<E> newSample) {
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
    public double getStartingTemperature() {
        return startingTemperature;
    }

    public void setStartingTemperature(double startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
