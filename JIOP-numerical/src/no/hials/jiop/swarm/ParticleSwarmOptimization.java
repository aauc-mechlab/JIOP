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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.Algorithm;
import no.hials.jiop.candidates.Candidate;
import no.hials.jiop.candidates.NumericCandidate;
import no.hials.jiop.candidates.ParticleCandidate;

/**
 *
 * @author Lars Ivar Hatledal
 */
public class ParticleSwarmOptimization<E> extends Algorithm<E> {

    private final Object mutex = new Object();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);

    public int size;
    private Swarm swarm;
    public double omega = 0.729, c1 = 1.49445, c2 = 1.49445, maxVel = 0.5;

    private boolean multiCore;

    public ParticleSwarmOptimization(Class<?> clazz, int size, boolean multiCore) {
        super(clazz, "Particle Swarm Optimization " + multiCore);
        this.size = size;
        this.multiCore = multiCore;
    }

    public ParticleSwarmOptimization(Class<?> clazz, int size, double omega, double c1, double c2, boolean multiCore) {
        this(clazz, size, multiCore);
        this.size = size;
        this.omega = omega;
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public Candidate<E> subInit() {
        this.swarm = new Swarm(size);
        Collections.sort(swarm);
        return swarm.get(0);
    }

    @Override
    public Candidate<E> subInit(List<E> seeds) {
        this.swarm = new Swarm(size - seeds.size());
        for (E seed : seeds) {
            swarm.add((ParticleCandidate<E>) newCandidate(seed));
        }

        Collections.sort(swarm);
        return swarm.get(0);
    }

    @Override
    protected Candidate<E> singleIteration() {
        if (multiCore) {
            for (final ParticleCandidate<E> particle : swarm) {
                completionService.submit(() -> {
                    for (int i = 0; i < getDimension(); i++) {
                        double li = particle.getLocalBest().get(i).doubleValue();
                        double gi = ((NumericCandidate<E>) getBestCandidate()).get(i).doubleValue();
                        double pi = particle.get(i).doubleValue();
                        double vi = particle.getVelocityAt(i).doubleValue();

                        double newVel = (omega * vi) + (rng.nextDouble() * c1 * (li - pi)) + (rng.nextDouble() * c2 * (gi - pi));

                        if (Math.abs(newVel) > maxVel) {
                            newVel = newVel > 0 ? maxVel : -maxVel;
                        }

                        double newPos = pi + newVel;
                        if (newPos < 0) {
                            newPos = 0;
                        } else if (newPos > 1) {
                            newPos = 1;
                        }
                        particle.set(i, newPos);
                        particle.setVelocityAt(i, newVel);
                    }
                    double cost = evaluate(particle);
                    particle.setCost(cost);
                    if (cost < particle.getLocalBest().getCost()) {
                        particle.setLocalBest((ParticleCandidate<E>) (particle).copy());
                    }
                    setBestCandidateIfBetter(particle);
                }, true);

            }
            for (ParticleCandidate<E> p : swarm) {
                try {
                    completionService.take().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MultiSwarmOptimization.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (ParticleCandidate<E> particle : swarm) {
                for (int i = 0; i < getDimension(); i++) {
                    double li = particle.getLocalBest().get(i).doubleValue();
                    double gi = ((NumericCandidate<E>) getBestCandidate()).get(i).doubleValue();
                    double pi = particle.get(i).doubleValue();
                    double vi = particle.getVelocityAt(i).doubleValue();

                    double newVel = (omega * vi) + (rng.nextDouble() * c1 * (li - pi)) + (rng.nextDouble() * c2 * (gi - pi));

                    if (Math.abs(newVel) > maxVel) {
                        newVel = newVel > 0 ? maxVel : -maxVel;
                    }

                    double newPos = pi + newVel;
                    if (newPos < 0) {
                        newPos = 0;
                    } else if (newPos > 1) {
                        newPos = 1;
                    }
                    particle.set(i, newPos);
                    particle.setVelocityAt(i, newVel);
                }
                double cost = evaluate(particle);
                particle.setCost(cost);
                if (cost < particle.getLocalBest().getCost()) {
                    particle.setLocalBest((ParticleCandidate<E>) (particle).copy());
                }
                setBestCandidateIfBetter(particle);
            }
        }
        return getBestCandidate();
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC2() {
        return c2;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }

    public double getMaxVel() {
        return maxVel;
    }

    public void setMaxVel(double maxVel) {
        this.maxVel = maxVel;
    }

//    @Override
//    public int getNumberOfFreeParameters() {
//        return 4;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        this.omega = new NormUtil(1, 0, 1, 0.01).normalize(array.get(0));
//        this.c1 = new NormUtil(1, 0, 2, 0.01).normalize(array.get(1));
//        this.c2 = new NormUtil(1, 0, 2, 0.01).normalize(array.get(2));
//        this.maxVel = new NormUtil(1, 0, 1, 0.0001).normalize(array.get(3));
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(omega,c1,c2,maxVel);
//    }
    private class Swarm extends ArrayList<ParticleCandidate<E>> {

        public Swarm(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add((ParticleCandidate<E>) newCandidate());
            }
        }
    }

}
