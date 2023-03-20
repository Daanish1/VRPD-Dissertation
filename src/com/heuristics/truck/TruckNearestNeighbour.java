package com.heuristics.truck;

import com.solutioninstance.Coordinate;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.solutioninstance.SolutionRepresentation;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Random;

//Solution initialisation - Construct truck route via nearest neighbours
public class TruckNearestNeighbour {
    private Random random;
    private ObjectiveFunction objectiveFunction;
    private SolutionInstance instance;

    public TruckNearestNeighbour(Random random, ObjectiveFunction objectiveFunction, SolutionInstance instance) {
        this.random = random;
        this.objectiveFunction = objectiveFunction;
        this.instance = instance;
    }

    //Create a truck route
    public Solution initialiseTruckNearestNeighbourSolution(ArrayList<Integer> nodesToAdd) {

        //Truck Solution - Initially empty
        ArrayList<Integer> truckSolution = new ArrayList<>();

        //Put all delivery nodes into nodesToVisit list
        ArrayList<Integer> nodesToVisit = new ArrayList<>();
        for (int i = 0; i < nodesToAdd.size(); i++) {
            nodesToVisit.add(nodesToAdd.get(i));
        }

        //Starting location
        Coordinate currentNode = instance.getStartNode();

        //Repeatedly insert node to truck route based on closest mode
        while (nodesToVisit.size() > 0) {

            int closest = getClosestNodeTo(currentNode, nodesToVisit, objectiveFunction);
            truckSolution.add(closest);
            nodesToVisit.remove(new Integer(closest));
            currentNode = instance.getNodeForDelivery(closest);

        }


        //Create representation with no drone deliveries
        SolutionRepresentation representation = new SolutionRepresentation(truckSolution, null);

        //Return solution with constructed truck route
        return new Solution(representation, objectiveFunction.calculateObjectiveFunction(representation));

    }

    //Find the closest delivery node to currentNode in list nodesToVisit
    public int getClosestNodeTo(Coordinate currentNode, ArrayList<Integer> nodesToVisit, ObjectiveFunction objectiveFunction) {

        int closestNode;
        double closestTime;


        closestNode = 0;
        closestTime = objectiveFunction.calculateTruckTimeAdjacent(currentNode, instance.getNodeForDelivery(nodesToVisit.get(0)));


        for (int i = 1; i < nodesToVisit.size(); i++) {
            if ( closestTime > objectiveFunction.calculateTruckTimeAdjacent(currentNode, instance.getNodeForDelivery(nodesToVisit.get(i)))) {
                closestNode = i;
                closestTime = objectiveFunction.calculateTruckTimeAdjacent(currentNode, instance.getNodeForDelivery(nodesToVisit.get(i)));
            }
        }

        return nodesToVisit.get(closestNode);


    }
}
