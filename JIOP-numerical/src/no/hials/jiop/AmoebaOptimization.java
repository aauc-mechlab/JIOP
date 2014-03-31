/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author LarsIvar
 */
public class AmoebaOptimization extends Algorithm  {

    private int size;
    private double alpha = 1.0;  // Reflection
    private double beta = 0.5;   // Contraction
    private double gamma = 2.0;  // Expansion

    private Amoeba candidates;
//    private Candidate bestCandidate;

    public AmoebaOptimization(int size, int dimension, Evaluator evaluator) {
        super("Amoeba Optimization", dimension, evaluator);
        this.size = size;
        this.init();
    }

    @Override
    public void subInit() {
        this.candidates = new Amoeba(size);
        Collections.sort(candidates);
//        bestCandidate = candidates.get(0).copy();
    }

    @Override
    public void subInit(DoubleArray... seeds) {
        this.candidates = new Amoeba(size - seeds.length);
        for (DoubleArray seed : seeds) {
            candidates.add(new Candidate(seed, getEvaluator().evaluate(seed)));
        }
        Collections.sort(candidates);
//        bestCandidate = candidates.get(0).copy();
    }

    @Override
    protected Candidate singleIteration() {
        Candidate centroid = centroid();
        Candidate reflected = reflected(centroid);
        if (reflected.getCost() < candidates.get(0).getCost()) {
            Candidate expanded = expanded(reflected, centroid);
            if (expanded.getCost() < candidates.get(0).getCost()) {
                replaceWorst(expanded);
            } else {
                replaceWorst(reflected);
            }
            return candidates.get(0);  // Best solution
        }
        if (isWorseThanAllButWorst(reflected) == true) {
            if (reflected.getCost() <= candidates.get(size - 1).getCost()) {
                replaceWorst(reflected);
            }
            Candidate contracted = contracted(centroid);
            if (contracted.getCost() > candidates.get(size - 1).getCost()) {
                shrink();
            } else {
                replaceWorst(contracted);
            }
            return candidates.get(0);  // Best solution
        }
        replaceWorst(reflected);
        return candidates.get(0);  // Best solution
    }

   

    public Candidate centroid() {
        double[] c = new double[getDimension()];
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < getDimension(); j++) {
                c[j] += candidates.get(i).get(j);
            }
        }
        // Accumulate sum of each component
        for (int j = 0; j < getDimension(); j++) {
            c[j] = c[j] / (size - 1);
        }
        DoubleArray doubleArray = new DoubleArray(c);
        doubleArray.clamp(0, 1);
        Candidate s = new Candidate(doubleArray, getEvaluator().evaluate(doubleArray));
        return s;
    }

    public Candidate reflected(Candidate centroid) {
        double[] r = new double[getDimension()];
        double[] worst = candidates.get(size - 1).getArray();  // Convenience only
        for (int j = 0; j < getDimension(); j++) {
            r[j] = ((1 + alpha) * centroid.get(j)) - (alpha * worst[j]);
        }
        DoubleArray doubleArray = new DoubleArray(r);
        doubleArray.clamp(0, 1);
        Candidate s = new Candidate(doubleArray, getEvaluator().evaluate(doubleArray));
        return s;
    }

    public Candidate expanded(Candidate reflected, Candidate centroid) {
        double[] e = new double[getDimension()];
        for (int j = 0; j < getDimension(); j++) {
            e[j] = (gamma * reflected.get(j)) + ((1 - gamma) * centroid.get(j));
        }
        DoubleArray doubleArray = new DoubleArray(e);
        doubleArray.clamp(0, 1);
        Candidate s = new Candidate(doubleArray, getEvaluator().evaluate(doubleArray));
        return s;
    }

    public Candidate contracted(Candidate centroid) {
        double[] v = new double[getDimension()];  // Didn't want to reuse 'c' from centroid routine
        double[] worst = candidates.get(size - 1).getArray();   // Convenience only
        for (int j = 0; j < getDimension(); j++) {
            v[j] = (beta * worst[j]) + ((1 - beta) * centroid.get(j));
        }
        DoubleArray doubleArray = new DoubleArray(v);
        doubleArray.clamp(0, 1);
        Candidate s = new Candidate(doubleArray, getEvaluator().evaluate(doubleArray));
        return s;
    }

    public void replaceWorst(Candidate newSolution) {
        candidates.set(size - 1, newSolution.copy());
        Collections.sort(candidates);
//        this.bestCandidate = candidates.get(0).copy();
    }

    public void shrink() {
        for (int i = 1; i < size; i++) // start at [1]
        {
            for (int j = 0; j < getDimension(); j++) {
                double value = (candidates.get(i).get(j) + candidates.get(0).get(j)) / 2d;
                if (value < 0) {
                    value = 0;
                } else if (value > 1) {
                    value = 1;
                }
                candidates.get(i).set(j, value);
            }
            candidates.get(i).setCost(getEvaluator().evaluate(candidates.get(i)));
        }
        Collections.sort(candidates);
//        bestCandidate = candidates.get(0).copy();
    }

    public boolean isWorseThanAllButWorst(Candidate reflected) {
        for (int i = 0; i < size - 1; i++) {
            if (reflected.getCost() <= candidates.get(i).getCost()) // Found worse solution
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getNumberOfFreeParameters() {
        return 4;
    }

    @Override
    public void setFreeParameters(DoubleArray array) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleArray getFreeParameters() {
        return new DoubleArray(size, alpha, beta, gamma);
    }

    private class Amoeba extends ArrayList<Candidate> {

        public Amoeba(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                add(Candidate.randomCandidate(getDimension(), getEvaluator()));
            }
        }
    }
}
