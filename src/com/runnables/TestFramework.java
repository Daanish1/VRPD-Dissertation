package com.runnables;

import com.solutionrunner.TestRunner;

import java.util.Random;

public class TestFramework {


    public static void main(String[] args) {
        //Test files - first number relates to "Instance number" in documented results
        String[] files = {
                "1Instance10R", "2Instance10R", "3Instance10R", "4Instance10R", "5Instance10R",
                "6Instance10C", "7Instance10C", "8Instance10C", "9Instance10C", "10Instance10C",
                "11Instance20R", "12Instance20R", "13Instance20R", "14Instance20R", "15Instance20R",
                "16Instance20C" , "17Instance20C", "18Instance20C", "19Instance20C", "20Instance20C",
                "21Instance50R", "22Instance50R", "23Instance50R", "24Instance50R", "25Instance50R",
                "26Instance50C", "27Instance50C", "28Instance50C", "29Instance50C", "30Instance50C",
                "31Instance100R", "32Instance100R", "33Instance100R", "34Instance100R", "35Instance100R",
                "36Instance100C", "37Instance100C", "38Instance100C", "39Instance100C", "40Instance100C"
        };

        //Loop through files and run each algorithm on it
        for (String file : files) {

            String instanceFile = file + ".txt";

            //Run 5 times
            for (int i = 0; i < 5; i++) {
                System.out.println("Instance = " + instanceFile);

                System.out.println("Run = " + (i + 1));

                //Random seed
                int seed = new Random().nextInt(100000);
                /*
                2-OPT = 2-opt
                DRR = DestroyRepairRoute
                DRR_FR = DestroyRepairFixedRange
                DRR_DA = DestroyRepairWithDroneAssignment
                DRR_RR = DestroyRepairWithRangeReduction
                 */
                String[] algorithms = {"2-OPT", "DRR", "DRR_FR", "DRR_DA", "DRR_RR"};

                System.out.println("=====");

                //Run each algorithm on the problem instance
                for (String alg : algorithms) {
                    TestRunner testRunner = new TestRunner(seed, instanceFile, alg);
                    testRunner.loadInstance();
                    testRunner.run();

                }
                System.out.println("===========================");

            }

            System.out.println("=======================================================");

        }

    }
}
