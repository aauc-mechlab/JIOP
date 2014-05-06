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
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;

/**
 *
 * @author Lars Ivar Hatledal
 * @param <E>
 */
public class ArtificialBeeColony<E> extends Algorithm<E> {

    private int size;
    private int numScouts;

    private Colony colony;

    public ArtificialBeeColony(Class<?> clazz, int size, int numScouts, Evaluator<E> evaluator, String name) {
        super(clazz, evaluator, name);
        this.size = size;
        this.numScouts = numScouts;
    }

    public ArtificialBeeColony(Class<?> clazz, int size, int numScouts, String name) {
        this(clazz, size, numScouts, null, name);
    }

    public ArtificialBeeColony(Class<?> clazz, int size, int numScouts) {
        this(clazz, size, numScouts, "Artificial Bee Colony");
    }

    public ArtificialBeeColony(Class<?> clazz, int size, double numScouts) {
        this(clazz, size, (int) (size * numScouts));
    }

    @Override
    public Candidate<E> subInit() {
        this.colony = new Colony(size);
        Collections.sort(colony);
        return colony.get(0);
    }

    @Override
    public Candidate<E> subInit(List<E> seeds) {
        this.colony = new Colony(size - seeds.size());
        for (E seed : seeds) {
            colony.add((NumericCandidate<E>) newCandidate(seed));
        }

        Collections.sort(colony);
        return colony.get(0);
    }

    @Override
    protected Candidate<E> singleIteration() {

        List<NumericCandidate<E>> newPop = new ArrayList<>(size);
        List<NumericCandidate<E>> bestCandidates = colony.subList(0, numScouts - 1);
        bestCandidates.add((NumericCandidate<E>) getBestCandidate());
        int neighborHoodSize = size / (numScouts);
        bestCandidates.stream().forEach((c) -> {
            List<NumericCandidate<E>> neighborhood = new ArrayList<>(neighborHoodSize);
            neighborhood.add(c);
            int remaining = neighborHoodSize - 1;
            for (int i = 0; i < remaining; i++) {
                 double prox = rng.nextDouble() * Math.abs(0.25 - 0.000001) + 0.000001;
                neighborhood.add((NumericCandidate<E>) evaluateAndUpdate(c.neighbor(prox)));
            }

            Collections.sort(neighborhood);
            NumericCandidate<E> best = (NumericCandidate<E>) neighborhood.get(0);
            newPop.add(best);
            for (int i = 0; i < remaining; i++) {
                newPop.add((NumericCandidate<E>) newCandidate());
            }
        });
        colony.clear();
        colony.addAll(newPop);
        Collections.sort(colony);
        return colony.get(0);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumOutlookers() {
        return numScouts;
    }

    public void setNumScouts(int numScouts) {
        this.numScouts = numScouts;
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
    private class Colony extends ArrayList<NumericCandidate<E>> {

        public Colony(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add((NumericCandidate<E>) newCandidate());
            }
        }
    }
}
