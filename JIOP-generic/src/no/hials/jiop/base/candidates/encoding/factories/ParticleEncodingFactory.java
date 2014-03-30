/*
 * Copyright (c) 2014, Lars Ivar
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

import java.util.ArrayList;
import java.util.List;
import no.hials.jiop.base.candidates.encoding.Encoding;
import no.hials.jiop.base.candidates.encoding.ParticleEncoding;

/**
 *
 * @author Lars Ivar
 */
public abstract class ParticleEncodingFactory<E> extends EncodingFactory<E> {

    public ParticleEncodingFactory(int encodingLength) {
        super(encodingLength);
    }

    @Override
    public List<Encoding<E>> getRandomEncodingList(int size) {
        List<Encoding<E>> random = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            random.add(getRandomEncoding());
        }
        return random;
    }

    @Override
    public abstract ParticleEncoding<E> wrapVariables(E variables);

    @Override
    public abstract ParticleEncoding<E> getNeighborEncoding(E variables, double change);

    @Override
    protected abstract ParticleEncoding<E> getRandomEncoding(int length);

    @Override
    public ParticleEncoding<E> getRandomEncoding() {
        return getRandomEncoding(encodingLength);
    }
}
