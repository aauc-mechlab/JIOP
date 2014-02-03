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

package no.hials.jiop.evolutionary;

import no.hials.jiop.utils.ArrayUtil;
import static no.hials.jiop.Main.dim;
import no.hials.jiop.base.candidates.encoding.BasicEncoding;
import no.hials.jiop.base.candidates.encoding.DoubleArrayEncoding;
import no.hials.jiop.factories.AbstractCandidateFactory;

/**
 *
 * @author LarsIvar
 */
public class DoubleArrayDE extends DEEngine<double[]>{

    public DoubleArrayDE(int size, double F, double CR, int candiateLength, AbstractCandidateFactory<double[]> factory) {
        super(size, F, CR, candiateLength, factory);
    }

     @Override
            public BasicEncoding<double[]> differentiate(int R, double F, double CR, double[] c, double[] c1, double[] c2, double[] c3) {
                double[] array = new double[dim];
                for (int i = 0; i < array.length; i++) {
                    if ((Math.random() < CR) || (i == R)) {
                        array[i] = c1[i] + F * (c2[i] - c3[i]);
                    } else {
                        array[i] = c[i];
                    }
                }

                return new DoubleArrayEncoding(ArrayUtil.clamp(0, 1, array));
            }

    @Override
    public String getName() {
        return "Differential Evolution";
    }
    
}
