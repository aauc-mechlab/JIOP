/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hials.jiop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author LarsIvar
 */
public class AntColonyOptimization extends Algorithm {

    private int size;
    private Colony colony;

    List<Candidate> permutationGraph = new LinkedList<>();

    public AntColonyOptimization(int dimension, Evaluator evaluator) {
        super("Ant Colony Optimization", dimension, evaluator);
        
        for (double[] d : getEquallySpacedJointValues(2000000)) {
            permutationGraph.add(new Candidate(new DoubleArray(d), getEvaluator().evaluate(new DoubleArray(d))));
        }
        Collections.sort(permutationGraph);
        
//         double[][] graph = new double[10][dimension];
         
//        for (int i = 0; i < graph.length; i++) {
//            for (int j = 0; j < graph[0].length; j++) {
//                graph[i][j] = new NormUtil(9, 0, 1, 0).normalize(i);
//            }
//            System.out.println(Arrays.toString(graph[i]));
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < graph.length; i++) {
//            sb.append(i);
//            if (i != graph.length - 1) {
//                sb.append("-");
//            }
//        }
//        PermutationsWithRepetition perm = new PermutationsWithRepetition(sb.toString(), dimension);
//        List<String> variations = perm.getVariations();
//
//        permutationGraph = new ArrayList<>(variations.size());
//        for (int i = 0; i < variations.size(); i++) {
//            String[] tmp = variations.get(i).split(":");
//            double[] aHomePos = new double[tmp.length];
//
//            for (int j = 0; j < aHomePos.length; j++) {
//                aHomePos[j] = graph[j][Integer.parseInt(tmp[j])];
//            }
//            permutationGraph.add(i, (aHomePos));
//            System.out.println(Arrays.toString(permutationGraph.get(i)));
//        }
       
    }

    @Override
    public void subInit() {
        this.colony = new Colony(size);
        Collections.sort(colony);
    }

    @Override
    public void subInit(DoubleArray... seeds) {
        this.colony = new Colony(size - seeds.length);
        for (DoubleArray seed : seeds) {
            colony.add(new Candidate(seed, getEvaluator().evaluate(seed)));
        }
        Collections.sort(colony);
    }

    @Override
    protected Candidate singleIteration() {
        for (Candidate c : permutationGraph) {
            c.setCost(getEvaluator().evaluate(c));
        }
        Collections.sort(permutationGraph);
        return permutationGraph.get(0);
    }

    @Override
    public int getNumberOfFreeParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFreeParameters(DoubleArray array) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DoubleArray getFreeParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<double[]> getEquallySpacedJointValues(int howMany) {
        int numberOfJointHomePositions = (int) Math.round(Math.pow(howMany, 1 / (double) getDimension()));
        ArrayList<double[]> homePositions = new ArrayList<>();
        ArrayList<ArrayList<Double>> jointHomePositions = new ArrayList<>();
        for (int k = 0; k < getDimension(); k++) {
            ArrayList<Double> list = new ArrayList<>();
            double step = Math.abs(1 - 0) / (double)(numberOfJointHomePositions - 1);
            list.add(1d);
            for (int i = 1; i < numberOfJointHomePositions - 1; i++) {
                list.add(1 - (i * step));
            }
            list.add(0d);
            jointHomePositions.add(list);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfJointHomePositions; i++) {
            sb.append(i);
            if (i != numberOfJointHomePositions - 1) {
                sb.append("-");
            }
        }
        PermutationsWithRepetition perm = new PermutationsWithRepetition(sb.toString(), getDimension());
        List<String> variations = perm.getVariations();

        for (int i = 0; i < variations.size(); i++) {
            String[] tmp = variations.get(i).split(":");
            double[] aHomePos = new double[tmp.length];

            for (int j = 0; j < aHomePos.length; j++) {
                aHomePos[j] = jointHomePositions.get(j).get(Integer.parseInt(tmp[j]));
            }
            homePositions.add(i, (aHomePos));
        }
        return homePositions;
    }

    private class Colony extends ArrayList<Candidate> {

        public Colony(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
//                add(new Candidate(new DoubleArray()), getEvaluator()));
            }
        }
    }

    private class Ant extends Candidate {

        public Ant(Candidate candidate) {
            super(candidate);
        }

    }

}
