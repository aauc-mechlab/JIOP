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
package no.hials.jiop.base.swing;

import java.awt.Color;
import no.hials.jiop.base.MLMethod;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author LarsIvar
 */
public class MLHistoryPlot extends Plot2DPanel {

    public MLHistoryPlot(MLMethod method) {
        addLegend("INVISIBLE");
        if (method.hasAverage()) {
            addLinePlot("", method.getHistory().getTimestampsD(), method.getHistory().getCosts());
            addLinePlot("", method.getAvgHistory().getTimestampsD(), method.getAvgHistory().getCosts());
        } else {
            addLinePlot("", method.getHistory().getTimestampsD(), method.getHistory().getCosts());
        }
    }
    
     public MLHistoryPlot(MLMethod method, Color ... c) {
        addLegend("INVISIBLE");
        if (method.hasAverage()) {
            addLinePlot("", c[0], method.getHistory().getTimestampsD(), method.getHistory().getCosts());
            addLinePlot("", c[1], method.getAvgHistory().getTimestampsD(), method.getAvgHistory().getCosts());
        } else {
            addLinePlot("", c[0], method.getHistory().getTimestampsD(), method.getHistory().getCosts());
        }
        
    }
    
//    public MLHistoryPlot(MLHistory history, Color c1, MLHistory avgHistory, Color c2) {
//        addLegend("INVISIBLE");
//        addLinePlot("", c1, history.getTimestampsD(), history.getCosts());
//        addLinePlot("", c2,avgHistory.getTimestampsD(), avgHistory.getCosts());
//    }

}
