package com.visualiser;

import com.solutioninstance.Coordinate;
import com.solutioninstance.DroneDeliveryRepresentation;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DrawSolution extends JPanel {

    private Solution sol[];
    private SolutionInstance instance;
    private final int nodeDiameter = 10;

    public DrawSolution(Solution sol[], SolutionInstance instance) {
        this.sol = sol;
        this.instance = instance;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(Color.LIGHT_GRAY);




        for (int i = 0; i < sol.length; i++) {
//            if (i != 0)
//                continue;

            //Draw Start Node
            g.setColor(Color.GREEN);
            Coordinate start = instance.getStartNode();
            drawNodeAtPoint(g, start.getX(), start.getY());

            //Draw Truck Nodes
            g.setColor(Color.BLUE);
            ArrayList<Integer> truck = sol[i].getRepresentation().getTruckRep();
            Coordinate toDraw;
            for (int j = 0; j < truck.size(); j++) {
                toDraw = instance.getNodeForDelivery(truck.get(j));
                drawNodeAtPoint(g, toDraw.getX(), toDraw.getY());
            }


            //Draw Truck Routes====================================================
            if (truck.size() > 0) {
                g.drawLine(start.getX(), start.getY(), instance.getNodeForDelivery(truck.get(0)).getX(), instance.getNodeForDelivery(truck.get(0)).getY());

                Coordinate firstNode;
                Coordinate secondNode = instance.getNodeForDelivery(truck.get(0));
                for (int j = 0; j < truck.size()-1; j++) {


                    firstNode = instance.getNodeForDelivery(truck.get(j));
                    secondNode = instance.getNodeForDelivery(truck.get(j+1));
                    g.drawLine(firstNode.getX(), firstNode.getY(), secondNode.getX(), secondNode.getY());
                }



                firstNode = secondNode;
                secondNode = start;
                g.drawLine(firstNode.getX(), firstNode.getY(), secondNode.getX(), secondNode.getY());
            }



            if (sol[i].getRepresentation().getDroneRep() != null) {

                //Draw Drone Nodes
                g.setColor(Color.RED);
//                g.setColor(Color.BLUE);
                ArrayList<DroneDeliveryRepresentation> drone = sol[i].getRepresentation().getDroneRep();
                for (int j = 0; j < drone.size(); j++) {
                    toDraw = instance.getNodeForDelivery(drone.get(j).getDroneDeliveryNode());
                    drawNodeAtPoint(g, toDraw.getX(), toDraw.getY());
                }





                //Draw Drone Routes==============================================

                Graphics2D gCopy = (Graphics2D) g.create();

                Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{9}, 0);
                gCopy.setStroke(dashed);


                Coordinate takeOff;
                Coordinate deliver;
                Coordinate land;
                for (int j = 0; j < drone.size(); j++) {
                    if (drone.get(j).getTakeOffNode() == -1) {
                        takeOff = start;
                    } else {
                        takeOff = instance.getNodeForDelivery(drone.get(j).getTakeOffNode());
                    }

                    if (drone.get(j).getLandingNode() == -1) {
                        land = start;
                    } else {
                        land = instance.getNodeForDelivery(drone.get(j).getLandingNode());
                    }

                    deliver = instance.getNodeForDelivery(drone.get(j).getDroneDeliveryNode());


                    gCopy.drawLine(takeOff.getX(), takeOff.getY(), deliver.getX(), deliver.getY());
                    gCopy.drawLine(deliver.getX(), deliver.getY(), land.getX(), land.getY());


                }

                gCopy.dispose();

            }
        }

    }

    public void drawNodeAtPoint(Graphics g, int x, int y) {
        g.fillOval(x - (nodeDiameter/2), y - (nodeDiameter/2), nodeDiameter, nodeDiameter);
    }
}
