// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    // TODO: define necessary variables and/or data structures
    private int mobFamilies;
    private int relationships;
    private int spies;
    private int[][] adjacencyMatrix;
    private String correctAnswer;
    Task2 task2Solver = new Task2();

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();

        // TODO: implement a way of successively querying the oracle (using Task2) about various arrest numbers until you
        //  find the minimum

        reduceToTask2();
        extractAnswerFromTask2();
        writeAnswer();
    }

    public void computeComplementaryMatrix() {
        for (int i = 1; i <= mobFamilies; i++) {
            for (int j = 1; j <= mobFamilies; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    adjacencyMatrix[i][j] = 0;
                } else {
                    if (i != j) {
                        adjacencyMatrix[i][j] = 1;
                    }
                }
            }
        }
    }

    @Override
    public void readProblemData() throws IOException {
        // TODO: read the problem input (inFilename) and store the data in the object's attributes
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inFilename));
        String st;
        boolean first = true;

        while ((st = bufferedReader.readLine()) != null) {
            String[] s = st.split(" ");
            if (first) {
                mobFamilies = Integer.parseInt(s[0]);
                relationships = Integer.parseInt(s[1]);
                adjacencyMatrix = new int[mobFamilies + 1][mobFamilies + 1];
                for (int[] row : adjacencyMatrix)
                    Arrays.fill(row, 0);
                first = false;
            } else {
                int firstFamily = Integer.parseInt(s[0]);
                int secondFamily = Integer.parseInt(s[1]);
                adjacencyMatrix[firstFamily][secondFamily] = 1;
                adjacencyMatrix[secondFamily][firstFamily] = 1;
            }
        }
    }

    public void reduceToTask2() throws IOException, InterruptedException {
        // TODO: reduce the current problem to Task2

        computeComplementaryMatrix();
        task2Solver.setAdjacencyMatrix(adjacencyMatrix);
        task2Solver.setMobFamilies(mobFamilies);
        task2Solver.setRelationships(relationships);

        for (int i = mobFamilies; i >= 1; i--) {
            task2Solver.setSpies(i);
            task2Solver.formulateOracleQuestion();
            task2Solver.askOracle();
            task2Solver.decipherOracleAnswer();

            if (task2Solver.getBool().startsWith("False")) {
                task2Solver.clearOracleAns();
                task2Solver.clearOracleInput();
            } else {
                break;
            }
        }
    }

    public void extractAnswerFromTask2() throws IOException {
        // TODO: extract the current problem's answer from Task2's answer

        correctAnswer = task2Solver.getCorrectAnswer();
        spies = task2Solver.getSpies();
        task2Solver.outFilename = outFilename;
        task2Solver.writeAnswer();
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)

        FileWriter myWriter = new FileWriter(outFilename);

        List<Integer> possibleAnswer = new ArrayList<>();
        for (int i = 1; i <= mobFamilies; i++) {
            possibleAnswer.add(i);
        }

        String[] answer = correctAnswer.split(" ");
        for (int i = 0; i < spies * mobFamilies; i++) {
            if (Integer.parseInt(answer[i]) > 0) {
                possibleAnswer.remove(Integer.valueOf(((Integer.parseInt(answer[i]) - 1) / spies + 1)));
            }
        }
        for (Integer integer : possibleAnswer) {
            myWriter.write(integer + " ");
        }
        myWriter.close();
    }
}
