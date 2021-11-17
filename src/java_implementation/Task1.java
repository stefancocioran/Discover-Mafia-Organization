// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    // TODO: define necessary variables and/or data structures
    private int mobFamilies;
    private int relationships;
    private int spies;
    private int[][] adjacencyMatrix;
    private BufferedReader bufferedReader;

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

    public int getType3ClausesNumber(int colorsNumber, int nodesNumber) {
        return nodesNumber * (colorsNumber - 1) * colorsNumber / 2;
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
        int type1Clauses = spies * relationships;
        int type2Clauses = mobFamilies;
        int type3Clauses = getType3ClausesNumber(spies, mobFamilies);

        myWriter.write("p cnf" + " " + spies * mobFamilies + " " + (type1Clauses + type2Clauses + type3Clauses) + "\n");

        // Write type 1 clauses
        for (int i = 1; i < adjacencyMatrix[0].length; i++) {
            for (int j = 1; j < i; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    for (int k = 1; k <= spies; k++) {
                        myWriter.write(-computeSATresult(i, k, spies) + " " + -computeSATresult(j, k, spies) + " 0\n");
                    }
                }
            }
        }

        // Write type 2 clauses
        for (int i = 1; i <= mobFamilies; i++) {
            for (int j = 1; j <= spies; j++) {
                myWriter.write(computeSATresult(i, j, spies) + " ");
            }
            myWriter.write("0\n");
        }

        // Write type 3 clauses
        for (int i = 1; i <= mobFamilies; i++) {
            for (int j = 1; j < spies; j++) {
                for (int k = j + 1; k <= spies; k++) {
                    myWriter.write(-computeSATresult(i, j, spies) + " " + -computeSATresult(i, k, spies) + " 0\n");
                }
            }
        }
        myWriter.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // TODO: write the answer to the current problem (outFilename)
        bufferedReader = new BufferedReader(new FileReader(oracleOutFilename));
        FileWriter myWriter = new FileWriter(outFilename);
        String st = bufferedReader.readLine();

        myWriter.write(st);
        if (st.startsWith("False")) {
            myWriter.close();
            return;
        }

        myWriter.write("\n");
        st = bufferedReader.readLine();
        st = bufferedReader.readLine();
        String[] answer = st.split(" ");
        for (int i = 0; i < spies * mobFamilies; i++) {
            if (Integer.parseInt(answer[i]) > 0) {
                myWriter.write(Integer.parseInt(answer[i]) % spies + " ");
            }
        }
        myWriter.close();
    }
}
