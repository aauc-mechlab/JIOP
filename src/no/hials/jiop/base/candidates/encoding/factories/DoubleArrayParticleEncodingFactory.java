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
package no.hials.jiop.base.candidates.encoding.factories;

import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.encoding.DoubleArrayEncoding;
import no.hials.jiop.base.candidates.encoding.DoubleArrayParticleEncoding;
import no.hials.jiop.base.candidates.encoding.Encoding;
import no.hials.jiop.utils.ArrayUtil;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class DoubleArrayParticleEncodingFactory extends EncodingFactory<double[]> {

    public DoubleArrayParticleEncodingFactory(int encodingLength) {
        super(encodingLength);
    }

    @Override
    protected Encoding<double[]> getRandomEncoding(int length) {
        double[] rand = ArrayUtil.randomD(length);
        return new DoubleArrayParticleEncoding(rand, new Candidate<>(new DoubleArrayEncoding(rand), Double.MAX_VALUE));
    }

    @Override
    public Encoding<double[]> getNeighborEncoding(double[] variables, double change) {
        double[] neighbor = ArrayUtil.neighbor(variables, change);
        return new DoubleArrayParticleEncoding(neighbor, new Candidate<>(new DoubleArrayEncoding(neighbor), Double.MAX_VALUE));
    }

    @Override
    public Encoding<double[]> getWrapVariables(double[] variables) {
       return new DoubleArrayParticleEncoding(variables, new Candidate<>(new DoubleArrayEncoding(variables), Double.MAX_VALUE));
    }

  

}
