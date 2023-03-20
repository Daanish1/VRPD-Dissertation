package com.solutioninstance;

//Represents one single drone delivery
public class DroneDeliveryRepresentation {

    private int[] droneDeliveryRep;
    private int takeOffNode;
    private int droneDeliveryNode;
    private int landingNode;


    public DroneDeliveryRepresentation(int takeOffNode, int droneDeliveryNode, int landingNode) {
        this.takeOffNode = takeOffNode;
        this.droneDeliveryNode = droneDeliveryNode;
        this.landingNode = landingNode;
        this.droneDeliveryRep = new int[]{takeOffNode, droneDeliveryNode, landingNode};
    }

    //Set a drone delivery
    public void setDroneDeliveryRep(int takeOff, int deliveryNode, int landing) {
        droneDeliveryRep[0] = takeOff;
        droneDeliveryRep[1] = deliveryNode;
        droneDeliveryRep[2] = landing;
    }

    public int getTakeOffNode() {
        return this.droneDeliveryRep[0];
    }

    public int getDroneDeliveryNode() {
        return this.droneDeliveryRep[1];
    }

    public int getLandingNode() {
        return this.droneDeliveryRep[2];
    }


    //Put drone delivery into output string
    public String toString() {
        String output = "(";

        output += getTakeOffNode() + ", ";
        output += getDroneDeliveryNode() + ", ";
        output += getLandingNode();
        output += ")";

        return output;
    }
}
