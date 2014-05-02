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
package no.hials.jiop.util;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayParticleStructure extends DoubleArrayCandidateStructure implements NumericParticleStructure<double[]> {

    private NumericCandidateStructure<double[]> localBest;
    private final DoubleArrayStructure velocity;

    public DoubleArrayParticleStructure(int length) {
        super(length);
        this.velocity = new DoubleArrayStructure(length);
        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
    }

    public DoubleArrayParticleStructure(double[] elements) {
        super(elements);
        this.velocity = new DoubleArrayStructure(elements.length);
        this.velocity.randomize();
        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
    }

    public DoubleArrayParticleStructure(double[] elements, double cost) {
        super(elements, cost);
        this.velocity = new DoubleArrayStructure(elements.length);
        this.velocity.randomize();
        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
    }

    @Override
    public NumericCandidateStructure<double[]> getLocalBest() {
        return localBest;
    }

    @Override
    public void setLocalBest(NumericCandidateStructure localBest) {
        this.localBest = (DoubleArrayCandidateStructure) localBest;
    }

    @Override
    public DoubleArrayStructure getVelocity() {
        return velocity;
    }

}
