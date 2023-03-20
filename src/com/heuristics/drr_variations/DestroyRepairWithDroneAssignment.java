package com.heuristics.drr_variations;

import com.Constants;
import com.solutioninstance.*;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Random;

//Destroy-repair with exact drone assignment (DRR_DA)
public class DestroyRepairWithDroneAssignment {

    private Random random;
    private ObjectiveFunction objectiveFunction;
    private SolutionInstance instance;



    public DestroyRepairWithDroneAssignment(Random random, SolutionInstance instance, ObjectiveFunction objectiveFunction) {
        this.random = random;
        this.instance = instance;
        this.objectiveFunction = objectiveFunction;

    }

    //Apply DRR_DA to all truck-drone pairs
    public Solution[] applyHeuristic(Solution[] sol) {

        Solution[] currentSolution = sol;

        for (int i = 0; i < sol.length; i++) {
            currentSolution[i] = destroyRepairSolution(currentSolution[i]);
        }

        return currentSolution;
    }

    //Apply DRR_DA to a single truck-drone pair
    private Solution destroyRepairSolution(Solution sol) {



        //Keep track of best solution rep and its objective function value
        SolutionRepresentation bestSolutionRep = sol.getRepresentation();
        ArrayList<Integer> bestTruckRep = bestSolutionRep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation> bestDroneRep = bestSolutionRep.getDroneRep();
        double bestObjectiveFunctionValue = sol.getObjectiveFunctionValue();


        //Keep track of current solution rep and its objective function value
        SolutionRepresentation currentSolutionRep = bestSolutionRep.deepCopy();
        ArrayList<Integer> currentTruckRep = currentSolutionRep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation> currentDroneRep = currentSolutionRep.getDroneRep();
        double currentObjectiveFunctionValue = bestObjectiveFunctionValue;


        //Temporary solution rep and its objective function value
        SolutionRepresentation tempSolutionRep = currentSolutionRep.deepCopy();
        ArrayList<Integer> tempTruckRep = tempSolutionRep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation> tempDroneRep = tempSolutionRep.getDroneRep();
        double tempObjectiveFunctionValue;

        //Keep track of start time and current time elapsed
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();

        //Keep track of the last accepted solution
        long lastImprovementTime = System.currentTimeMillis();
        int i = 0;

        //While the time elapsed is less than the max run time allowed repeatedly perform DRR_DA
        while (currentTime - startTime < Constants.RUN_TIME) {

            //If the truck route is empty break out of loop
            if (tempTruckRep.size() == 0) {
                break;
            }

            //Initialise reinsertion list to keep track of removed delivery nodes
            ArrayList<Integer> reinsertionList = new ArrayList<>();



            //Select range to destroy
            int destroyRange = tempTruckRep.size();

            int start, end;

            end = random.nextInt(destroyRange);

            //Find random segment in the truck route
            int offsetRange = tempTruckRep.size() - end;
            int offsetValue = random.nextInt(offsetRange);

            start = 0 + offsetValue;
            end += offsetValue;
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }

            //ROUTE DESTROY SECTION

            //Delete temp truck route between start and end
            for (int j = end; j >= start; j--) {
                int valueToRemove = tempTruckRep.get(j);
                reinsertionList.add(valueToRemove);

                tempTruckRep.remove(j);
            }


            //Delete drone nodes in this range
            ArrayList<Integer> removedDroneNodes = new ArrayList<>();
            for (int j = 0; j < reinsertionList.size(); j++) {

                int current = reinsertionList.get(j);

                //Check for current in drone representation
                //Need to check this twice as two drone deliveries can be associated with one truck delivery
                int droneDeliveryNodes[] = isCurrentInDroneRep(current, tempDroneRep);


                for (int k = 0; k < droneDeliveryNodes.length; k++) {
                    if (droneDeliveryNodes[k] != -1) {
                        removedDroneNodes.add(droneDeliveryNodes[k]);
                    }
                }
            }

            //Add removedDroneNodes to Reinsertion list
            for (int j = 0; j < removedDroneNodes.size(); j++) {
                reinsertionList.add(removedDroneNodes.get(j));
            }

            //Set temp solution to the edited truck and drone rep with the destroy segment removed
            tempSolutionRep.setTruckRep(tempTruckRep);
            tempSolutionRep.setDroneRep(tempDroneRep);

            //Calculate tempSolution objective function
            tempObjectiveFunctionValue = objectiveFunction.calculateObjectiveFunction(tempSolutionRep);

            //Initialise a truck and drone reinsertion list
            ArrayList<Integer> truckReinsertionList = new ArrayList<>();
            ArrayList<Integer> droneReinsertionList = new ArrayList<>();


            //Calculate maximum number of drone deliveries
            int numberOfPossibleDrones = (int) Math.round(reinsertionList.size() / 2.0);

            //Add the maximum number of possible drone deliveries to the drone reinsertion list
            for (int j = 0; j < numberOfPossibleDrones; j++) {

                int index = random.nextInt(reinsertionList.size());
                droneReinsertionList.add(reinsertionList.get(index));
                reinsertionList.remove(index);

            }

            //Add rest of reinsertion to truck reinsertion list
            for (int j = 0; j < reinsertionList.size(); j++) {
                truckReinsertionList.add(reinsertionList.get(j));
            }


            //ROUTE REPAIR SECTION

            //Iterate through truck reinsertion list and add each element as a truck node in the location that increased the objective function
            //of temp solution by the smallest amount (Greedy Insertion)

            for (int j = 0; j < truckReinsertionList.size(); j++) {

                //Node chosen for insertion
                int nodeToInsert = truckReinsertionList.get(j);

                //Keep track of best location as well as the lowest increase in objective function
                double lowestIncrease = -1;
                int lowestIndex = -1;

                //Iterate through temp solution looking for the best location to insert nodeToInsert
                for (int k = 0; k < tempTruckRep.size(); k++) {

                    //Add nodeToInsert at location k
                    tempTruckRep.add(k, nodeToInsert);

                    double checkObjectiveFunctionValue = objectiveFunction.calculateObjectiveFunction(new SolutionRepresentation(tempTruckRep, tempDroneRep));

                    double objectiveFunctionValueIncrease = checkObjectiveFunctionValue - tempObjectiveFunctionValue;

                    if ((lowestIncrease == -1 ) || (objectiveFunctionValueIncrease < lowestIncrease)) {
                        lowestIncrease = objectiveFunctionValueIncrease;
                        lowestIndex = k;
                    }

                    //Remove nodeToInsert from location k
                    tempTruckRep.remove(k);


                }




                if (tempTruckRep.size() == 0) {

                    //Add nodeToInsert at the index that increased the objective function the least
                    tempTruckRep.add(nodeToInsert);

                    //Update tempSolution objective function value
                    tempObjectiveFunctionValue += objectiveFunction.calculateObjectiveFunction(new SolutionRepresentation(tempTruckRep, tempDroneRep));
                } else {

                    //Add nodeToInsert at the index that increased the objective function the least
                    tempTruckRep.add(lowestIndex, nodeToInsert);

                    //Update tempSolution objective function value
                    tempObjectiveFunctionValue += lowestIncrease;
                }



            }

            //Add rest of drones to drone reinsertion list
            while(tempDroneRep.size() > 0) {
                droneReinsertionList.add(tempDroneRep.get(0).getDroneDeliveryNode());
                tempDroneRep.remove(0);
            }


            //Drone Assignment via Hungarian Algorithm:
            //Construct Matrix:
            double[][] dataMatrix = new double[tempTruckRep.size()+1][droneReinsertionList.size()];

            //Calculate time for each drone assignment
            for (int truckIndex = 0; truckIndex < dataMatrix.length; truckIndex++) {
                for (int droneIndex = 0; droneIndex < droneReinsertionList.size(); droneIndex++) {
                    if (truckIndex == 0) {
                        dataMatrix[truckIndex][droneIndex] = objectiveFunction.calculateDroneTime(new DroneDeliveryRepresentation(-1, droneReinsertionList.get(droneIndex), tempTruckRep.get(truckIndex)));
                    } else if (truckIndex == tempTruckRep.size()) {
                        dataMatrix[truckIndex][droneIndex] = objectiveFunction.calculateDroneTime(new DroneDeliveryRepresentation(tempTruckRep.get(truckIndex-1), droneReinsertionList.get(droneIndex), -1));
                    } else {
                        dataMatrix[truckIndex][droneIndex] = objectiveFunction.calculateDroneTime(new DroneDeliveryRepresentation(tempTruckRep.get(truckIndex-1), droneReinsertionList.get(droneIndex), tempTruckRep.get(truckIndex)));
                    }
                }
            }

            //Call Hungarian Algorithm Code:
            HungarianAlgorithm hg = new HungarianAlgorithm(dataMatrix);
            int[] assignment = hg.execute();


            //Create the drone routes (i, j, k) and add them to the tempDroneRep
            for (int index = 0; index < assignment.length; index++) {
                if (assignment[index] == -1)
                    continue;
                DroneDeliveryRepresentation newDrone;

                if (index == 0) {
                    newDrone = new DroneDeliveryRepresentation(-1, droneReinsertionList.get(assignment[index]), tempTruckRep.get(index));
                } else if (index == assignment.length-1) {
                    newDrone = new DroneDeliveryRepresentation(tempTruckRep.get(index-1), droneReinsertionList.get(assignment[index]), -1);
                } else {
                    newDrone = new DroneDeliveryRepresentation(tempTruckRep.get(index-1), droneReinsertionList.get(assignment[index]), tempTruckRep.get(index));
                }

                tempDroneRep.add(newDrone);
            }


            //Set temp solution to the edited truck and drone rep with the inserted drone nodes
            tempSolutionRep.setTruckRep(tempTruckRep);
            tempSolutionRep.setDroneRep(tempDroneRep);

            //Calculate tempSolution objective function
            tempObjectiveFunctionValue = objectiveFunction.calculateObjectiveFunction(tempSolutionRep);


            //Choose which solution to keep
            if (tempObjectiveFunctionValue < currentObjectiveFunctionValue) {
                currentSolutionRep = tempSolutionRep.deepCopy();
                currentTruckRep = currentSolutionRep.getTruckRep();
                currentDroneRep = currentSolutionRep.getDroneRep();
                currentObjectiveFunctionValue = tempObjectiveFunctionValue;
                lastImprovementTime = System.currentTimeMillis();


            } else {
                SolutionRepresentation newRep =  currentSolutionRep.deepCopy();
                tempTruckRep = newRep.getTruckRep();
                tempDroneRep = newRep.getDroneRep();

            }

            currentTime = System.currentTimeMillis();
            i++;

            long timeRunning = currentTime - startTime;
            long startToLastImprovement = lastImprovementTime - startTime;

            //Check for early break out rule
            if ((timeRunning > Constants.RUN_TIME/2)  &&  (startToLastImprovement <= timeRunning/2)) {
                break;
            }


        }

