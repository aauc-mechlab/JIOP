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
package no.hials.jiop.candidates;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayParticleCandidate extends DoubleArrayCandidate implements ParticleCandidate<double[]> {

    private double[] localBestPosition;
    private double localBestCost;
    private final double[] velocity;

    public DoubleArrayParticleCandidate(int length) {
        super(length);
        this.localBestPosition = elements.clone();
        this.localBestCost = cost;
        this.velocity = new double[length];
    }

    public DoubleArrayParticleCandidate(double[] elements) {
        super(elements);
        this.localBestPosition = elements.clone();
        this.localBestCost = cost;
        this.velocity = new double[elements.length];
    }

    public DoubleArrayParticleCandidate(double[] elements, double cost) {
        super(elements, cost);
        this.localBestPosition = elements.clone();
        this.localBestCost = cost;
        this.velocity = new double[elements.length];
    }

    @Override
    public Number getVelocityAt(int index) {
        return velocity[index];
    }

    @Override
    public void setVelocityAt(int index, Number value) {
        velocity[index] = value.doubleValue();
    }

    @Override
    public DoubleArrayParticleCandidate copy() {
        return new DoubleArrayParticleCandidate(elements.clone(), cost);
    }

    @Override
    public NumericCandidate<double[]> getLocalBest() {
        return new DoubleArrayParticleCandidate(localBestPosition, localBestCost);
    }

    @Override
    public void setLocalBest(NumericCandidate<double[]> localBest) {
       this.localBestPosition = localBest.getElements();
       this.localBestCost = localBest.getCost();
    }

}
