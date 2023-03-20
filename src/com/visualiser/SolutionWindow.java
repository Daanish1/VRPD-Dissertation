package com.visualiser;

import com.solutioninstance.Solution;
import com.solutioninstance.SolutionInstance;

import javax.swing.*;

public class SolutionWindow extends JFrame {

    public SolutionWindow(Solution sol[], SolutionInstance instance, String instanceName) {
        initUI(instanceName);
        DrawSolution panel = new DrawSolution(sol, instance);
        this.add(panel);

        this.setResizable(false);
    }

    //Create UI window
    private void initUI(String instanceName) {
        setTitle("Solution Visual: " + instanceName);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
