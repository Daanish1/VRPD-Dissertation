package com.reader;

import com.solutioninstance.Coordinate;
import com.solutioninstance.SolutionInstance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class InstanceReader {
    private String fileName;
    File file;
    private Random random;

    public InstanceReader(String fileName, Random random) {
        this.fileName = fileName;
        this.random = random;

    }

    public SolutionInstance read() {

        String startNodeAsString = "";
        ArrayList<String> deliveryNodesAsString = new ArrayList<String>();

        try {
            file = new File(fileName);
            Scanner reader = new Scanner(file);

            String line;


            while (reader.hasNextLine()) {
                line = reader.nextLine();
                if (line.contains("START_NODE")) {
                    line = reader.nextLine();
                    startNodeAsString = line;
                } else if (line.contains("DELIVERY_NODES")) {
                    while(reader.hasNextLine()) {
                        line = reader.nextLine();
                        deliveryNodesAsString.add(line);
                    }
                }

            }


        }catch (FileNotFoundException e) {
            System.out.println("Could not open file");
            e.printStackTrace();
            return null;
        }


        String[] startNodeSplitString = startNodeAsString.split(", ");
        Coordinate startNode = splitIntoCoordinate(startNodeSplitString);

        Coordinate[] deliveryNodes = new Coordinate[deliveryNodesAsString.size()];

        String[] splitDeliveryNode;
        for (int i = 0; i < deliveryNodesAsString.size(); i++) {
            splitDeliveryNode = deliveryNodesAsString.get(i).split(", ");
            deliveryNodes[i] = splitIntoCoordinate(splitDeliveryNode);
        }

        return new SolutionInstance(deliveryNodes, startNode, random);
    }

    public Coordinate splitIntoCoordinate(String[] input) {
        int x = Integer.parseInt(input[0]);
        int y = Integer.parseInt(input[1]);

        return new Coordinate(x, y);
    }


}
