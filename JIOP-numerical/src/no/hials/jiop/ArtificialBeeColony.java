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
package no.hials.jiop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.hials.utilities.DoubleArray;
import no.hials.utilities.NormUtil;

/**
 *
 * @author LarsIvar
 */
public class ArtificialBeeColony extends Algorithm {

    private int size;
    private int numOutlookers;
    
    private Colony colony;

    public ArtificialBeeColony(int size, int numOutlookers) {
        super("Artificial Bee Colony");
        this.size = size;
        this.numOutlookers = numOutlookers;
    }
    
     public ArtificialBeeColony(int size, double outlookers) {
        this(size, (int) (size * outlookers));
    }

    @Override
    public void subInit() {
        this.colony = new Colony(size);
        Collections.sort(colony);
    }

    @Override
    public void subInit(DoubleArray... seeds) {
        this.colony = new Colony(size - seeds.length);
        for (DoubleArray seed : seeds) {
            colony.add(new Candidate(seed, getEvaluator().evaluate(seed)));
        }
        Collections.sort(colony);
    }

   

    @Override
    protected Candidate singleIteration() {
        final List<Candidate> bestCandidates = colony.subList(0, numOutlookers);
        final List<Candidate> newPop = new ArrayList<>(size);
        for (final Candidate c : bestCandidates) {
            int neighborHoodSize = size / (numOutlookers);
            final List<Candidate> neighborhood = new ArrayList<>(neighborHoodSize);
            neighborhood.add(c);
            int remaining = neighborHoodSize - 1;
            for (int i = 0; i < remaining; i++) {
                neighborhood.add(Candidate.neighborCandidate(c, c.getCost() / 5, getEvaluator()));
            }
            Collections.sort(neighborhood);
            Candidate best = neighborhood.get(0);
            newPop.add(best);
            Colony randoms = new Colony(remaining);
            newPop.addAll(randoms);
        }
        colony.clear();
        colony.addAll(newPop);
        Collections.sort(colony);
        return colony.get(0).copy();
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

    @Override
    public int getNumberOfFreeParameters() {
        return 2;
    }

    @Override
    public void setFreeParameters(DoubleArray array) {
        this.size = (int) new NormUtil(1, 0, 120, 6).normalize(array.get(0));
        this.numOutlookers = (int) new NormUtil(1, 0, size, 1).normalize(array.get(0));
    }

    @Override
    public DoubleArray getFreeParameters() {
        return new DoubleArray(size, numOutlookers);
    }

    private class Colony extends ArrayList<Candidate> {

        public Colony(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add(Candidate.randomCandidate(getDimension(), getEvaluator()));
            }
        }
    }
}