        //Choose best solution to return
        if (currentObjectiveFunctionValue <= bestObjectiveFunctionValue) {
            return new Solution(currentSolutionRep, currentObjectiveFunctionValue);
        } else {
            return new Solution(bestSolutionRep, bestObjectiveFunctionValue);
        }

    }

    //Checks to see if a given node is a take off location for a drone delivery
    private boolean isNodeTakeOff(int node, ArrayList<DroneDeliveryRepresentation> droneRep) {

        for (int i = 0; i < droneRep.size(); i++) {
            if (droneRep.get(i).getTakeOffNode() == node) {
                return true;
            }
        }
        return false;
    }

    //Checks to see if a given node is a landing location for a drone delivery
    private boolean isNodeLanding(int node, ArrayList<DroneDeliveryRepresentation> droneRep) {
        for (int i = 0; i < droneRep.size(); i++) {
            if (droneRep.get(i).getLandingNode() == node) {
                return true;
            }
        }
        return false;
    }

    //Identify which drone delivery nodes are associated with current node
    private int[] isCurrentInDroneRep(int current, ArrayList<DroneDeliveryRepresentation> droneRep) {
        int droneDeliveryNodes[] = {-1, -1};
        int index = 0;

        for (int i = 0; i < droneRep.size(); i++) {
            DroneDeliveryRepresentation delivery = droneRep.get(i);

            //If current is the drone delivery take off or landing node set droneDeliveryNode[i] to the drone delivery node
            if ((delivery.getTakeOffNode() == current) || (delivery.getLandingNode() == current)) {
                droneDeliveryNodes[index++] = delivery.getDroneDeliveryNode();

                droneRep.remove(i);
                i--;

            }
        }
        return droneDeliveryNodes;
    }
}
