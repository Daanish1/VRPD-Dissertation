package com.heuristics;

import com.Constants;
import com.solutioninstance.DroneDeliveryRepresentation;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.solutioninstance.SolutionRepresentation;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//2-opt local search applied to VRPD
public class TwoOpt {

    private Random random;
    private ObjectiveFunction objectiveFunction;
    private SolutionInstance instance;

    //How many swap operators are defined

    // 2-opt operators:
    // swap truck nodes = 0
    // swap drone delivery nodes = 1
    //swap truck and drone node = 2
    private int numberOfOperators = 3;


    public TwoOpt(Random random, SolutionInstance instance, ObjectiveFunction objectiveFunction) {
        this.random = random;
        this.instance = instance;
        this.objectiveFunction = objectiveFunction;

    }

    //Apply 2-opt to all truck-drone pairs
    public Solution[] applyHeuristic(Solution[] sol) {

        Solution[] currentSolution = sol;

        for (int i = 0; i < sol.length; i++) {
            currentSolution[i] = twoOptSolution(currentSolution[i]);
        }


        return currentSolution;
    }


    //Apply 2-opt to a single truck-drone pair
    public Solution twoOptSolution(Solution sol) {

        //Keep track of current solution rep and its objective function value
        SolutionRepresentation currentSolutionRep = sol.getRepresentation();
        ArrayList<Integer> currentTruckSolution = currentSolutionRep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation> currentDroneSolution = currentSolutionRep.getDroneRep();
        double currentObjectiveFunctionValue = sol.getObjectiveFunctionValue();

        //Temporary solution rep and its objective function value
        SolutionRepresentation tempSolutionRep = currentSolutionRep.deepCopy();
        ArrayList<Integer> tempTruckSolution = tempSolutionRep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation>  tempDroneSolution = tempSolutionRep.getDroneRep();
        double tempObjectiveFunctionValue = currentObjectiveFunctionValue;

        //Keep track of start time and current time elapsed
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();

        //Keep track of the last accepted solution
        long lastImprovementTime = System.currentTimeMillis();
        int i = 0;

        //While the time elapsed is less than the max run time allowed repeatedly perform 2-opt
        while (currentTime - startTime < Constants.RUN_TIME) {

            //Select a random swap operator
            int operator = random.nextInt(numberOfOperators);


            //Based on selected operator apply it to the solution
            if (operator == 0) {
                tempSolutionRep = twoTruckNodeSwap(tempTruckSolution, tempDroneSolution);
            }
            else if (operator == 1) {
                tempSolutionRep = twoDroneNodeSwap(tempTruckSolution, tempDroneSolution);
            } else if (operator == 2) {
                tempSolutionRep = droneTruckNodeSwap(tempTruckSolution, tempDroneSolution);
            }



            //Calculate objective function of the new solution
            tempObjectiveFunctionValue = objectiveFunction.calculateObjectiveFunction(tempSolutionRep);

            //Choose which solution to keep
            if (tempObjectiveFunctionValue < currentObjectiveFunctionValue) {
                currentSolutionRep = tempSolutionRep.deepCopy();
                currentTruckSolution = currentSolutionRep.getTruckRep();
                currentDroneSolution = currentSolutionRep.getDroneRep();
                currentObjectiveFunctionValue = tempObjectiveFunctionValue;
                lastImprovementTime = System.currentTimeMillis();

            }

            tempSolutionRep = currentSolutionRep.deepCopy();
            tempTruckSolution = tempSolutionRep.getTruckRep();
            tempDroneSolution = tempSolutionRep.getDroneRep();
            tempObjectiveFunctionValue = currentObjectiveFunctionValue;

            currentTime = System.currentTimeMillis();
            i++;


            long timeRunning = currentTime - startTime;
            long startToLastImprovement = lastImprovementTime - startTime;

            //Check for early break out rule
            if ((timeRunning > Constants.RUN_TIME/2)  &&  (startToLastImprovement <= timeRunning/2)) {
                break;
            }

        }


        return new Solution(currentSolutionRep, currentObjectiveFunctionValue);







    }

    //Operator - Swap two truck deliveries
    private SolutionRepresentation twoTruckNodeSwap(ArrayList<Integer> truck, ArrayList<DroneDeliveryRepresentation> drone) {

        //If not enough truck nodes to swap return
        if ((truck.size() < 2) ) {
            return new SolutionRepresentation(truck, drone);
        }

        //Select two random indexes where they are not the same
        int i, j;
        do {
            i = random.nextInt(truck.size());
            j = random.nextInt(truck.size());
        } while(i != j);

        //Swap truck deliveries i and j
        Collections.swap(truck, i, j);

        //Return solution with swapped delivery nodes
        return new SolutionRepresentation(truck, drone);

    }

    //Operator - Swap two drone deliveries
    private SolutionRepresentation twoDroneNodeSwap(ArrayList<Integer> truck, ArrayList<DroneDeliveryRepresentation> drone) {

        //If not enough drone nodes to swap then return
        if ((drone.size() < 2) ) {
            return new SolutionRepresentation(truck, drone);
        }

        //Select two random indexes where they are not the same
        int i, j;

        do {
            i = random.nextInt(drone.size());
            j = random.nextInt(drone.size());
        }while(i != j);

        //Identify the drone deliveries
        DroneDeliveryRepresentation first = drone.get(i);
        DroneDeliveryRepresentation second = drone.get(j);
        int temp = second.getDroneDeliveryNode();

        //Swap the drone delivery nodes
        second.setDroneDeliveryRep(second.getTakeOffNode(), first.getDroneDeliveryNode(), second.getLandingNode());

        first.setDroneDeliveryRep(first.getTakeOffNode(), temp, first.getLandingNode());

        //Return solution with swapped delivery nodes
        return new SolutionRepresentation(truck, drone);


    }


    //Operator - Swap one truck delivery with one drone delivery
    private SolutionRepresentation droneTruckNodeSwap(ArrayList<Integer> truck, ArrayList<DroneDeliveryRepresentation> drone) {

        //Checks if either of the representations are empty, if one is then return
        if ((truck.size() == 0) || (drone.size() == 0) ) {
            return new SolutionRepresentation(truck, drone);
        }

        //Select random indexes from truck and drone deliveries
        int i = random.nextInt(truck.size());
        int j = random.nextInt(drone.size());

        //Get the delivery node
        int truckNode = truck.get(i);
        int droneNode = drone.get(j).getDroneDeliveryNode();

        //Switch take off and landing nodes if necessary
        for (int k = 0; k < drone.size(); k++) {
            DroneDeliveryRepresentation tempD = drone.get(k);
            if (tempD.getTakeOffNode() == truckNode) {
                drone.set(k, new DroneDeliveryRepresentation(droneNode, tempD.getDroneDeliveryNode(), tempD.getLandingNode()));
            }
            if (tempD.getLandingNode() == truckNode) {
                drone.set(k, new DroneDeliveryRepresentation(tempD.getTakeOffNode(), tempD.getDroneDeliveryNode(), droneNode));
            }
        }

        //Swap delivery nodes
        truck.set(i, droneNode);
        drone.set(j, new DroneDeliveryRepresentation(drone.get(j).getTakeOffNode(), truckNode, drone.get(j).getLandingNode()));



        //Return solution with swapped delivery nodes
        return new SolutionRepresentation(truck, drone);
    }





}
