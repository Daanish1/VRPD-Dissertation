package com.heuristics;

import com.Constants;
import com.heuristics.drone.DroneGreedyInsertion;
import com.heuristics.truck.TruckNearestNeighbour;
import com.solutioninstance.Coordinate;
import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;
import com.solutioninstance.ObjectiveFunction;

import java.util.ArrayList;
import java.util.Random;

//Cluster solutions and also select which number of clusters to use
public class KMeansClustering {

    private Random random;
    private SolutionInstance instance;
    private ObjectiveFunction objectiveFunction;


    public KMeansClustering(Random random, ObjectiveFunction objectiveFunction, SolutionInstance instance) {
        this.random = random;
        this.instance = instance;
        this.objectiveFunction = objectiveFunction;
    }

    //Find out which cluster a delivery node is assigned to
    private int findClusterFor(int i, boolean rank[][]) {
        for (int j = 0; j < rank.length; j++) {
            if (rank[j][i]) {
                return j;
            }
        }

        System.out.println("Error findClusterFor() - No cluster assigned to i");
        return -1;
    }

    //Silhouette coefficient segment: calculate b(i)
    private double calculateMeanClusterDissimilarity(int i, int cluster, boolean rank[][]) {

        int count = 0;
        double totalDistance = 0;
        for (int j = 0; j < rank[cluster].length; j++) {
            if (rank[cluster][j]) {
                totalDistance += getDistanceBetween(instance.getNodeForDelivery(i), instance.getNodeForDelivery(j));
                count++;
            }
        }

        return totalDistance/count;
    }


    //Calculate how many values are in cluster c
    private int calculateClusterCardinality(boolean rank[][], int c) {
        int cardinality = 0;

        for (int i = 0; i < rank[c].length; i++) {
            if (rank[c][i]) {
                cardinality++;
            }
        }

        return cardinality;
    }

    //calculate silhouette value for single delivery node i
    private double calculateSilhouetteValue(boolean rank[][], int i) {
        //a(i) = mean distance between i and all other points in i's cluster

        //Determine i's cluster
        int c = findClusterFor(i, rank);

        int clusterCardinality = calculateClusterCardinality(rank, c);

        if (clusterCardinality == 1) {
            return 0.0;
        }


        int count = 0;
        double totalDistance = 0;
        for (int j = 0; j < rank[c].length; j++) {
            if ((rank[c][j]) && (j != i)) {
                //Distance between i and j
                totalDistance += getDistanceBetween(instance.getNodeForDelivery(i), instance.getNodeForDelivery(j));
                count++;
            }
        }

        //a(i)
        double iMeanIntraClusterDistance = totalDistance / count;

        //b(i)
        double iNeighbouringClusterDissimilarity = -1;

        double clusterMeanDissimilarity[] = new double[rank.length];

        for (int j = 0; j < rank.length; j++) {
            if (j == c) {
                clusterMeanDissimilarity[j] = -1;
                continue;
            }

            clusterMeanDissimilarity[j] = calculateMeanClusterDissimilarity(i, j, rank);

            if ((iNeighbouringClusterDissimilarity < 0) || (iNeighbouringClusterDissimilarity > clusterMeanDissimilarity[j])) {
                iNeighbouringClusterDissimilarity = clusterMeanDissimilarity[j];
            }

        }


        //Calculate silhouette coefficient using equation (b(i) - a(i)/Max(a(i), b(i)))
        double iSilhouetteValue = (iNeighbouringClusterDissimilarity - iMeanIntraClusterDistance) / (Math.max(iNeighbouringClusterDissimilarity, iMeanIntraClusterDistance));



        return iSilhouetteValue;
    }



    private double calculateMeanOfSilhouetteValues(double silhouetteValues[]) {
        int count = 0;
        double sum = 0;

        for (int i = 0; i < silhouetteValues.length; i++) {
            sum += silhouetteValues[i];
            count++;
        }

        return sum/count;
    }

    //Calculate Silhouette coefficient
    private double calculateSilhouetteCoefficient(boolean rank[][]) {
        double silhouetteValues[] = new double[instance.getNumberOfLocations()];

        for (int i = 0; i < instance.getNumberOfLocations(); i++) {
            silhouetteValues[i] = calculateSilhouetteValue(rank, i);
        }


        double silhouetteCoefficient = calculateMeanOfSilhouetteValues(silhouetteValues);


        return silhouetteCoefficient;
    }


    public boolean isValidCluster(boolean rank[][]) {


        //Loop through each rank[i] and check if at least 3 rank[i][j] = 1
        for (int i = 0; i < rank.length; i++) {
            boolean singleCluster = false;
            int clusterCount = 0;
            for (int j = 0; j < rank[i].length; j++) {
                if (rank[i][j]) {
                    singleCluster = true;
                    clusterCount++;
                }
            }
            if ((!singleCluster) || (clusterCount < Constants.MIN_VALUES_PER_CLUSTER)) {
                return false;
            }


        }

        return true;


    }


