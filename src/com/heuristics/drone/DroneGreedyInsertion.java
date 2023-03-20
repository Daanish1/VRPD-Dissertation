package com.heuristics.drone;

import com.solutioninstance.*;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Random;

//Part of the solution initialisation. This class inserts drones into a truck route via Greedy Insertion method
public class DroneGreedyInsertion {

    //Number of times to insert drones
    int ITERATIONS = 50;

    private Random random;
    private ObjectiveFunction objectiveFunction;

    //Problem instance
    private SolutionInstance instance;

    public DroneGreedyInsertion(Random random, ObjectiveFunction objectiveFunction, SolutionInstance instance) {
        this.random = random;
        this.objectiveFunction = objectiveFunction;
        this.instance = instance;
    }



    //Looks through a truck route and identifies every delivery node in it that can be converted into a drone delivery node
    public ArrayList<Integer> retrieveValidDroneNodes(ArrayList<Integer> truck, ArrayList<DroneDeliveryRepresentation> drone) {

        //List to hold all nodes valid for drone delivery
        ArrayList<Integer> validDroneNodes = new ArrayList<Integer>();

        //If there are no drone deliveries that means all truck delivery nodes are valid drone delivery nodes
        if (drone.size() == 0) {
            return truck;
        }

        //Loop through all truck nodes
        for (int i = 0; i < truck.size(); i++) {
            boolean isNodeValid = true;
            int currentTruck = truck.get(i);
            //Looks through drone delivery nodes and checks if the currentTruck delivery node is a valid drone delivery node
            for (int j = 0; j < drone.size(); j++) {
                DroneDeliveryRepresentation droneRoute = drone.get(j);
                if ((currentTruck == droneRoute.getTakeOffNode()) || (currentTruck == droneRoute.getDroneDeliveryNode()) || (currentTruck == droneRoute.getLandingNode())) {
                    isNodeValid = false;
                    break;
                }
            }
            //If the check proves currentTruck is a valid drone delivery node add it to validDroneNodes list
            if (isNodeValid == true) {
                validDroneNodes.add(currentTruck);
            }
        }


        return validDroneNodes;

    }

    //Method to insert drones into a given truck route
    public Solution insertDroneNodes(SolutionRepresentation rep) {

        //Retrieve truck route from solution representation
        ArrayList<Integer> truck = rep.getTruckRep();

        //Create empty drone representation to store new drone deliveries
        ArrayList<DroneDeliveryRepresentation> drone = new ArrayList<>();

        //Repeat drone insertion for a set number of times
        for (int x = 0; x < ITERATIONS; x++) {
            //Retrieve list of nodes valid to be converted to a drone node
            ArrayList<Integer> validDroneNodes = retrieveValidDroneNodes(truck, drone);


            int conversionNodeBefore = 0;
            int conversionNodeAfter = 0;
            int conversionNode = 0;
            double longestSegmentValue = 0;

            //Find the longest 3-node truck segment with a valid drone delivery node as the centre of the three
            for (int i = 0; i < truck.size(); i++) {
                //Check if valid drone node
                if (validDroneNodes.contains(truck.get(i))) {

                    //Set nodeBefore to the node before the valid drone node selected
                    int nodeBefore;
                    if (i == 0) {
                        nodeBefore = -1;
                    } else {
                        nodeBefore = truck.get(i-1);
                    }

                    //Set nodeAfter to the node after the valid drone node selected
                    int nodeAfter;
                    if (i == truck.size() - 1) {
                        nodeAfter = -1;
                    } else {
                        nodeAfter = truck.get(i+1);
                    }

                    int currentNode = truck.get(i);


                    //Identify the coordinate of node Before
                    Coordinate coordNodeBefore;
                    if (nodeBefore >= 0) {
                        coordNodeBefore = instance.getNodeForDelivery(nodeBefore);
                    } else {
                        coordNodeBefore = instance.getStartNode();
                    }

                    //Identify currentNode coordinate
                    Coordinate coordCurrentNode = instance.getNodeForDelivery(currentNode);

                    //Identify nodeAfter coordinate
                    Coordinate coordNodeAfter;
                    if (nodeAfter >= 0) {
                        coordNodeAfter = instance.getNodeForDelivery(nodeAfter);
                    } else {
                        coordNodeAfter = instance.getStartNode();
                    }

                    //Calculate the time taken to traverse the three node segment
                    double segmentValue = objectiveFunction.calculateTruckTimeAdjacent(coordNodeBefore, coordCurrentNode) +
                            objectiveFunction.calculateTruckTimeAdjacent(coordCurrentNode, coordNodeAfter);


                    //Keep track of the longest 3-node segment
                    if (segmentValue > longestSegmentValue) {
                        conversionNode = currentNode;
                        conversionNodeBefore = nodeBefore;
                        conversionNodeAfter = nodeAfter;

                        longestSegmentValue = segmentValue;
                    }
                }
            }

            //If there is a valid drone node then add that noted longest segment as a drone delivery
            if (validDroneNodes.size() > 0) {
                //convert it to drone node
                drone.add(new DroneDeliveryRepresentation(conversionNodeBefore, conversionNode, conversionNodeAfter));
                //remove it from truck route
                truck.remove(new Integer(conversionNode));
            } else { // Break out the loop if validDroneNodes is empty as there are no more valid drone deliveries
                break;
            }

        }


        //Construct a solution representation with the new truck route and the new addition of drone deliveries
        SolutionRepresentation representation = new SolutionRepresentation(truck, drone);


        //Return solution object
        return new Solution(representation, objectiveFunction.calculateObjectiveFunction(representation));
    }
}
