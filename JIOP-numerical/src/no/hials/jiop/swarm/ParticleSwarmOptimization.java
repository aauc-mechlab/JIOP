///*
// * Copyright (c) 2014, Aalesund University College 
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * * Redistributions of source code must retain the above copyright notice, this
// *   list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
//package no.hials.jiop.swarm;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorCompletionService;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import no.hials.jiop.Algorithm;
//import no.hials.jiop.Candidate;
//import no.hials.utilities.DoubleArray;
//import no.hials.utilities.NormUtil;
//
///**
// *
// * @author Lars Ivar Hatledal
// */
//public class ParticleSwarmOptimization extends Algorithm {
//
//    private final Object mutex = new Object();
//    private final ExecutorService pool = Executors.newCachedThreadPool();
//    private final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
//
//    public int size;
//    private Swarm swarm;
//    public double omega = 0.729, c1 = 1.49445, c2 = 1.49445, maxVel = 0.1;
//
//    private boolean multiCore;
//    private Candidate bestCandidate;
//
//    public ParticleSwarmOptimization(int size,  boolean multiCore) {
//        super("Particle Swarm Optimization " + multiCore);
//        this.size = size;
//        this.multiCore = multiCore;
//    }
//
//    public ParticleSwarmOptimization(int size, double omega, double c1, double c2, boolean multiCore) {
//        this(size, multiCore);
//        this.size = size;
//        this.omega = omega;
//        this.c1 = c1;
//        this.c2 = c2;
//    }
//
//    @Override
//    public void subInit() {
//        this.swarm = new Swarm(size);
//        this.bestCandidate = swarm.get(0).copy();
//    }
//
//    @Override
//    public void subInit(DoubleArray... seeds) {
//        this.swarm = new Swarm(size - seeds.length);
//        for (DoubleArray seed : seeds) {
//            swarm.add(new Particle(seed, getEvaluator().evaluate(seed)));
//        }
//        Collections.sort(swarm);
//        this.bestCandidate = swarm.get(0).copy();
//    }
//
//    @Override
//    protected Candidate singleIteration() {
//        if (multiCore) {
//            for (final Particle particle : swarm) {
//                completionService.submit(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < getDimension(); i++) {
//                            double li = particle.localBest.get(i);
//                            double gi = bestCandidate.get(i);
//                            double pi = particle.get(i);
//                            double vi = particle.velocity.get(i);
//
//                            double newVel = (omega * vi) + (rng.nextDouble() * c1 * (li - pi)) + (rng.nextDouble() * c2 * (gi - pi));
//
//                            if (Math.abs(newVel) > maxVel) {
//                                newVel = newVel > 0 ? maxVel : -maxVel;
//                            }
//
//                            double newPos = pi + newVel;
//                            if (newPos < 0) {
//                                newPos = 0;
//                            } else if (newPos > 1) {
//                                newPos = 1;
//                            }
//                            particle.set(i, newPos);
//                            particle.velocity.set(i, newVel);
//                        }
//                        double cost = getEvaluator().evaluate(particle);
//                        particle.setCost(cost);
//                        if (cost < particle.localBest.getCost()) {
//                            particle.localBest = particle.copy();
//                        }
//                        synchronized (mutex) {
//                            if (cost < bestCandidate.getCost()) {
//                                bestCandidate = particle.copy();
//                            }
//                        }
//                    }
//                }, true);
//
//            }
//            for (Particle p : swarm) {
//                try {
//                    completionService.take().get();
//                } catch (InterruptedException | ExecutionException ex) {
//                    Logger.getLogger(MultiSwarmOptimization.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        } else {
//            for (Particle particle : swarm) {
//                for (int i = 0; i < getDimension(); i++) {
//                    double li = particle.localBest.get(i);
//                    double gi = bestCandidate.get(i);
//                    double pi = particle.get(i);
//                    double vi = particle.velocity.get(i);
//
//                    double newVel = (omega * vi) + (rng.nextDouble() * c1 * (li - pi)) + (rng.nextDouble() * c2 * (gi - pi));
//
//                    if (Math.abs(newVel) > maxVel) {
//                        newVel = newVel > 0 ? maxVel : -maxVel;
//                    }
//
//                    double newPos = pi + newVel;
//                    if (newPos < 0) {
//                        newPos = 0;
//                    } else if (newPos > 1) {
//                        newPos = 1;
//                    }
//                    particle.set(i, newPos);
//                    particle.velocity.set(i, newVel);
//                }
//                double cost = getEvaluator().evaluate(particle);
//                particle.setCost(cost);
//                if (cost < particle.localBest.getCost()) {
//                    particle.localBest = particle.copy();
//                }
//                if (cost < bestCandidate.getCost()) {
//                    bestCandidate = particle.copy();
//                }
//            }
//        }
//        return bestCandidate.copy();
//    }
//
//    public double getOmega() {
//        return omega;
//    }
//
//    public void setOmega(double omega) {
//        this.omega = omega;
//    }
//
//    public double getC1() {
//        return c1;
//    }
//
//    public void setC1(double c1) {
//        this.c1 = c1;
//    }
//
//    public double getC2() {
//        return c2;
//    }
//
//    public void setC2(double c2) {
//        this.c2 = c2;
//    }
//
//    public double getMaxVel() {
//        return maxVel;
//    }
//
//    public void setMaxVel(double maxVel) {
//        this.maxVel = maxVel;
//    }
//
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
//
//    private class Swarm extends ArrayList<Particle> {
//
//        public Swarm(int size) {
//            super(size);
//            for (int i = 0; i < size; i++) {
//                DoubleArray random = Candidate.random(getDimension());
//                add(new Particle(random, getEvaluator().evaluate(random)));
//            }
//            Collections.sort(this);
//        }
//    }
//
//    private class Particle extends Candidate {
//
//        public Candidate localBest;
//        public DoubleArray velocity;
//
//        public Particle(DoubleArray array, double cost) {
//            super(array, cost);
//            this.localBest = super.copy();
//            this.velocity = new DoubleArray(length);
//        }
//
//        public Particle(Particle particle) {
//            super(particle);
//            this.localBest = particle.localBest.copy();
//            this.velocity = particle.velocity.copy();
//        }
//
//        @Override
//        public Particle copy() {
//            return new Particle(this);
//        }
//    }
//}
