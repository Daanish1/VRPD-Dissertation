package com.writer;

import com.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class InstanceCreator {

    //Create randomly distributed delivery nodes
    public static void create(String filename, int numberOfNodes) {

        Random rand = new Random();

        try {
            File myFile = new File(filename);
            if (myFile.createNewFile()) {
                FileWriter writer = new FileWriter(filename);
                writer.write("START_NODE\n");
                //Create coordinate
                writer.write("" + rand.nextInt(Constants.COORDINATE_MAX) + ", " + rand.nextInt(Constants.COORDINATE_MAX) + "\n");

                //Create coordinate
                writer.write("DELIVERY_NODES");
                for (int i = 0; i < numberOfNodes; i++) {
                    writer.write("\n" + rand.nextInt(Constants.COORDINATE_MAX) + ", " + rand.nextInt(Constants.COORDINATE_MAX));
                }


                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }


    public static void createWithGaussianClusters(String filename, int numberOfNodes) {
        Random rand = new Random();


        //random number 1-5
        int numberOfClusters = rand.nextInt(Constants.MAX_CLUSTERS) + 1;


        System.out.println("NUMBER OF CLUSTERS = " + numberOfClusters);

        int whichCluster[] = new int[numberOfNodes];
        int numbersInCluster[] = new int[numberOfClusters];

        int clusterCentroidX[] = new int[numberOfClusters];
        int clusterCentroidY[] = new int[numberOfClusters];


        for (int i = 0; i < numbersInCluster.length; i++) {
            //set values in cluster to 0
            numbersInCluster[i] = 0;

            //set cluster centre
            clusterCentroidX[i] = rand.nextInt(Constants.CLUSTER_COORDINATE_MAX - Constants.CLUSTER_COORDINATE_MIN) + Constants.CLUSTER_COORDINATE_MIN;
            clusterCentroidY[i] = rand.nextInt(Constants.CLUSTER_COORDINATE_MAX - Constants.CLUSTER_COORDINATE_MIN) + Constants.CLUSTER_COORDINATE_MIN;
        }

        for (int i = 0; i < numberOfNodes; i++) {
            //select random cluster (index 0-4)
            int x = rand.nextInt(numberOfClusters);
            whichCluster[i] = x;
            numbersInCluster[x] = numbersInCluster[x] + 1;
        }

        //====================

        try{
            File myFile = new File(filename);
            if (myFile.createNewFile()) {
                FileWriter writer = new FileWriter(filename);
                writer.write("START_NODE\n");
                writer.write("" + rand.nextInt(Constants.COORDINATE_MAX) + ", " + rand.nextInt(Constants.COORDINATE_MAX) + "\n");
                writer.write("DELIVERY_NODES");



                for (int i = 0; i < numbersInCluster.length; i++) {
                    int numbersToAssign = numbersInCluster[i];
                    int clusterLocX = clusterCentroidX[i];
                    int clusterLocY = clusterCentroidY[i];


                    for (int j = 0; j < numbersToAssign; j++) {
                        double xBias = rand.nextGaussian();
                        double yBias = rand.nextGaussian();

                        int x;
                        int y;

                        int xBiasInt = (int) Math.round(Constants.CLUSTER_STANDARD_DEVIATION * xBias + Constants.CLUSTER_MEAN);
                        int yBiasInt = (int) Math.round(Constants.CLUSTER_STANDARD_DEVIATION * yBias + Constants.CLUSTER_MEAN);


                        if (rand.nextDouble() < 0.5) {
                            x = clusterLocX + xBiasInt;
                        } else {
                            x = clusterLocX - xBiasInt;
                        }

                        if (rand.nextDouble() < 0.5) {
                            y = clusterLocY + yBiasInt;
                        } else {
                            y = clusterLocY - yBiasInt;
                        }

                        if (x > Constants.COORDINATE_MAX) {
                            x = Constants.COORDINATE_MAX;
                        }
                        if (x < Constants.COORDINATE_MIN) {
                            x = Constants.COORDINATE_MIN;
                        }

                        if (y > Constants.COORDINATE_MAX) {
                            y = Constants.COORDINATE_MAX;
                        }
                        if (y < Constants.COORDINATE_MIN) {
                            y = Constants.COORDINATE_MIN;
                        }


                        //write to file============================
                        writer.write("\n" + x + ", " + y);
                    }
                }





                writer.close();
            }
        } catch(IOException e) {
            System.out.println("File Error");
        }


    }

}
