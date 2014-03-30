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
package no.hials.jiop.base.algorithms.evolutionary.ga;

import no.hials.jiop.base.Evaluator;
import no.hials.jiop.base.algorithms.evolutionary.ga.crossover.CrossoverOperator;
import no.hials.jiop.base.algorithms.evolutionary.ga.crossover.DoubleArrayCrossover;
import no.hials.jiop.base.algorithms.evolutionary.ga.mutation.MutationOperator;
import no.hials.jiop.base.algorithms.evolutionary.ga.selection.SelectionOperator;
import no.hials.jiop.base.candidates.encoding.factories.DoubleArrayEncodingFactory;

/**
 *
 * @author Lars Ivar
 */
public class DoubleArrayGA extends GA<double[]> {

    public DoubleArrayGA(int size, int numElites, int numSelection, int numMutations, int dim, SelectionOperator<double[]> selection, MutationOperator<double[]> mutation, Evaluator<double[]> evaluator) {
        super(size, numElites, numSelection, numMutations, selection, new DoubleArrayCrossover(), mutation, new DoubleArrayEncodingFactory(dim), evaluator);
    }

    public DoubleArrayGA(int size, float elitism, float keep, float mutrate, int dim, SelectionOperator<double[]> selection, MutationOperator<double[]> mutation, Evaluator<double[]> evaluator) {
        super(size, elitism, keep, mutrate, selection, new DoubleArrayCrossover(), mutation, new DoubleArrayEncodingFactory(dim), evaluator);
    }

    public DoubleArrayGA(int size, int numElites, int numSelection, int numMutations, int dim, SelectionOperator<double[]> selection, CrossoverOperator<double[]> crossover, MutationOperator<double[]> mutation, Evaluator<double[]> evaluator) {
        super(size, numElites, numSelection, numMutations, selection, crossover, mutation, new DoubleArrayEncodingFactory(dim), evaluator);
    }

    public DoubleArrayGA(int size, float elitism, float keep, float mutrate, int dim, SelectionOperator<double[]> selection, CrossoverOperator<double[]> crossover, MutationOperator<double[]> mutation, Evaluator<double[]> evaluator) {
        super(size, elitism, keep, mutrate, selection, crossover, mutation, new DoubleArrayEncodingFactory(dim), evaluator);
    }

}
