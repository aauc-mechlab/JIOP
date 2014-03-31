/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop.swarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.Algorithm;
import no.hials.jiop.Candidate;
import no.hials.jiop.DoubleArray;
import no.hials.jiop.Evaluator;
import no.hials.jiop.NormUtil;

/**
 *
 * @author LarsIvar
 */
public class ParticleSwarmOptimization extends Algorithm {

    private final Object mutex = new Object();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ExecutorCompletionService completionService = new ExecutorCompletionService(pool);

    public int size;
    private Swarm swarm;
    public double omega = 0.729, c1 = 1.49445, c2 = 1.49445, maxVel = 0.1;

    private boolean multiCore;
    private Candidate bestCandidate;

    public ParticleSwarmOptimization(int size, int dimension, Evaluator evaluator, boolean multiCore) {
        super("Particle Swarm Optimization " + multiCore, dimension, evaluator);
        this.size = size;
        this.multiCore = multiCore;
        this.init();
    }

    public ParticleSwarmOptimization(int size, double omega, double c1, double c2, int dimension, Evaluator evaluator, boolean multiCore) {
        this(size, dimension, evaluator, multiCore);
        this.size = size;
        this.omega = omega;
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public void subInit() {
        this.swarm = new Swarm(size);
        this.bestCandidate = swarm.get(0).copy();
    }

    @Override
    public void subInit(DoubleArray... seeds) {
        this.swarm = new Swarm(size - seeds.length);
        for (DoubleArray seed : seeds) {
            swarm.add(new Particle(seed, getEvaluator().evaluate(seed)));
        }
        Collections.sort(swarm);
        this.bestCandidate = swarm.get(0).copy();
    }

    @Override
    protected Candidate singleIteration() {
        if (multiCore) {
            for (final Particle particle : swarm) {
                completionService.submit(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < getDimension(); i++) {
                            double li = particle.localBest.get(i);
                            double gi = bestCandidate.get(i);
                            double pi = particle.get(i);
                            double vi = particle.velocity.get(i);

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
                            particle.velocity.set(i, newVel);
                        }
                        double cost = getEvaluator().evaluate(particle);
                        particle.setCost(cost);
                        if (cost < particle.localBest.getCost()) {
                            particle.localBest = particle.copy();
                        }
                        synchronized (mutex) {
                            if (cost < bestCandidate.getCost()) {
                                bestCandidate = particle.copy();
                            }
                        }
                    }
                }, true);

            }
            for (Particle p : swarm) {
                try {
                    completionService.take().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MultiSwarmOptimization.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            for (Particle particle : swarm) {
                for (int i = 0; i < getDimension(); i++) {
                    double li = particle.localBest.get(i);
                    double gi = bestCandidate.get(i);
                    double pi = particle.get(i);
                    double vi = particle.velocity.get(i);

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
                    particle.velocity.set(i, newVel);
                }
                double cost = getEvaluator().evaluate(particle);
                particle.setCost(cost);
                if (cost < particle.localBest.getCost()) {
                    particle.localBest = particle.copy();
                }
                if (cost < bestCandidate.getCost()) {
                    bestCandidate = particle.copy();
                }
            }
        }
        return bestCandidate.copy();
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

    @Override
    public int getNumberOfFreeParameters() {
        return 4;
    }

    @Override
    public void setFreeParameters(DoubleArray array) {
        this.omega = new NormUtil(1, 0, 1, 0.01).normalize(array.get(0));
        this.c1 = new NormUtil(1, 0, 2, 0.01).normalize(array.get(1));
        this.c2 = new NormUtil(1, 0, 2, 0.01).normalize(array.get(2));
        this.maxVel = new NormUtil(1, 0, 1, 0.0001).normalize(array.get(3));
    }

    @Override
    public DoubleArray getFreeParameters() {
        return new DoubleArray(omega,c1,c2,maxVel);
    }

    private class Swarm extends ArrayList<Particle> {

        public Swarm(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                DoubleArray random = Candidate.random(getDimension());
                add(new Particle(random, getEvaluator().evaluate(random)));
            }
            Collections.sort(this);
        }
    }

    private class Particle extends Candidate {

        public Candidate localBest;
        public DoubleArray velocity;

        public Particle(DoubleArray array, double cost) {
            super(array, cost);
            this.localBest = super.copy();
            this.velocity = new DoubleArray(length);
        }

        public Particle(Particle particle) {
            super(particle);
            this.localBest = particle.localBest.copy();
            this.velocity = particle.velocity.copy();
        }

        @Override
        public Particle copy() {
            return new Particle(this);
        }
    }
}
