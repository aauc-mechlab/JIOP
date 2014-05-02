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
//import java.util.List;
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
//public class MultiSwarmOptimization extends Algorithm {
//
//    private final Object mutex = new Object();
//    private final ExecutorService pool = Executors.newCachedThreadPool();
//    private final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);
//
//    private List<Swarm> swarms;
//    public int numSwarms, numParticles;
//    public double omega = 0.729, c1 = 1.49445, c2 = 1.49445, c3 = 0.3645, maxVel = 0.1;
//
//    private Candidate bestCandidate;
//
//    boolean multiCore = false;
//
//    public MultiSwarmOptimization(int numSwarms, int numParticles, boolean multiCore) {
//        super("Multi Swarm Optimization " + multiCore);
//        this.numSwarms = numSwarms;
//        this.numParticles = numParticles;
//        this.multiCore = multiCore;
//    }
//
//    @Override
//    public void subInit() {
//        this.bestCandidate = null;
//        this.swarms = new ArrayList<>(numSwarms);
//        for (int i = 0; i < numSwarms; i++) {
//            Swarm swarm = new Swarm(numParticles);
//            if (bestCandidate == null) {
//                bestCandidate = swarm.get(0).copy();
//            } else {
//                if (swarm.get(0).getCost() < bestCandidate.getCost()) {
//                    bestCandidate = swarm.get(0).copy();
//                }
//            }
//            swarms.add(swarm);
//        }
//    }
//
//    @Override
//    public void subInit(DoubleArray... seeds) {
//        subInit();
//        double cost = getEvaluator().evaluate(seeds[0]);
//        if (cost < bestCandidate.getCost()) {
//            this.bestCandidate = new Candidate(seeds[0], cost).copy();
//        }
//    }
//
//    @Override
//    protected Candidate singleIteration() {
//        if (multiCore) {
//            for (final Swarm swarm : swarms) {
//                completionService.submit(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        for (Particle particle : swarm) {
//                            for (int i = 0; i < getDimension(); i++) {
//                                double li = particle.localBest.get(i);
//                                double si = swarm.swarmBest.get(i);
//                                double gi = bestCandidate.get(i);
//                                double pi = particle.get(i);
//                                double vi = particle.velocity.get(i);
//
//                                double newVel = (omega * vi)
//                                        + (rng.nextDouble() * c1 * (li - pi))
//                                        + (rng.nextDouble() * c2 * (si - pi))
//                                        + (rng.nextDouble() * c3 * (gi - pi));
//
//                                if (Math.abs(newVel) > maxVel) {
//                                    newVel = newVel > 0 ? maxVel : -maxVel;
//                                }
//
//                                double newPos = pi + newVel;
//                                if (newPos < 0) {
//                                    newPos = 0;
//                                } else if (newPos > 1) {
//                                    newPos = 1;
//                                }
//                                particle.set(i, newPos);
//                                particle.velocity.set(i, newVel);
//                            }
//                            double cost = getEvaluator().evaluate(particle);
//                            particle.setCost(cost);
//                            if (cost < particle.localBest.getCost()) {
//                                particle.localBest = particle.copy();
//                            }
//                            if (cost < swarm.swarmBest.getCost()) {
//                                swarm.swarmBest = particle.copy();
//                            }
//                            synchronized (mutex) {
//                                if (cost < bestCandidate.getCost()) {
//                                    bestCandidate = particle.copy();
//                                }
//                            }
//                        }
//                    }
//                }, true);
//            }
//
//            for (final Swarm swarm : swarms) {
//                try {
//                    completionService.take().get();
//                } catch (InterruptedException | ExecutionException ex) {
//                    Logger.getLogger(MultiSwarmOptimization.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        } else {
//            for (final Swarm swarm : swarms) {
//                for (Particle particle : swarm) {
//                    for (int i = 0; i < getDimension(); i++) {
//                        double li = particle.localBest.get(i);
//                        double si = swarm.swarmBest.get(i);
//                        double gi = bestCandidate.get(i);
//                        double pi = particle.get(i);
//                        double vi = particle.velocity.get(i);
//
//                        double newVel = (omega * vi)
//                                + (rng.nextDouble() * c1 * (li - pi))
//                                + (rng.nextDouble() * c2 * (si - pi))
//                                + (rng.nextDouble() * c3 * (gi - pi));
//
//                        if (Math.abs(newVel) > maxVel) {
//                            newVel = newVel > 0 ? maxVel : -maxVel;
//                        }
//
//                        double newPos = pi + newVel;
//                        if (newPos < 0) {
//                            newPos = 0;
//                        } else if (newPos > 1) {
//                            newPos = 1;
//                        }
//                        particle.set(i, newPos);
//                        particle.velocity.set(i, newVel);
//                    }
//                    double cost = getEvaluator().evaluate(particle);
//                    particle.setCost(cost);
//                    if (cost < particle.localBest.getCost()) {
//                        particle.localBest = particle.copy();
//                    }
//                    if (cost < swarm.swarmBest.getCost()) {
//                        swarm.swarmBest = particle.copy();
//                    }
//                    if (cost < bestCandidate.getCost()) {
//                        bestCandidate = particle.copy();
//                    }
//                }
//            }
//        }
//        return bestCandidate.copy();
//    }
//
//    @Override
//    public int getNumberOfFreeParameters() {
//        return 5;
//    }
//
//    @Override
//    public void setFreeParameters(DoubleArray array) {
//        this.omega = new NormUtil(1, 0, 1, 0.1).normalize(array.get(0));
//        this.c1 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(1));
//        this.c2 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(2));
//        this.c3 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(3));
//        this.maxVel = new NormUtil(1, 0, 1, 0.1).normalize(array.get(4));
//    }
//
//    @Override
//    public DoubleArray getFreeParameters() {
//        return new DoubleArray(omega, c1, c2, c3, maxVel);
//    }
//
//    private class Swarm extends ArrayList<Particle> {
//
//        public Candidate swarmBest;
//
//        public Swarm(int size) {
//            super(size);
//            for (int i = 0; i < size; i++) {
//                DoubleArray random = Candidate.random(getDimension());
//                add(new Particle(random, getEvaluator().evaluate(random)));
//            }
//            Collections.sort(this);
//            swarmBest = get(0).copy();
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
