package no.hials.jiop.generic.factories;

//package no.hials.jiop.factories;
//
///*
//* Copyright (c) 2014, Aalesund University College
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
//package no.hials.jiop.candidates.factories;
//
//import java.util.ArrayList;
//import java.util.List;
//import no.hials.jiop.candidates.NumericCandidate;
//
///**
// *
// * @author LarsIvar
// */
//public abstract class AbstractNumericCandidateFactory<E>  implements NumericCandidateFactory<E>{
//
//    @Override
//    public List<NumericCandidate<E>> generatePopulation(int size, int dimension) {
//        List<NumericCandidate<E>> population = new ArrayList<>(size);
//        for (int i = 0; i < size; i++) {
//            population.add(generateRandom(dimension));
//        }
//        return population;
//    }
//
//    @Override
//    public List<NumericCandidate<E>> generatePopulation(int size, int dimension, List<E> seed) {
//        if (seed == null) {
//            return generatePopulation(size, dimension);
//        } else if (seed.size() > size) {
//            throw new IllegalArgumentException();
//        } else {
//            List<NumericCandidate<E>> population = new ArrayList<>(size);
//            for (int i = 0; i < seed.size(); i++) {
//                population.add(generateFromElements(seed.get(i)));
//            }
//            for (int i = seed.size(); i < size; i++) {
//                population.add(generateRandom(dimension));
//            }
//            return population;
//        }
//    }
//
//    @Override
//    public abstract NumericCandidate<E> generateFromElements(E e);
//
//    @Override
//    public abstract NumericCandidate<E> generateRandom(int dimension);
//
//}
