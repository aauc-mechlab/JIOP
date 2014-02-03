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

package no.hials.jiop.factories;

import no.hials.jiop.utils.ArrayUtil;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;
import no.hials.jiop.base.candidates.encoding.DoubleArrayEncoding;
import no.hials.jiop.base.candidates.encoding.DoubleArrayParticleEncoding;
import no.hials.jiop.base.candidates.encoding.ParticleEncoding;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayParticleFactory extends DoubleArrayCandidateFactory {

    public DoubleArrayParticleFactory(Evaluator<double[]> evaluator) {
        super(evaluator);
    }

    @Override
    public ParticleEncoding<double[]> random(int length) {
        double[] rand = ArrayUtil.randomD(length);
        return new DoubleArrayParticleEncoding(rand, new Candidate<>(new DoubleArrayEncoding(rand), evaluate(rand)));
    }

    @Override
    public BasicEncoding<double[]> wrap(double[] original) {
         return new DoubleArrayParticleEncoding(original, new Candidate<>(new DoubleArrayEncoding(original), evaluate(original)));
    }

    
    
}
