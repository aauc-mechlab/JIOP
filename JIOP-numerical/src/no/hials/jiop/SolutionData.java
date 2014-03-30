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
public class SolutionData {

    public final long nano;
    public final double cost;
    public final int iterations;
    public final DoubleArray solution;

    public SolutionData(DoubleArray solution, double cost, int iterations, long nano) {
        this.cost = cost;
        this.nano = nano;
        this.solution = solution;
        this.iterations = iterations;
    }

    @Override
    public String toString() {
        return "SolutionData{" + "#cost=" + cost + ", \t#iterations=" + iterations + ", \t#time=" + nano + ", \t#solution=" + solution + '}';
    }
    
    
}