    //Select which cluster number has the best silhouette coefficient
    public Solution[] selectClusters(int inputK) {

        if (inputK > 0) {
            boolean rank[][] = cluster(inputK);

            //Initialise solution with KNN and then DGI
            TruckNearestNeighbour TNN = new TruckNearestNeighbour(this.random, this.objectiveFunction, this.instance);
            DroneGreedyInsertion DGI = new DroneGreedyInsertion(this.random, this.objectiveFunction, this.instance);

            //Apply solution initialisation to best clustered data
            Solution solution[] = new Solution[inputK];
            for (int i = 0; i < inputK; i++) {
                ArrayList<Integer> cluster = new ArrayList<>();
                for (int j = 0; j < rank[i].length; j++) {
                    if (rank[i][j]) {
                        cluster.add(j);
                    }
                }

                solution[i] = TNN.initialiseTruckNearestNeighbourSolution(cluster);
                solution[i] = DGI.insertDroneNodes(solution[i].getRepresentation());
            }

            return solution;


        } else {
            int bestK = 0;
            boolean bestRank[][] = null;
            double bestSilhouetteCoefficient = -10;


            for (int k = 1; k < Constants.MAX_CLUSTERS; k++) {

                //Separate into k clusters
                boolean rank[][] = cluster(k);

                //Calculate silhouette coefficient
                double silhouetteCoefficient = calculateSilhouetteCoefficient(rank);

                //Keep track of best number of clusters
                if ((silhouetteCoefficient > bestSilhouetteCoefficient) && (isValidCluster(rank))) {
                    bestSilhouetteCoefficient = silhouetteCoefficient;
                    bestRank = rank;
                    bestK = k;
                }

            }


            //Initialise solution with KNN and then DGI
            TruckNearestNeighbour TNN = new TruckNearestNeighbour(this.random, this.objectiveFunction, this.instance);
            DroneGreedyInsertion DGI = new DroneGreedyInsertion(this.random, this.objectiveFunction, this.instance);

            //Apply solution initialisation to best clustered data
            Solution solution[] = new Solution[bestK];
            for (int i = 0; i < bestK; i++) {
                ArrayList<Integer> cluster = new ArrayList<>();
                for (int j = 0; j < bestRank[i].length; j++) {
                    if (bestRank[i][j]) {
                        cluster.add(j);
                    }
                }

                solution[i] = TNN.initialiseTruckNearestNeighbourSolution(cluster);
                solution[i] = DGI.insertDroneNodes(solution[i].getRepresentation());
            }


            return solution;
        }




    }


    //Cluster solutions into k clusters
    public boolean[][] cluster(int k) {

        //Array of cluster centroids
        Coordinate centroids[] = new Coordinate[k];


        //Create random coordinates for all cluster centroids
        for (int i = 0; i < k; i++) {
            centroids[i] = new Coordinate(random.nextInt(Constants.COORDINATE_MAX), random.nextInt(Constants.COORDINATE_MAX));
        }


        //Initialise k ranks
        boolean rank[][] = new boolean[k][instance.getNumberOfLocations()];


        //Initialise all ranks to false
        for (int i = 0; i < rank.length; i++) {
            for (int j = 0; j < rank[i].length; j++) {
                rank[i][j] = false;
            }
        }


        boolean keepRunning = true;

        while (keepRunning) {
            //For each delivery node assign true to the cluster of which its closest to
            for (int i = 0; i < instance.getNumberOfLocations(); i++) {

                //Find which centroid is closest to location[i] and set that rank[k][i] to true
                int closestK = getClosestK(centroids, instance.getNodeForDelivery(i));
                rank[closestK][i] = true;

                //Set all other columns to false:
                rank = setOthersToFalse(rank, k, closestK, i);

            }

            //Produce new cluster centroids
            Coordinate newCentroids[] = new Coordinate[k];

            //Update centroids based on mean of each cluster
            for (int i = 0; i < k; i++) {

                int sumX = 0;
                int sumY = 0;
                int count = 0;

                for (int j = 0; j < rank[i].length; j++) {
                    if (rank[i][j]) {
                        count++;
                        sumX += instance.getNodeForDelivery(j).getX();
                        sumY += instance.getNodeForDelivery(j).getY();
                    }
                }


                int newXCluster;
                int newYCluster;

                if (count == 0) {
                    newXCluster = centroids[i].getX();
                    newYCluster = centroids[i].getY();
                } else {
                    newXCluster = sumX / count;
                    newYCluster = sumY / count;
                }


                newCentroids[i] = new Coordinate(newXCluster, newYCluster);


            }

            //If change in cluster centroids keep running otherwise you have converged
            if (compareCentroids(centroids, newCentroids)) {
                keepRunning = false;
            }

            centroids = newCentroids;


        }


        return rank;
    }


    //Check if two sets of cluster centroids are the same set of coordinates
    boolean compareCentroids(Coordinate centroids[], Coordinate newCentroids[]) {

        for (int i = 0; i < centroids.length; i++) {
            if (!centroids[i].compare(newCentroids[i])) {
                return false;
            }
        }

        return true;
    }

    //Set ranks to false
    boolean[][] setOthersToFalse(boolean rank[][], int k, int closestK, int i) {

        for (int j = 0; j < k; j++) {
            if (j != closestK) {
                rank[j][i] = false;
            }
        }

        return rank;

    }

    //Find closest cluster
    int getClosestK(Coordinate centroids[], Coordinate location) {
        double closestDistance = getDistanceBetween(centroids[0], location);
        int k = 0;

        for (int i = 1; i < centroids.length; i++) {
            double newDist = getDistanceBetween(centroids[i], location);

            if (newDist < closestDistance) {
                closestDistance = newDist;
                k = i;
            }
        }


        return k;
    }


    //Straight line distance
    double getDistanceBetween(Coordinate coordA, Coordinate coordB) {
        double x1 = coordA.getX();
        double y1 = coordA.getY();

        double x2 = coordB.getX();
        double y2 = coordB.getY();

        double xDist = Math.pow((x1 - x2), 2.0);
        double yDist = Math.pow((y1 - y2), 2.0);


        return Math.pow(xDist + yDist, 0.5);

    }

}
