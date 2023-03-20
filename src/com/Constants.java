package com;

public class Constants {

    //Coordinate grid
    public static int COORDINATE_MAX = 500;
    public static int COORDINATE_MIN = 0;


    //Cluster centroid bounds
    public static int CLUSTER_COORDINATE_MAX = 400;
    public static int CLUSTER_COORDINATE_MIN = 100;

    //Max clusters
    public static int MAX_CLUSTERS = 5;

    //Standard deviation of created clusters
    public static int CLUSTER_STANDARD_DEVIATION = 30;
    public static int CLUSTER_MEAN = 50;


    public static int CLUSTER_RADIUS_MIN = 50;
    public static int CLUSTER_RADIUS_RANGE = 50;

    //Minimum values assigned to a cluster
    public static int MIN_VALUES_PER_CLUSTER = 4;


    //DRR
    public static int DESTROY_REPAIR_ITERATIONS = 100000;
    public static int DESTROY_REPAIR_MAX_NO_IMPROVEMENTS = 1000000;

    //DRR-Destroy-Range Percentage
    public static double DESTROY_RANGE_PERCENTAGE = 0.1;


    //2-OPT
    public static int TWO_OPT_ITERATIONS = 1000000;


    //Time Elapsed (5 minutes)
//    public static long RUN_TIME = 300 * 1000;

    public static long RUN_TIME = 20 * 1000;


}
