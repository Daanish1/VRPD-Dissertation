package com.solutioninstance;

// X,Y coordinate to identify location of delivery nodes on coordinate grid
public class Coordinate {

    private final int xCoord, yCoord;

    public Coordinate(int xCoord, int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getX() {
        return this.xCoord;
    }

    public int getY() {
        return this.yCoord;
    }

    //Retrieve string of coordinate in format (x,y)
    public String toString() {
        String output = "(";
        output += this.getX();
        output += ", ";
        output += this.getY();
        output += ")";

        return output;
    }

    //Check if two coordinates are the same
    public boolean compare(Coordinate coordB) {

        int x1 = this.getX();
        int y1 = this.getY();

        int x2 = coordB.getX();
        int y2 = coordB.getY();

        if ((x1 == x2) && (y1 == y2)) {
            return true;
        }else {
            return false;
        }

    }
}
