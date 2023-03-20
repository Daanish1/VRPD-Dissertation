package com.solutioninstance;

import java.util.Random;

//Keeps track of the delivery locations as well as the starting location
public class SolutionInstance {

    //Values to keep track of
    private final Coordinate[] deliveryNodes;
    private final Coordinate startNode;
    private final int numberOfLocations;
    private final Random random;


    public SolutionInstance(Coordinate[] deliveryNodes, Coordinate start, Random random) {
        this.deliveryNodes = deliveryNodes;
        this.startNode = start;
        this.numberOfLocations = getDeliveryNodes().length;
        this.random = random;


    }


    private Coordinate[] getDeliveryNodes() {
        return this.deliveryNodes;
    }

    public Coordinate getStartNode() {
        return this.startNode;
    }

    //Get number of delivery nodes
    public int getNumberOfLocations() {
        return this.numberOfLocations;
    }

    //Get delivery coordinate at index
    public Coordinate getNodeForDelivery(int index) {
        return getDeliveryNodes()[index];
    }

}
