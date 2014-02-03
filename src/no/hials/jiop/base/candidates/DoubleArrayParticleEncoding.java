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
package no.hials.jiop.base.candidates;

import java.util.Random;
import no.hials.jiop.ArrayUtil;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayParticleEncoding extends DoubleArrayEncoding implements ParticleEncoding<double[]> {

    private final double[] velocity;
    private Candidate<double[]> localBest;

    public DoubleArrayParticleEncoding(double[] elements, Candidate<double[]> localBest) {
        super(elements);
        this.velocity = ArrayUtil.randomD(size());
        this.localBest = localBest;
    }
    
    public DoubleArrayParticleEncoding(double[] elements, double[] velocity, Candidate<double[]> localBest) {
        super(elements);
        this.velocity = velocity;
        this.localBest = new Candidate<>(localBest);
    }

    @Override
    public DoubleArrayParticleEncoding copy() {
        return new DoubleArrayParticleEncoding(getElements().clone(), velocity.clone(), (localBest));
    }

    @Override
    public double[] getVelocity() {
        return velocity;
    }

    @Override
    public Candidate<double[]> getLocalBest() {
        return localBest;
    }

    @Override
    public void setLocalBest(Candidate<double[]> localBest) {
        this.localBest = new Candidate<>(localBest);
    }

    @Override
    public void update(double omega, double c1, double c2, double[] globalBest) {
        Random rng = new Random();
        for (int i = 0; i < globalBest.length; i++) {
            double vi = getVelocity()[i];
            double li = getLocalBest().getElements()[i];
            double pi = getElements()[i];
            double gi = globalBest[i];
            double vel = (omega * vi) + (rng.nextDouble() * c1 * (li - pi)) + (rng.nextDouble() * c2 * (gi - pi));

            if (vel < -0.5) {
                vel = -0.5;
            } else if (vel > 0.5) {
                vel = 0.5;
            }

            double newPos = getElements()[i] + vel;
            if (newPos < 0) {
                newPos = 0;
            } else if (newPos > 1) {
                newPos = 1;
            }
            getElements()[i] = newPos;
            getVelocity()[i] = vel;
        }
    }

    @Override
    public String toString() {
        return "DoubleArrayParticleEncoding{" + super.toString() + '}';
    }

    
    
}
