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
package no.hials.jiop.swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.jiop.Algorithm;
import no.hials.jiop.Evaluator;
import no.hials.jiop.util.NumericCandidateStructure;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public class ArtificialBeeColony<E> extends Algorithm<E> {

    private int size;
    private int numOutlookers;

    private Colony colony;

    public ArtificialBeeColony(Class<?> clazz, int size, int numOutlookers) {
        super(clazz, "Artificial Bee Colony");
        this.size = size;
        this.numOutlookers = numOutlookers;
    }

    public ArtificialBeeColony(Class<?> clazz, int size, double outlookers) {
        this(clazz, size, (int) (size * outlookers));
    }

    @Override
    public void subInit() {
        this.colony = new Colony(size);
        Collections.sort(colony);
    }

    @Override
    public void subInit(List<E> seeds) {
        this.colony = new Colony(size - seeds.size());
        for (E seed : seeds) {
            colony.add((NumericCandidateStructure<E>) newCandidate(seed));
        }

        Collections.sort(colony);
    }

    @Override
    protected NumericCandidateStructure<E> singleIteration() {
        final List<NumericCandidateStructure<E>> bestCandidates = colony.subList(0, numOutlookers);
        final List<NumericCandidateStructure<E>> newPop = new ArrayList<>(size);
        for (final NumericCandidateStructure<E> c : bestCandidates) {
            int neighborHoodSize = size / (numOutlookers);
            final List<NumericCandidateStructure<E>> neighborhood = new ArrayList<>(neighborHoodSize);
            neighborhood.add(c);
            int remaining = neighborHoodSize - 1;
            for (int i = 0; i < remaining; i++) {
                NumericCandidateStructure<E> neighbor = (NumericCandidateStructure<E>) evaluateAndUpdate(c.neighbor(c.getCost() / 5));
                neighborhood.add(neighbor);
            }
            Collections.sort(neighborhood);
            NumericCandidateStructure<E> best = neighborhood.get(0);
            newPop.add(best);
            Colony randoms = new Colony(remaining);
            newPop.addAll(randoms);
        }
        this.colony.clear();
        this.colony.addAll(newPop);
        Collections.sort(colony);

        return (NumericCandidateStructure<E>) copy(colony.get(0));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumOutlookers() {
        return numOutlookers;
    }

    public void setNumOutlookers(int numOutlookers) {
        this.numOutlookers = numOutlookers;
    }

//    @Override
//    public int getNumberOfFreeParameters() {
//        return 2;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        this.size = (int) new NormUtil(1, 0, 120, 6).normalize(array.get(0));
//        this.numOutlookers = (int) new NormUtil(1, 0, size, 1).normalize(array.get(0));
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(size, numOutlookers);
//    }
    private class Colony extends ArrayList<NumericCandidateStructure<E>> {

        public Colony(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add((NumericCandidateStructure) random());
            }
        }
    }
}
