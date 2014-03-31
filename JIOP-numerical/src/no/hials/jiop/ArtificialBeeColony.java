/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author LarsIvar
 */
public class ArtificialBeeColony extends Algorithm {

    private int size;
    private int numOutlookers;
    
    

    private Colony colony;

    public ArtificialBeeColony(int size, int numOutlookers, int dimension, Evaluator evaluator) {
        super("Artificial Bee Colony", dimension, evaluator);
        this.size = size;
        this.numOutlookers = numOutlookers;
        this.init();
        
        
        
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

    public ArtificialBeeColony(int size, double outlookers, int dimension, Evaluator evaluator) {
        this(size, (int) (size * outlookers), dimension, evaluator);
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
