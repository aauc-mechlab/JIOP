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

package no.hials.jiop.factories;

import java.util.Random;
import no.hials.jiop.candidates2.Encoding;
import no.hials.jiop.candidates2.DoubleArrayEncoding;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayCandidateFactory extends AbstractEncodingFactory<double[]>{

    private final Random rng  = new Random();
    
    @Override
    public Encoding<double[]> generateRandom(int dim) {
        double[] random = new double[dim];
        for (int i = 0; i < random.length; i++) {
            random[i] = rng.nextDouble();
        }
        return new DoubleArrayEncoding(random);
    }

    @Override
    public Encoding<double[]> generate(double[] elements) {
         return new DoubleArrayEncoding(elements);
    }
    
}
