/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

/**
 *
 * @author LarsIvar
 */
public class Candidate extends DoubleArray implements Comparable<Candidate> {

    private double cost;

    public Candidate(DoubleArray array) {
        this(array, Double.MAX_VALUE);
    }

    protected Candidate(Candidate candidate) {
        super(candidate);
        this.cost = candidate.getCost();
    }

    public Candidate(DoubleArray array, double cost) {
        super(array.getArray());
        this.cost = cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setVariables(double[] variables) {
        super.array = variables;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public Candidate copy() {
        return new Candidate(this);
    }

    @Override
    public int compareTo(Candidate c) {
        if (this.cost == c.getCost()) {
            return 0;
        } else if (this.cost < c.getCost()) {
            return -1;
        } else {
            return 1;
        }
    }

    public static Candidate randomCandidate(int length, Evaluator eval) {
        DoubleArray random = DoubleArray.random(length);
        return new Candidate(random, eval.evaluate(random));
    }
    
    public static Candidate neighborCandidate(Candidate candidate, double factor, Evaluator eval) {
        DoubleArray array = DoubleArray.neighbor(candidate, factor);
        Candidate neighbor = new Candidate(array, eval.evaluate(array));
        neighbor.clamp(0, 1);
        return neighbor;
    }

}
