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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.Algorithm;
import no.hials.jiop.util.NumericCandidateStructure;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class DifferentialEvolution<E> extends Algorithm<E> {

    private final Object mutex = new Object();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);

    private int NP;
    private double F, CR;
    private Candidates candidates;

    private boolean multiCore;
    private NumericCandidateStructure<E> bestCandidate;

    public DifferentialEvolution(Class<?> clazz, int NP, double F, double CR) {
        this(clazz, NP, F, CR, false);
    }

    public DifferentialEvolution(Class<?> clazz, int NP, double F, double CR, boolean multiCore) {
        super(clazz, "Differential Evolution " + multiCore);
        this.F = F;
        this.CR = CR;
        this.NP = NP;
        this.multiCore = multiCore;
    }

    @Override
    public void subInit() {
        this.candidates = new Candidates(NP);
        Collections.sort(candidates);
        this.bestCandidate = (NumericCandidateStructure<E>) copy(candidates.get(0));
    }

    @Override
    public void subInit(List<E> seeds) {
        this.candidates = new Candidates(NP - seeds.size());
        for (E seed : seeds) {
            candidates.add((NumericCandidateStructure<E>) newCandidate(seed));
        }

        Collections.sort(candidates);
        this.bestCandidate = (NumericCandidateStructure<E>) copy(candidates.get(0));
    }

    @Override
    protected NumericCandidateStructure<E> singleIteration() {

        if (multiCore) {
            for (final NumericCandidateStructure<E> c : candidates) {
                completionService.submit(() -> {
                    NumericCandidateStructure<E> c1;
                    NumericCandidateStructure<E> c2, c3;
                    do {
                        int rand = rng.nextInt(candidates.size());
                        c1 = candidates.get(rand);
                    } while (c1 == c);
                    do {
                        int rand = rng.nextInt(candidates.size());
                        c2 = candidates.get(rand);
                    } while (c2 == c && c2 == c1);
                    do {
                        int rand = rng.nextInt(candidates.size());
                        c3 = candidates.get(rand);
                    } while (c3 == c && c3 == c1 && c3 == c2);
                    int R = rng.nextInt(getDimension());
                    NumericCandidateStructure<E> sample = (NumericCandidateStructure<E>) newCandidate();
                    for (int i = 0; i < sample.size(); i++) {
                        if ((rng.nextDouble() < CR) || (i == R)) {
                            double value = c1.get(i).doubleValue() + F * (c2.get(i).doubleValue() - c3.get(i).doubleValue());
                            sample.set(i, value);
                        } else {
                            sample.set(i, c.get(i));
                        }
                    }
                    sample.clamp(0, 1);
                    evaluateAndUpdate(sample);
                    if (sample.getCost() < c.getCost()) {
                        candidates.set(candidates.indexOf(c), sample);
                        synchronized (mutex) {
                            if (sample.getCost() < bestCandidate.getCost()) {
                                bestCandidate = sample;
                            }
                        }
                    }
                }, true);
            }
            for (NumericCandidateStructure<E> c : candidates) {
                try {
                    completionService.take().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(DifferentialEvolution.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (NumericCandidateStructure<E> c : candidates) {
                NumericCandidateStructure<E> c1;
                NumericCandidateStructure<E> c2, c3;
                do {
                    int rand = rng.nextInt(candidates.size());
                    c1 = candidates.get(rand);
                } while (c1 == c);
                do {
                    int rand = rng.nextInt(candidates.size());
                    c2 = candidates.get(rand);
                } while (c2 == c && c2 == c1);
                do {
                    int rand = rng.nextInt(candidates.size());
                    c3 = candidates.get(rand);
                } while (c3 == c && c3 == c1 && c3 == c2);
                int R = rng.nextInt(c.size());
                NumericCandidateStructure<E> sample = (NumericCandidateStructure<E>) newCandidate();
                for (int i = 0; i < sample.size(); i++) {
                    if ((rng.nextDouble() < CR) || (i == R)) {
                        double value = c1.get(i).doubleValue() + F * (c2.get(i).doubleValue() - c3.get(i).doubleValue());
                        sample.set(i, value);
                    } else {
                        sample.set(i, c.get(i));
                    }
                }
                sample.clamp(0, 1);
                evaluateAndUpdate(sample);
                if (sample.getCost() < c.getCost()) {
                    candidates.set(candidates.indexOf(c), sample);
                    if (sample.getCost() < bestCandidate.getCost()) {
                        bestCandidate = sample;
                    }
                }
            }
        }

        return (NumericCandidateStructure<E>) copy(bestCandidate);
    }

    public int getNP() {
        return NP;
    }

    public void setNP(int NP) {
        this.NP = NP;
    }

    public double getF() {
        return F;
    }

    public void setF(double F) {
        this.F = F;
    }

    public double getCR() {
        return CR;
    }

    public void setCR(double CR) {
        this.CR = CR;
    }

//    @Override
//    public int getNumberOfFreeParameters() {
//        return 3;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        this.NP = (int) new NormUtil(1, 0, 60, 10).normalize(array.get(0));
//        this.F = new NormUtil(1, 0, 2, 0.1).normalize(array.get(1));
//        this.CR = new NormUtil(1, 0, 1, 0.1).normalize(array.get(2));
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(NP, F, CR);
//    }
    private class Candidates extends ArrayList<NumericCandidateStructure<E>> {

        public Candidates(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add((NumericCandidateStructure<E>) random());
            }
        }

    }
}
