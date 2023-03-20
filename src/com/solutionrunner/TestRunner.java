package com.solutionrunner;

import com.heuristics.KMeansClustering;
import com.heuristics.TwoOpt;
import com.heuristics.drr_variations.DestroyRepairFixedRange;
import com.heuristics.drr_variations.DestroyRepairRoute;
import com.heuristics.drr_variations.DestroyRepairWithDroneAssignment;
import com.heuristics.drr_variations.DestroyRepairWithRangeReduction;
import com.reader.InstanceReader;
import com.solutioninstance.ObjectiveFunction;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.visualiser.SolutionWindow;

import java.util.Random;

public class TestRunner {

    private Random random;
    private String instanceFile;
    private SolutionInstance instance;
    private Solution solution[];
    private ObjectiveFunction objectiveFunction;
    private String algorithm;
    private int seed;


    public TestRunner(int seed, String instanceFile, String algorithm) {
        this.seed = seed;
        this.random = new Random(seed);
        this.instanceFile = instanceFile;
        this.algorithm = algorithm;
    }

    public void loadInstance() {
        //Read instance from text file
        InstanceReader myInstanceReader = new InstanceReader(this.instanceFile, this.random);
        this.instance = myInstanceReader.read();

        //Initialise the objective function
        objectiveFunction = new ObjectiveFunction(this.instance);


    }

    public void run() {

        //k-means clustering. produce array of SolutionRepresentation objects. and apply TNN and DGI to all of them for initialisation.

        KMeansClustering KMC = new KMeansClustering(random, objectiveFunction, instance);

        //this.solution = KMC.cluster(k);
        this.solution = KMC.selectClusters(-1);



        //Select why algorithm to run
        if (this.algorithm == "2-OPT") {
            TwoOpt twoOpt = new TwoOpt(this.random, this.instance, this.objectiveFunction);
            this.solution = twoOpt.applyHeuristic(this.solution);

        } else if (this.algorithm == "DRR") {

            DestroyRepairRoute DRR = new DestroyRepairRoute(this.random, this.instance, this.objectiveFunction);
            this.solution = DRR.applyHeuristic(this.solution);

        } else if (this.algorithm == "DRR_FR") {
            DestroyRepairFixedRange DRR_FR = new DestroyRepairFixedRange(this.random, this.instance, this.objectiveFunction);
            this.solution = DRR_FR.applyHeuristic(this.solution);

        } else if (this.algorithm == "DRR_DA") {
            DestroyRepairWithDroneAssignment DRRDA = new DestroyRepairWithDroneAssignment(this.random, this.instance, this.objectiveFunction);
            this.solution = DRRDA.applyHeuristic(this.solution);

        } else if (this.algorithm == "DRR_RR") {
            DestroyRepairWithRangeReduction DRRR = new DestroyRepairWithRangeReduction(this.random, this.instance, this.objectiveFunction);
            this.solution = DRRR.applyHeuristic(this.solution);
        } else {
            System.out.println("NO SELECTED ALGORITHM");
            System.exit(-1);
        }


        double sumObj = 0;
        for (int i = 0; i < this.solution.length; i++) {
            sumObj += solution[i].getObjectiveFunctionValue();
        }


        System.out.println("Algorithm: " + this.algorithm);
        System.out.println("Objective Function Value = " + sumObj);
        System.out.println("Number of Clusters = " + this.solution.length);
        System.out.println("Seed = " + this.seed);
        System.out.println("=====");




//        for (int i = 0; i < this.solution.length; i++){
//            System.out.println("Object Function Cluster " + i + " = " + solution[i].getObjectiveFunctionValue() + " units of time");
//        }


    }

    public void displaySolution() {
        SolutionWindow window = new SolutionWindow(this.solution, this.instance, this.instanceFile + " | " + this.algorithm);
        window.setVisible(true);
    }



}
