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
package no.hials.jiop.evolutionary.de;

import java.util.Random;
import no.hials.jiop.AbstractAlgorithm;
import no.hials.jiop.Evaluator;
import no.hials.jiop.candidates2.Candidate;
import no.hials.jiop.candidates2.NumericEncoding;
import no.hials.jiop.factories.NumericEncodingFactory;
import no.hials.utilities.NormUtil;

/**
 * A Differential Evolution implementation
 *
 * @author Lars Ivar Hatledal
 */
public class DifferentialEvolution<E> extends AbstractAlgorithm<E>{

    private double F, CR;

      public DifferentialEvolution(int size, double F, double CR, NumericEncodingFactory<E> encodingFactory, Evaluator<E> evaluator, boolean multiThreaded) {
        this(size, F, CR, encodingFactory, evaluator, multiThreaded ? "MultiThreaded Differential Evolution" : "SingleThreaded Differential Evolution", multiThreaded);
    }
    
    public DifferentialEvolution(int size, double F, double CR, NumericEncodingFactory<E> encodingFactory, Evaluator<E> evaluator, String name, boolean multiThreaded) {
        super(size, encodingFactory, evaluator, name, multiThreaded);
        this.F = F;
        this.CR = CR;
    }

  

    @Override
    protected void candidateUpdate(final Candidate<E> cand) {
        Random rng = new Random();
        NumericEncoding<E> c = (NumericEncoding<E>) cand.getEncoding();
        NumericEncoding<E> c1;
        NumericEncoding<E> c2, c3;
        do {
            int rand = rng.nextInt(candidates.size());
            c1 = (NumericEncoding<E>) candidates.get(rand);
        } while (c1 == c);
        do {
            int rand = rng.nextInt(candidates.size());
            c2 = (NumericEncoding<E>) candidates.get(rand);
        } while (c2 == c && c2 == c1);
        do {
            int rand = rng.nextInt(candidates.size());
            c3 = (NumericEncoding<E>) candidates.get(rand);
        } while (c3 == c && c3 == c1 && c3 == c2);
        int R = rng.nextInt(getDimension());
        NumericEncoding<E> sample = (NumericEncoding<E>) newCandidate().getEncoding();
        for (int i = 0; i < sample.size(); i++) {
            if ((rng.nextDouble() < CR) || (i == R)) {
                double value = c1.get(i).doubleValue() + F * (c2.get(i).doubleValue() - c3.get(i).doubleValue());
                sample.set(i, value);
            } else {
                sample.set(i, c.get(i));
            }
        }
        sample.clamp(0, 1);
        Candidate<E> evaluateAndUpdate = evaluateAndUpdate(sample);
        if (evaluateAndUpdate.getCost() < cand.getCost()) {
            c.setElements(sample.getElements(), evaluateAndUpdate.getCost());
            setBestCandidateIfBetter(c);
        }
    }

//    private void threadingTask(final NumericCandidate<E> c) {
//        NumericCandidate<E> c1;
//        NumericCandidate<E> c2, c3;
//        do {
//            int rand = rng.nextInt(population.size());
//            c1 = (NumericCandidate<E>) population.get(rand);
//        } while (c1 == c);
//        do {
//            int rand = rng.nextInt(population.size());
//            c2 = (NumericCandidate<E>) population.get(rand);
//        } while (c2 == c && c2 == c1);
//        do {
//            int rand = rng.nextInt(population.size());
//            c3 = (NumericCandidate<E>) population.get(rand);
//        } while (c3 == c && c3 == c1 && c3 == c2);
//        int R = rng.nextInt(getDimension());
//        NumericCandidate<E> sample = (NumericCandidate<E>) newCandidate();
//        for (int i = 0; i < sample.size(); i++) {
//            if ((rng.nextDouble() < CR) || (i == R)) {
//                double value = c1.get(i).doubleValue() + F * (c2.get(i).doubleValue() - c3.get(i).doubleValue());
//                sample.set(i, value);
//            } else {
//                sample.set(i, c.get(i));
//            }
//        }
//        sample.clamp(0, 1);
//        evaluateAndUpdate(sample);
//        if (sample.getCost() < c.getCost()) {
//            c.setElements(sample.getElements(), sample.getCost());
//            setBestCandidateIfBetter(c);
//        }
//    }

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
//
//    @Override
//    public int getNumberOfFreeParameters() {
//        return 3;
//    }
//
//    @Override
//    public void setFreeParameters(double[] array) {
//        this.size = (int) new NormUtil(1, 0, 60, 10).normalize(array[0]);
//        this.F = new NormUtil(1, 0, 2, 0.1).normalize(array[1]);
//        this.CR = new NormUtil(1, 0, 1, 0.1).normalize(array[2]);
//    }
//
//    @Override
//    public double[] getFreeParameters() {
//        return new double[]{size, F, CR};
//    }
}
