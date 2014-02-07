/*
 * Copyright (c) 2014, LarsIvar
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
import no.hials.jiop.base.AbstractEvaluator;
import no.hials.jiop.base.MLMethod;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.factories.CandidateFactory;

/**
 *
 * @author LarsIvar
 */
public class SA<E> extends MLMethod<E> {

    private double temperature;
    private final double startingTemperature;
    private final AnnealingSchedule schedule;
    private Candidate<E> current;

    public SA(double startingTemperature, AnnealingSchedule schedule, CandidateFactory<E> factory, AbstractEvaluator<E> evaluator) {
        super(factory, evaluator);
        this.startingTemperature = startingTemperature;
        this.schedule = schedule;
    }

    @Override
    public void internalIteration() {
        Candidate<E> newSample = getFactory().neighborCandidate(current, 0.001);
        if (doAccept(current, newSample)) {
            current = newSample;
        }
        if (newSample.getCost() < getBestCandidate().getCost()) {
            setBestCandidate(newSample);
        }
        temperature = schedule.cool(temperature);
    }

    private boolean doAccept(Candidate<E> current, Candidate<E> newSample) {
        return newSample.getCost() < current.getCost() | Math.exp(-(newSample.getCost() - current.getCost()) / temperature) > Math.random();
    }

    @Override
    public void reset(List<E> initials, boolean clearHistory) {
        super.reset(initials, clearHistory);
        this.current = getFactory().toCandidate(initials.get(0));
        this.temperature = startingTemperature;
    }

    @Override
    public void reset(boolean clearHistory) {
        super.reset(clearHistory);
        this.current = getFactory().randomCandidate();
        this.temperature = startingTemperature;
    }

    @Override
    public String getName() {
        return "Simulated Annealing";
    }

}
