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
public class SimulatedAnnealing extends Algorithm {

    private double startingTemperature;
    private double temperature, alpha;
    private Candidate current, bestCandidate;

    public SimulatedAnnealing(double startingTemperature, double alpha, int dimension, Evaluator evaluator) {
        super("Simulated Annealing", dimension, evaluator);
        this.startingTemperature = startingTemperature;
        this.alpha = alpha;
    }

    @Override
    public void init() {
        super.init();
        this.temperature = startingTemperature;
        this.current = Candidate.randomCandidate(getDimension(), getEvaluator());
        this.bestCandidate = current.copy();
    }

    @Override
    public void init(DoubleArray... seed) {
        super.init();
        this.current = new Candidate(seed[0], getEvaluator().evaluate(seed[0]));
        this.bestCandidate = current.copy();
    }

    @Override
    protected Candidate singleIteration() {
        Candidate newSample = Candidate.neighborCandidate(current, bestCandidate.getCost()/5, getEvaluator());
        if (doAccept(current, newSample)) {
            current = newSample.copy();
        }
        if (newSample.getCost() < bestCandidate.getCost()) {
            bestCandidate = newSample.copy();
        }
        temperature *= alpha;
        return bestCandidate.copy();
    }

    /**
     * Should we accept the new solution based on the Metropolis criteria?
     *
     * @param current the current solution
     * @param newSample the new solution
     * @return whether or not the new solution should be accepted
     */
    private boolean doAccept(Candidate current, Candidate newSample) {
        return newSample.getCost() < current.getCost() | Math.exp(-(newSample.getCost() - current.getCost()) / temperature) > Math.random();
    }

    @Override
    public int getNumberOfFreeParameters() {
       return 2;
    }

    @Override
    public void setFreeParameters(DoubleArray array) {
        this.startingTemperature = new NormUtil(1, 0, 1000, 10).normalize(array.get(0));
        this.alpha = new NormUtil(1, 0, 0.995, 0.8).normalize(array.get(1));
    }

    @Override
    public DoubleArray getFreeParameters() {
        return new DoubleArray(startingTemperature, alpha);
    }

}
