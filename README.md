JIOP
======

JIOP is an open-source Java intelligent optimization framework.
It's main purpose is to deliver a easy to use framework for creating and using intelligent optimization algorithms.

Currently, JIOP already contains implementations of:

	* Differential Evolution
	* Particle Swarm Optimization
	* Multi Swarm Optimization
	* Continuous Genetic Algorithm 
	* Artificial Bee Colony
	* Bacterial Foraging Optimization
	* Amoeba Optimization (Nelder-Mead)
	* Simulated Annealing
	
Features
==========

	* Supports generics
	* Supports "seed" insertion. (Populate the solution pool with one or more known solutions)
	* Plotting is handled by the framework. 
	* Easy comparison of different algorithms
	* .CSV export of iteration data (number of iteration, time used and cost)
	* Self optimization of variables (If the algorithm implements the Optimizable interface)
	* Contains multi-threaded version of DE, PSO and MSO
	
Dependencies
============

JFreeChart