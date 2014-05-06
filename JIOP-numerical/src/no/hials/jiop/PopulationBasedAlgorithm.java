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
package no.hials.jiop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;
import no.hials.jiop.evolutionary.DifferentialEvolution;

/**
 *
 * @author LarsIvar
 */
public abstract class PopulationBasedAlgorithm<E> extends AbstractAlgorithm<E> {

    protected int size;
    protected Population population;

    public PopulationBasedAlgorithm(Class<?> clazz, int size, String name) {
        super(clazz, name);
        this.size = size;
    }

    public PopulationBasedAlgorithm( Class<?> templateClass,int size, Evaluator<E> evaluator, String name) {
        super(templateClass, evaluator, name);
        this.size = size;
    }
    
    

    @Override
    public Candidate<E> subInit() {
        this.population = new Population(size);
        Collections.sort(population);
        return population.get(0);
    }

    @Override
    public Candidate<E> subInit(List<E> seeds) {
        this.population = new Population(size - seeds.size());
        for (E seed : seeds) {
            population.add((NumericCandidate<E>) newCandidate(seed));
        }

        Collections.sort(population);
        return population.get(0);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    protected class Population extends ArrayList<Candidate<E>> {

        public Population(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add(newCandidate());
            }
        }

    }

}
