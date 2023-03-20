package com.runnables;


import com.solutionrunner.Runner;
import com.writer.InstanceCreator;

import java.util.Random;

//This class is used to produce a solution to a single problem instance
public class Main {

    public static void main(String[] args) {

        //Variable set to true if you want to create a new problem instance
        boolean createInstance = false;


        String fileName = "newInstance.txt";

        if (createInstance) {
            //Create instances of problems


            //Use this following line to create clustered problem instance
            InstanceCreator.createWithGaussianClusters(fileName, 100);


            //Use this following line to create randomly distributed problem instance
            //InstanceCreator.create(fileName, 100);
        }


        //Produce a random seed (for repetition if needed)
        int seed = new Random().nextInt(100000);

        //Problem instance file that will be worked on
        String instanceFile = "oldprobleminstances/3_instance20.txt";

        //Instantiate new runner using the random seed and the chosen problem instance file
        Runner runner = new Runner(seed, instanceFile);
        runner.loadInstance();
        runner.run();

        System.out.println("Seed = " + seed);

        //Display the solution on screen
        runner.displaySolution();

    }
}
