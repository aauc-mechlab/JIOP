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
package no.hials.jiop.base.MLHistory;

import java.util.ArrayList;
import no.hials.jiop.base.MLHistory.MLHistory.MLHistoryPoint;

/**
 * Class to store the performance history of an algorithm
 * @author Lars Ivar Hatledal
 */
public class MLHistory extends ArrayList<MLHistoryPoint> {
    
    private double tOffset = 0;
    private int iteration = 0;
    
    /**
     * Adds a new element to the list
     * @param cost the cost
     * @param timestamp the time it took to get the solution
     */
    public void add (double cost, double timestamp) {
        add(new MLHistoryPoint(cost, iteration++, timestamp + tOffset));
        tOffset += timestamp;
    }

    @Override
    public void clear() {
        super.clear(); 
        tOffset  = 0;
        iteration = 0;
    }
    
    public double[] getTimestamps() {
        double[] stamps = new double[size()];
        for (int i = 0; i < size(); i++) {
            stamps[i] = get(i).timestamp;
        }
        return stamps;
    }
    
    public double[] getCosts() {
        double[] costs = new double[size()];
        for (int i = 0; i < size(); i++) {
            costs[i] = get(i).cost;
        }
        return costs;
    }
    
     public int[] getIterations() {
        int[] iterations = new int[size()];
        for (int i = 0; i < size(); i++) {
            iterations[i] = get(i).iteration;
        }
        return iterations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i).toString());
            if (i != size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    protected class MLHistoryPoint {

    public final double cost;
    public final int iteration;
    public final double timestamp;

        public MLHistoryPoint(double cost, int iteration, double timestamp) {
            this.cost = cost;
            this.iteration = iteration;
            this.timestamp = timestamp;
        }

    @Override
    public String toString() {
        return "MLHistoryPoint{" + "cost=" + cost + ", timestamp=" + timestamp + '}';
    }

}
    
}
