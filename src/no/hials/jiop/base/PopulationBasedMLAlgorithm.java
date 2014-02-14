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
package no.hials.jiop.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.hials.jiop.base.MLHistory.MLHistory;
import no.hials.jiop.base.candidates.Candidate;
import no.hials.jiop.base.candidates.CandidateContainer;
import no.hials.jiop.base.candidates.encoding.factories.EncodingFactory;
import org.math.plot.Plot2DPanel;

/**
 * Extention of MLAlgorithm with support for populationBased algorithms
 * @author Lars Ivar Hatledal
 */
public abstract class PopulationBasedMLAlgorithm<E> extends MLAlgorithm<E> {

    private final MLHistory avgHistory = new MLHistory();
    private final CandidateContainer<E> container;
    protected final int preferredSize;

    public PopulationBasedMLAlgorithm(int size, EncodingFactory<E> encodingFactory, Evaluator<E> evaluator) {
        super(encodingFactory, evaluator);
        this.preferredSize = size;
        this.container = new CandidateContainer<>(size);
    }

    @Override
    public void reset(List<E> seed, boolean clearHistory) {
        super.reset(seed, clearHistory);
        if (clearHistory) {
            avgHistory.clear();
        }
        List<Candidate<E>> candidates = new ArrayList<>(preferredSize);
        candidates.addAll(getCandidateFactory().toCandidateList(candidates));
        candidates.addAll(getCandidateFactory().getRandomCandidateList(preferredSize - candidates.size()));
        getContainer().clearAndAddAll(candidates);
        setBestCandidate(getContainer().sort().get(0));
    }

    @Override
    public void reset(boolean clearHistory) {
        super.reset(clearHistory);
        if (clearHistory) {
            avgHistory.clear();
        }
        container.clearAndAddAll(getCandidateFactory().getRandomCandidateList(preferredSize));
        setBestCandidate(getContainer().sort().get(0));
    }

    @Override
    public double iteration() {
        double t = super.iteration();
        avgHistory.add(container.getAverage(), t);
        return t;
    }
    
    @Override
    public Plot2DPanel getPlot() {
        Plot2DPanel plot = super.getPlot();
        plot.addLinePlot("", getAvgHistory().getTimestamps(), getAvgHistory().getCosts());
        return plot;
    }
    
    /**
     * Getter for the candidate container
     * @return the candidate container
     */
    public CandidateContainer<E> getContainer() {
        return container;
    }

    /**
     * Getter for the average MLHistory
     * @return the average MLHistory
     */
    public MLHistory getAvgHistory() {
        return avgHistory;
    }

    @Override
    public void dumpHistoryToFile(String dir, String fileName) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        StringBuilder sb = new StringBuilder();
        try (
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "//" + fileName)))) {
            for (int i = 0; i < history.size(); i++) {
                sb.append(history.getIterations()[i]).append("\t").append(history.getTimestamps()[i]).append("\t").append(history.getCosts()[i]).append("\t").append(avgHistory.getCosts()[i]).append("\n");
            }
            bw.write(sb.toString());
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
