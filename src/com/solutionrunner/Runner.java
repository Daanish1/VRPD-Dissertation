package com.solutionrunner;

import com.heuristics.KMeansClustering;

import com.heuristics.drr_variations.DestroyRepairRoute;
import com.heuristics.drr_variations.DestroyRepairWithDroneAssignment;
import com.reader.InstanceReader;
import com.solutioninstance.ObjectiveFunction;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.visualiser.SolutionWindow;

import java.util.Random;

public class Runner {

    private Random random;
    private String instanceFile;
    private SolutionInstance instance;
    private Solution solution[];
    private ObjectiveFunction objectiveFunction;


    public Runner(int seed, String instanceFile) {
        this.random = new Random(seed);
        this.instanceFile = instanceFile;
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


        for (int i = 0; i < this.solution.length; i++) {

            System.out.println("Truck Representation, Tour " + i);
            for (int j = 0; j < this.solution[i].getRepresentation().getTruckRep().size(); j++) {
                System.out.print(this.solution[i].getRepresentation().getTruckRep().get(j) + ", ");
            }
            System.out.println();

            System.out.println("Drone Representation, Tour " + i);
            for (int j = 0; j < this.solution[i].getRepresentation().getDroneRep().size(); j++) {
                System.out.print(this.solution[i].getRepresentation().getDroneRep().get(j).toString() + ", ");
            }
            System.out.println();

        }


        System.out.println("Running...");

//        //2-opt
//        TwoOpt twoOpt = new TwoOpt(this.random, this.instance, this.objectiveFunction);
//        this.solution = twoOpt.applyHeuristic(this.solution);

//        DestroyRepairWithDroneAssignment DRRDA = new DestroyRepairWithDroneAssignment(this.random, this.instance, this.objectiveFunction);
//        this.solution = DRRDA.applyHeuristic(this.solution);


        DestroyRepairWithDroneAssignment DRRDA = new DestroyRepairWithDroneAssignment(this.random, this.instance, this.objectiveFunction);

        this.solution = DRRDA.applyHeuristic(this.solution);

//        DestroyRepairWithDroneAssignment DRRDA = new DestroyRepairWithDroneAssignment(this.random, this.instance, this.objectiveFunction);
//        this.solution = DRRDA.applyHeuristic(this.solution);



//
//        DestroyRepairRoute DRR = new DestroyRepairRoute(this.random, this.instance, this.objectiveFunction);
//
//        this.solution = DRR.applyHeuristic(this.solution);

//        DestroyRepairFixedRange DRR_FR = new DestroyRepairFixedRange(this.random, this.instance, this.objectiveFunction);
//        this.solution = DRR_FR.applyHeuristic(this.solution);


//        DestroyRepairWithRangeReduction DRRR = new DestroyRepairWithRangeReduction(this.random, this.instance, this.objectiveFunction);
//        this.solution = DRRR.applyHeuristic(this.solution);


        for (int i = 0; i < this.solution.length; i++){
            System.out.println("Object Function Cluster " + i + " = " + solution[i].getObjectiveFunctionValue() + " units of time");
        }


    }

    public void displaySolution() {
        SolutionWindow window = new SolutionWindow(this.solution, this.instance, this.instanceFile);
        window.setVisible(true);
    }


}
