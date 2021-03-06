/*
 * Copyright (c) 2014, laht
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
package no.hials.jiop.generic.candidates.bacterium;

import no.hials.jiop.generic.candidates.DoubleArrayCandidate;

/**
 *
 * @author laht
 */
public class DoubleArrayBacteria extends DoubleArrayCandidate implements BacteriaCandidate<double[]> {

    private double prevCost = Double.MAX_VALUE;
    private double health;

    public DoubleArrayBacteria(double[] elements) {
        super(elements);
    }

    public DoubleArrayBacteria(double[] elements, double cost) {
        super(elements, cost);
    }
    
    private DoubleArrayBacteria(double[] elements, double cost, double prevCost, double health) {
        super(elements, cost);
        this.prevCost = prevCost;
        this.health = health;
    }

    @Override
    public double getPrevCost() {
        return prevCost;
    }

    @Override
    public void setPrevCost(double prevCost) {
        this.prevCost = prevCost;
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public DoubleArrayBacteria copy() {
        return new DoubleArrayBacteria(elements.clone(), cost, prevCost, health);
    } 
}
