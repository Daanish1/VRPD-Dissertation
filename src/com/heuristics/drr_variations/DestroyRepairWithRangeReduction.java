package com.heuristics.drr_variations;

import com.Constants;
import com.solutioninstance.DroneDeliveryRepresentation;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.solutioninstance.SolutionRepresentation;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Random;

//DestroyRepair with range reduction over time (DRR_RR)
public class DestroyRepairWithRangeReduction {


    private Random random;
    private ObjectiveFunction objectiveFunction;
    private SolutionInstance instance;



    public DestroyRepairWithRangeReduction(Random random, SolutionInstance instance, ObjectiveFunction objectiveFunction) {
        this.random = random;
        this.instance = instance;
        this.objectiveFunction = objectiveFunction;

    }

    //Apply DRR_RR to all truck-drone pairs
    public Solution[] applyHeuristic(Solution[] sol) {

        Solution[] currentSolution = sol;

        for (int i = 0; i < sol.length; i++) {
            currentSolution[i] = destroyRepairSolution(currentSolution[i]);
        }


        return currentSolution;
    }

    //Apply DRR_RR to a single truck-drone pair
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

        //While the time elapsed is less than the max run time allowed repeatedly perform DRR_RR
        while (currentTime - startTime < Constants.RUN_TIME) {

            //Initialise reinsertion list to keep track of removed delivery nodes
            ArrayList<Integer> reinsertionList = new ArrayList<>();

            //Select range to destroy
            double destroyPercentage = 1 - ((currentTime - startTime) / Constants.RUN_TIME);

            int destroyRange = (int) Math.round(tempTruckRep.size() * destroyPercentage);

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


            //ROUTE REPAIR SECTION

            //Iterate through reinsertion list and add each element as a truck node in the location that increased the objective function
            //of temp solution by the smallest amount (Greedy Insertion)

            for (int j = 0; j < reinsertionList.size(); j++) {

                //Node chosen for insertion
                int nodeToInsert = reinsertionList.get(j);

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

                if (lowestIndex == -1) {
                    //Add nodeToInsert at the index that increased the objective function the least
                    tempTruckRep.add(nodeToInsert);

                    tempObjectiveFunctionValue += objectiveFunction.calculateObjectiveFunction(new SolutionRepresentation(tempTruckRep, tempDroneRep));
                } else {
                    //Add nodeToInsert at the index that increased the objective function the least
                    tempTruckRep.add(lowestIndex, nodeToInsert);

                    //Update tempSolution objective function value
                    tempObjectiveFunctionValue += lowestIncrease;
                }

            }

            //Identify nodes that can be accepted as drone delivery nodes
            ArrayList<Integer> validDroneNodes = retrieveValidDroneNodes(tempTruckRep, tempDroneRep);

            //Replace truck delivery nodes with drone delivery nodes where possible
            while (validDroneNodes.size() > 0) {

                //Choose random node from validDroneNodes
                int nodeToInsertAsDrone = validDroneNodes.get(this.random.nextInt(validDroneNodes.size()));

                //Find this node index in the truck route:
                int truckIndexOfNewDroneNode = tempTruckRep.indexOf(nodeToInsertAsDrone);

                //Error
                if(truckIndexOfNewDroneNode < 0) {
                    break;
                }

                int takeOffNode;
                int landingNode;

                //Assign the takeOffNode as node before nodeToInsertAsDrone
                if (truckIndexOfNewDroneNode> 0) {
                    takeOffNode = tempTruckRep.get(truckIndexOfNewDroneNode - 1);
                } else {
                    takeOffNode = -1;
                }

                if (truckIndexOfNewDroneNode < tempTruckRep.size()-1) {
                    landingNode = tempTruckRep.get(truckIndexOfNewDroneNode + 1);
                } else {
                    landingNode = -1;
                }

                //Create new drone delivery tuple
                DroneDeliveryRepresentation newDroneDelivery = new DroneDeliveryRepresentation(takeOffNode, tempTruckRep.get(truckIndexOfNewDroneNode), landingNode);

                //Remove new drone node from the truck route
                tempTruckRep.remove(truckIndexOfNewDroneNode);

                //Add newDroneDelivery to the drone representation
                tempDroneRep.add(newDroneDelivery);

                //Retrieve the new set of valid drone nodes in the truck route
                validDroneNodes = retrieveValidDroneNodes(tempTruckRep, tempDroneRep);


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


    //Looks through a truck route and identifies every delivery node in it that can be converted into a drone delivery node
    private ArrayList<Integer> retrieveValidDroneNodes(ArrayList<Integer> truckRep, ArrayList<DroneDeliveryRepresentation> droneRep) {

        //List to hold all nodes valid for drone delivery
        ArrayList<Integer> validDroneNodes = new ArrayList<>();

        //Identify if drone is on the truck
        boolean isDroneOnTruck = true;

        isDroneOnTruck = !isNodeTakeOff(-1, droneRep);

        for (int i = 0; i < truckRep.size(); i++) {

            int currentNode = truckRep.get(i);

            //Check if drone is on truck
            if (isNodeTakeOff(currentNode, droneRep)) {
                isDroneOnTruck = false;
            } else {
                if (isNodeLanding(currentNode, droneRep)) {
                    isDroneOnTruck = true;
                }
            }

            //Deduce whether this is a valid drone node
            if ((isDroneOnTruck) && (!isNodeTakeOff(currentNode, droneRep) && (!isNodeLanding(currentNode, droneRep)))) {
                validDroneNodes.add(currentNode);
            }
        }

        return validDroneNodes;
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
