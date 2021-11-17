// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.util.Arrays;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    // TODO: define necessary variables and/or data structures
    private int mobFamilies;
    private int relationships;
    private int spies;
    private int[][] adjacencyMatrix;
    private BufferedReader bufferedReader;
    private String correctAnswer;
    private String bool;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    public int computeSATresult(int i, int j, int k) {
        return (i - 1) * k + j;
    }

    public int nonEdges(int[] array) {
        int count = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] == 0) {
                count += 1;
            }
        }
        return count - 1;
    }

    public int getType2ClausesNumber() {
        int sum = 0;
        for (int[] row : adjacencyMatrix) {
            sum += nonEdges(row) * (spies - 1) * spies;
        }
        sum -= nonEdges(adjacencyMatrix[0]) * (spies - 1) * spies;
        return sum;
    }

    public int getType3ClausesNumber() {
        return spies * mobFamilies * (mobFamilies - 1) / 2 + spies * (spies - 1) / 2 * mobFamilies;
    }


    @Override
    public void readProblemData() throws IOException {
        // TODO: read the problem input (inFilename) and store the data in the object's attributes
        bufferedReader = new BufferedReader(new FileReader(inFilename));
        String st;
        boolean first = true;

        while ((st = bufferedReader.readLine()) != null) {
            String[] s = st.split(" ");
            if (first) {
                mobFamilies = Integer.parseInt(s[0]);
                relationships = Integer.parseInt(s[1]);
                spies = Integer.parseInt(s[2]);
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

    @Override
    public void formulateOracleQuestion() throws IOException {
        // TODO: transform the current problem into a SAT problem and write it (oracleInFilename) in a format
        //  understood by the oracle


        FileWriter myWriter = new FileWriter(oracleInFilename);
        int type1Clauses = spies;
        int type2Clauses = getType2ClausesNumber();
        int type3Clauses = getType3ClausesNumber();

        myWriter.write("p cnf" + " " + spies * mobFamilies + " " + (type1Clauses + type2Clauses + type3Clauses) + "\n");

        // Write type1 clauses
        for (int i = 1; i <= spies; i++) {
            for (int j = 1; j <= mobFamilies; j++) {
                myWriter.write(computeSATresult(j, i, spies) + " ");
            }
            myWriter.write("0\n");
        }

        // Write type2 clauses
        for (int i = 1; i <= mobFamilies; i++) {
            for (int j = 1; j < i; j++) {
                for (int k = 1; k <= spies; k++) {
                    if (adjacencyMatrix[i][j] == 0) {
                        for (int l = 1; l <= spies; l++) {
                            if (l != k)
                                myWriter.write(-computeSATresult(j, k, spies) + " " + -computeSATresult(i, l, spies) + " 0\n");
                        }
                    }
                }
            }
        }

        // Write type3 clauses
        for (int i = 1; i <= mobFamilies; i++) {
            for (int j = 1; j <= spies; j++) {
                for (int k = 1; k < j; k++) {
                    myWriter.write(-computeSATresult(i, j, spies) + " " + -computeSATresult(i, k, spies) + " 0\n");
                }
            }
        }

        for (int i = 1; i <= spies; i++) {
            for (int j = 1; j <= mobFamilies; j++) {
                for (int k = 1; k < j; k++) {
                    myWriter.write(-computeSATresult(j, i, spies) + " " + -computeSATresult(k, i, spies) + " 0\n");
                }
            }
        }

        myWriter.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // TODO: extract the current problem's answer from the answer given by the oracle (oracleOutFilename)

        bufferedReader = new BufferedReader(new FileReader(oracleOutFilename));
        bool = bufferedReader.readLine();

        if (bool.startsWith("False")) {
            return;
        }
        correctAnswer = bufferedReader.readLine();
        correctAnswer = bufferedReader.readLine();
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)

        FileWriter myWriter = new FileWriter(outFilename);

        myWriter.write(bool);
        if (bool.startsWith("False")) {
            myWriter.close();
            return;
        }

        myWriter.write("\n");

        String[] answer = correctAnswer.split(" ");
        for (int i = 0; i < spies * mobFamilies; i++) {
            if (Integer.parseInt(answer[i]) > 0) {
                myWriter.write(((Integer.parseInt(answer[i]) - 1) / spies + 1) + " ");
            }
        }
        myWriter.close();
    }

    public int getSpies() {
        return spies;
    }

    public void setSpies(int spies) {
        this.spies = spies;
    }

    public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public void setMobFamilies(int mobFamilies) {
        this.mobFamilies = mobFamilies;
    }

    public void setRelationships(int relationships) {
        this.relationships = relationships;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getBool() {
        return bool;
    }

    public void clearOracleAns() throws IOException {
        FileWriter fileWriter = new FileWriter(oracleOutFilename, false);
        PrintWriter printWriter = new PrintWriter(fileWriter, false);
        printWriter.flush();
        printWriter.close();
        fileWriter.close();
    }

    public void clearOracleInput() throws IOException {
        FileWriter fileWriter = new FileWriter(oracleInFilename, false);
        PrintWriter printWriter = new PrintWriter(fileWriter, false);
        printWriter.flush();
        printWriter.close();
        fileWriter.close();
    }
}
