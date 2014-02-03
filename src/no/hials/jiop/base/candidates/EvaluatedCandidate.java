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

import no.hials.jiop.base.candidates.encoding.BasicEncoding;

/**
 *
 * @author LarsIvar
 */
public class EvaluatedCandidate<E> extends Candidate<E>{

    private final int iterations;
    private final long time;

    public EvaluatedCandidate(BasicEncoding<E> encoding, int iterations, long time,  double cost) {
        super(encoding.copy(), cost);
        this.iterations = iterations;
        this.time = time;
    }

    public EvaluatedCandidate(Candidate<E> candidate, int iterations, long time) {
        super(candidate.getEncoding().copy(), candidate.getCost());
        this.iterations = iterations;
        this.time = time;
    }

    public int getIterations() {
        return iterations;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "EvaluatedCandidate{" + "iterations=" + iterations + ", time=" + time + ", candidate=" + super.toString() +  '}';
    }

    
    
}
