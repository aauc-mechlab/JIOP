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
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import no.hials.jiop.AbstractAlgorithm;
//import no.hials.jiop.Evaluator;
//import no.hials.jiop.candidates.Candidate;
//import no.hials.jiop.candidates.NumericCandidate;
//import no.hials.jiop.candidates.ParticleCandidate;
//
///**
// *
// * @author Lars Ivar Hatledal
// */
//public class MultiSwarmOptimization<E> extends AbstractAlgorithm<E> {
//
//    private List<Swarm> swarms;
//    public int numSwarms, numParticles;
//    public double omega = 0.729, c1 = 1.49445, c2 = 1.49445, c3 = 0.3645, maxVel = 0.1;
//
//    boolean multiThreaded = false;
//
//    public MultiSwarmOptimization(Class<?> clazz, int numSwarms, int numParticles, Evaluator<E> evalutor, String name, boolean multiCore) {
//        super(clazz, evalutor, name);
//        this.numSwarms = numSwarms;
//        this.numParticles = numParticles;
//        this.multiThreaded = multiCore;
//    }
//
//    public MultiSwarmOptimization(Class<?> clazz, int numSwarms, int numParticles, Evaluator<E> evalutor, boolean multiThreaded) {
//        this(clazz, numSwarms, numParticles, evalutor, multiThreaded ? "MultiThreaded Multi Swarm Optimization" : "SingleThreaded Multi Swarm Optimization", multiThreaded);
//    }
//
//    @Override
//    public Candidate<E> subInit() {
//        Candidate<E> bestCandidate = null;
//        this.swarms = new ArrayList<>(numSwarms);
//        for (int i = 0; i < numSwarms; i++) {
//            Swarm swarm = new Swarm(numParticles);
//            if (bestCandidate == null) {
//                bestCandidate = (ParticleCandidate<E>) (swarm.get(0).copy());
//            } else {
//                if (swarm.get(0).getCost() < bestCandidate.getCost()) {
//                    bestCandidate = (NumericCandidate<E>) (swarm.get(0).copy());
//                }
//            }
//            swarms.add(swarm);
//        }
//        return bestCandidate;
//    }
//
//    @Override
//    public Candidate<E> subInit(List<E> seeds) {
//        Candidate<E> bestCandidate = subInit();
//        double cost = getEvaluator().evaluate(seeds.get(0));
//        if (cost < bestCandidate.getCost()) {
//            bestCandidate = newCandidate(seeds.get(0));
//        }
//        return bestCandidate;
//    }
//
//    @Override
//    protected void candidateUpdate() {
//
//        for (Swarm swarm : swarms) {
//            if (multiThreaded) {
//                getCompletionService().submit(() -> threadingTask(swarm), null);
//            } else {
//                threadingTask(swarm);
//            }
//        }
//        if (multiThreaded) {
//            for (Swarm swarm : swarms) {
//                try {
//                    getCompletionService().take().get();
//                } catch (InterruptedException | ExecutionException ex) {
//                    Logger.getLogger(MultiSwarmOptimization.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//
//    private void threadingTask(final Swarm swarm) {
//        for (ParticleCandidate<E> particle : swarm) {
//            for (int i = 0; i < getDimension(); i++) {
//                double li = particle.getLocalBest().get(i).doubleValue();
//                double si = swarm.swarmBest.get(i).doubleValue();
//                double gi = ((NumericCandidate<E>) getBestCandidate()).get(i).doubleValue();
//                double pi = particle.get(i).doubleValue();
//                double vi = particle.getVelocityAt(i).doubleValue();
//
//                double newVel = (omega * vi)
//                        + (rng.nextDouble() * c1 * (li - pi))
//                        + (rng.nextDouble() * c2 * (si - pi))
//                        + (rng.nextDouble() * c3 * (gi - pi));
//
//                if (Math.abs(newVel) > maxVel) {
//                    newVel = newVel > 0 ? maxVel : -maxVel;
//                }
//
//                double newPos = pi + newVel;
//                if (newPos < 0) {
//                    newPos = 0;
//                } else if (newPos > 1) {
//                    newPos = 1;
//                }
//                particle.set(i, newPos);
//                particle.setVelocityAt(i, newVel);
//            }
//
//            double cost = evaluate(particle);
//            particle.setCost(cost);
//            if (cost < particle.getLocalBest().getCost()) {
//                particle.setLocalBest((ParticleCandidate<E>) (particle).copy());
//            }
//            if (cost < swarm.swarmBest.getCost()) {
//                swarm.swarmBest = (ParticleCandidate<E>) (particle).copy();
//                setBestCandidateIfBetter(particle);
//            }
//        }
//    }
//
////    @Override
////    public int getNumberOfFreeParameters() {
////        return 5;
////    }
////
////    @Override
////    public void setFreeParameters(DoubleArray array) {
////        this.omega = new NormUtil(1, 0, 1, 0.1).normalize(array.get(0));
////        this.c1 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(1));
////        this.c2 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(2));
////        this.c3 = new NormUtil(1, 0, 2, 0.1).normalize(array.get(3));
////        this.maxVel = new NormUtil(1, 0, 1, 0.1).normalize(array.get(4));
////    }
////
////    @Override
////    public DoubleArray getFreeParameters() {
////        return new DoubleArray(omega, c1, c2, c3, maxVel);
////    }
//    private class Swarm extends ArrayList<ParticleCandidate<E>> {
//
//        public ParticleCandidate<E> swarmBest;
//
//        public Swarm(int size) {
//            super(size);
//            for (int i = 0; i < size; i++) {
//                add((ParticleCandidate<E>) newCandidate());
//            }
//            Collections.sort(this);
//            swarmBest = (ParticleCandidate<E>) (get(0).copy());
//        }
//    }
//}
