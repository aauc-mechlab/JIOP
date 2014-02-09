/*
 * Copyright (c) 2014, LarsIvar
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
package no.hials.jiop.tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.hials.jiop.base.AbstractEvaluator;

/**
 *
 * @author LarsIvar
 */
public class TSPEvaluator extends AbstractEvaluator<double[]> {

//    private final City[] cities;
//    private final NormUtil xNorm, yNorm; 
//    private final NormUtil norm;
    public TSPEvaluator() {
//        this.cities = cities;
//        this.norm = new NormUtil(cities.length, 0);
//        int xMin = 0, xMax = 0, yMin = 0, yMax = 0;
//        for (int i = 0; i < cities.length; i++) {
//            City c = cities[i];
//            int x = c.x;
//            int y = c.y;
//            if (i == 0) {
//                xMin = x;
//                xMax = x;
//                yMin = y;
//                yMax = y;
//            } else {
//                if (x > xMax) {
//                    xMax = x;
//                } else if (x < xMin) {
//                    xMin = x;
//                }
//                 if (y > yMax) {
//                    yMax = y;
//                } else if (y < yMin) {
//                    yMin = y;
//                }
//            }
//        }
//        this.xNorm = new NormUtil(xMax, xMin);
//        this.yNorm = new NormUtil(yMax, yMin);
    }

    @Override
    public double evaluate(double[] variables) {
        
        City[] cities = new City[variables.length];
        for (int i = 0; i < cities.length; i++) {
            cities[i] = TSPMain.cities[TSPMain.decode(variables[i])];
        }
        
        List<City> nogo = new ArrayList<>();
        List<City> nogo2 = new ArrayList<>();

        double cost = 0;
        for (int i = 0; i < variables.length - 1; i++) {
            City city1 = cities[TSPMain.decode(variables[i])];
            City city2 = cities[TSPMain.decode(variables[i + 1])];
            if (city1 == city2) {
                return 1000;
            }
            if (nogo.contains(city1) | nogo2.contains(city2)) {
                return 1000;
            }
            nogo.add(city1);
            nogo2.add(city2);
            final double dist = city1.dist(city2);
            cost += dist;
        }
        if (new ArrayList<>(Arrays.asList(TSPMain.cities)).containsAll(Arrays.asList(cities))) {
            
        } else {
            return 1000;
        }

        return cost;

    }

}
