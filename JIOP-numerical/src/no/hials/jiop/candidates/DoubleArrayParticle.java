package no.hials.jiop.candidates;

///*
// * Copyright (c) 2014, Aalesund University College
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * * Redistributions of source code must retain the above copyright notice, this
// *   list of conditions and the following disclaimer.
// * * Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
//package no.hials.jiop.structure.candidates;
//
//import no.hials.jiop.structure.DoubleArrayCandidate;
//
///**
// *
// * @author LarsIvar
// */
//public class DoubleArrayParticle extends DoubleArrayCandidate {
//
//    private DoubleArrayCandidate localBest;
//    private final DoubleArrayCandidate velocity;
//
//    public DoubleArrayParticle(int length) {
//        super(length);
//        this.velocity = new DoubleArrayCandidate(length);
//        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
//    }
//
//    public DoubleArrayParticle(double[] elements) {
//        super(elements);
//        this.velocity = new DoubleArrayCandidate(elements.length);
//        this.velocity.randomize();
//        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
//    }
//
//    public DoubleArrayParticle(double[] elements, double cost) {
//        super(elements, cost);
//        this.velocity = new DoubleArrayCandidate(elements.length);
//        this.velocity.randomize();
//        this.localBest = new DoubleArrayCandidateStructure(getElements(), getCost());
//    }
//
//    public DoubleArrayCandidate getLocalBest() {
//        return localBest;
//    }
//
//    public void setLocalBest(NumericCandidateStructure localBest) {
//        this.localBest = (DoubleArrayCandidateStructure) localBest;
//    }
//
//    public DoubleArrayCandidate getVelocity() {
//        return velocity;
//    }
//
//}
