package com.solutioninstance;

import java.util.ArrayList;


//Keeps track of both truck and drone solution list
public class SolutionRepresentation {

    private ArrayList<DroneDeliveryRepresentation> droneRep;
    private ArrayList<Integer> truckRep;

    public SolutionRepresentation(ArrayList<Integer> truckRep, ArrayList<DroneDeliveryRepresentation> droneRep) {

        setTruckRep(truckRep);
        setDroneRep(droneRep);

    }

    public ArrayList<Integer> getTruckRep() {
        return truckRep;
    }

    public ArrayList<DroneDeliveryRepresentation> getDroneRep() {
        return droneRep;
    }

    public void setTruckRep(ArrayList<Integer> newRep) {
        this.truckRep = newRep;
    }

    public void setDroneRep(ArrayList<DroneDeliveryRepresentation> newRep) {
        this.droneRep = newRep;
    }


    //Copy whole solution representation
    public SolutionRepresentation deepCopy() {
        ArrayList<Integer> truckRepCopy = copyTruckRep();
        ArrayList<DroneDeliveryRepresentation> droneRepCopy = copyDroneDelivery();



        return new SolutionRepresentation(truckRepCopy, droneRepCopy);
    }


    //Copy truck solution
    private ArrayList<Integer> copyTruckRep() {
        ArrayList<Integer> copyOfTruck = new ArrayList<>();


        for (int i = 0; i < this.truckRep.size(); i++) {
            copyOfTruck.add(truckRep.get(i));
        }


        return copyOfTruck;
    }

    //Copy drone solution
    private ArrayList<DroneDeliveryRepresentation> copyDroneDelivery() {
        ArrayList<DroneDeliveryRepresentation> copyOfDrone = new ArrayList<>();


        for (int i = 0; i < this.droneRep.size(); i++) {
            DroneDeliveryRepresentation temp = new DroneDeliveryRepresentation(this.droneRep.get(i).getTakeOffNode(), this.droneRep.get(i).getDroneDeliveryNode(), this.droneRep.get(i).getLandingNode());
            copyOfDrone.add(temp);
        }


        return copyOfDrone;
    }

}
