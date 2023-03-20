package com.solutioninstance;

//Keeps track of the solution representation as well as its corresponding objective function value
public class Solution {

    private SolutionRepresentation representation;
    private double objectiveFunctionValue;


    public Solution(SolutionRepresentation representation, double objectiveFunctionValue) {
        this.representation = representation;
        setObjectiveFunctionValue(objectiveFunctionValue);
    }

    public SolutionRepresentation getRepresentation() {
        return this.representation;
    }

    public void setRepresentation(SolutionRepresentation newRep) {
        this.representation = newRep;
    }

    public double getObjectiveFunctionValue() {
        return this.objectiveFunctionValue;
    }

    public void setObjectiveFunctionValue(double newObjectiveFunctionValue) {
        this.objectiveFunctionValue = newObjectiveFunctionValue;
    }

}
