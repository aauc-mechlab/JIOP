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

import no.hials.jiop.base.candidates.CandidatePair;
import java.util.Random;
import no.hials.jiop.ArrayUtil;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.DoubleArrayEncoding;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayCrossover extends AbstractCrossoverOperator<double[]> {

    @Override
    public CandidatePair<double[]> mate(Candidate<double[]> ma, Candidate<double[]> pa, Random rng) {
//
        double[] offspring1 = ArrayUtil.newInstance(ma.getElements());
        double[] offspring2 = ArrayUtil.newInstance(pa.getElements());

        int alpha = rng.nextInt(ma.getElements().length);
        double beta = rng.nextDouble();

        offspring1[alpha] = ma.getElements()[alpha] - beta * ma.getElements()[alpha] + beta * pa.getElements()[alpha];
        offspring2[alpha] = pa.getElements()[alpha] + beta * ma.getElements()[alpha] - beta * pa.getElements()[alpha];

        if (alpha == offspring1.length) {
            for (int i = offspring1.length-1; i >= 0; i--) {
                offspring1[i] = pa.getElements()[i];
                offspring2[i] = ma.getElements()[i];
            }
        } else {
            for (int i = alpha + 1; i < offspring1.length; i++) {
                offspring1[i] = pa.getElements()[i];
                offspring2[i] = ma.getElements()[i];
            }
        }

        ArrayUtil.clamp(0, 1, offspring1);
        ArrayUtil.clamp(0, 1, offspring2);

//        double[] offspring1 = ArrayUtil.clamp(0, 1, ArrayUtil.plus(ArrayUtil.scale(ArrayUtil.minus(ma.getElements(), pa.getElements()), rng.nextDouble()), ma.getElements()));
//        double[] offspring2 = ArrayUtil.clamp(0, 1, ArrayUtil.plus(ArrayUtil.scale(ArrayUtil.minus(pa.getElements(), ma.getElements()), rng.nextDouble()), pa.getElements()));
        return new CandidatePair<>(new Candidate(new DoubleArrayEncoding(offspring1), Double.MAX_VALUE), new Candidate(new DoubleArrayEncoding(offspring2), Double.MAX_VALUE));
    }

}
