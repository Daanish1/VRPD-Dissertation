package com.solutioninstance;

import com.solutioninstance.Coordinate;
import com.solutioninstance.DroneDeliveryRepresentation;
import com.solutioninstance.SolutionInstance;
import com.solutioninstance.SolutionRepresentation;

import java.util.ArrayList;

//Used to calculate the total time of deliveries
public class ObjectiveFunction {

    private final SolutionInstance instance;

    //Truck and drone speed
    public final int truckSpeed = 40;
    public final int droneSpeed = 70;

    public ObjectiveFunction(SolutionInstance instance) {
        this.instance = instance;
    }

    //Calculates time of a solution representation
    public double calculateObjectiveFunction(SolutionRepresentation rep) {

        //Track time elapsed
        double timeElapsed = 0.0;
        ArrayList<Integer> truckRep = rep.getTruckRep();
        ArrayList<DroneDeliveryRepresentation> droneRep = rep.getDroneRep();


        if (truckRep.size() == 0) {
            return 0.0;
        }

        Coordinate firstNode = instance.getStartNode();
        Coordinate nextNode = instance.getNodeForDelivery(truckRep.get(0));

        //Add on time from start to 0
        timeElapsed += calculateTruckTimeAdjacent(firstNode, nextNode);

        for (int i = 0; i < truckRep.size()-1; i++) {
            firstNode = instance.getNodeForDelivery(truckRep.get(i));
            nextNode = instance.getNodeForDelivery(truckRep.get(i+1));


            timeElapsed += calculateTruckTimeAdjacent(firstNode, nextNode);
        }


        firstNode = nextNode;
        nextNode = instance.getStartNode();


        //add on time from end to start node
        timeElapsed += calculateTruckTimeAdjacent(firstNode, nextNode);



        //loop through drone nodes and add on drone time
        if (droneRep != null) {
            for (int i = 0; i < droneRep.size(); i++) {
                DroneDeliveryRepresentation drDelivery = droneRep.get(i);

                double droneTime = calculateDroneTime(drDelivery);
                double truckTime = calculateTruckTimeSegment(truckRep, drDelivery.getTakeOffNode(), drDelivery.getLandingNode());


                if (droneTime > truckTime) {
                    timeElapsed = timeElapsed - truckTime + droneTime;
                }

            }
        }





        return timeElapsed;
    }

    //Calculate the truck time from start to end
    public double calculateTruckTimeSegment(ArrayList<Integer> truckRep, int start, int end) {
        double segmentTime = 0.0;

        int indexStart = truckRep.indexOf(start);
        int indexEnd = truckRep.indexOf(end);


        if (start == -1) {
            segmentTime += calculateTruckTimeAdjacent(instance.getStartNode(), instance.getNodeForDelivery(truckRep.get(0)));

            indexStart = 0;
        }

        if (end == -1) {
            segmentTime += calculateTruckTimeAdjacent(instance.getNodeForDelivery(truckRep.get(truckRep.size()-1)), instance.getStartNode());

            indexEnd = truckRep.size()-1;
        }


        while(indexStart < indexEnd) {
            if (indexStart == -1) {
                segmentTime += calculateTruckTimeAdjacent(instance.getStartNode(), instance.getNodeForDelivery(truckRep.get(indexStart+1)));
            } else {
                segmentTime += calculateTruckTimeAdjacent(instance.getNodeForDelivery(truckRep.get(indexStart)), instance.getNodeForDelivery(truckRep.get(indexStart+1)));
            }



            indexStart++;
        }


        return segmentTime;
    }



    //Calculate time of a drone delivery
    public double calculateDroneTime(DroneDeliveryRepresentation droneDelivery) {

        double distance = 0.0;



        Coordinate takeOff;
        Coordinate landingNode;
        if (droneDelivery.getTakeOffNode() == -1) {
            takeOff = instance.getStartNode();
        } else {
            takeOff = instance.getNodeForDelivery(droneDelivery.getTakeOffNode());
        }



        Coordinate delivery = instance.getNodeForDelivery(droneDelivery.getDroneDeliveryNode());

        if (droneDelivery.getLandingNode() == -1) {
            landingNode = instance.getStartNode();
        } else {
            landingNode = instance.getNodeForDelivery(droneDelivery.getLandingNode());
        }


        distance += getStraightLineDistance(takeOff, delivery);
        distance += getStraightLineDistance(delivery, landingNode);


        return (distance / droneSpeed);

    }

    //Calculate truck time of two adjacent coordinates
    public double calculateTruckTimeAdjacent(Coordinate startNode, Coordinate secondNode) {
        return getStraightLineDistance(startNode, secondNode) / truckSpeed;
    }




    //Euclidean distance
    public double getStraightLineDistance(Coordinate startNode, Coordinate secondNode) {
        double xDistance = Math.abs(startNode.getX() - secondNode.getX());
        double yDistance = Math.abs(startNode.getY() - secondNode.getY());

        double xSquaredDistance = Math.pow(xDistance, 2);
        double ySquaredDistance = Math.pow(yDistance, 2);

        return Math.pow(xSquaredDistance + ySquaredDistance, 0.5);
    }


}
