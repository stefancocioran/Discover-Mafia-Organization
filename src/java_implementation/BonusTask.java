import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Bonus Task
 * readProblemData         - read the problem input and store it
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    // define necessary variables and/or data structures

    private int mobFamilies;
    private int relationships;
    private int[][] adjacencyMatrix;
    private BufferedReader bufferedReader;
    private String correctAnswer;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // read the problem input (inFilename) and store the data in the object's attributes

        bufferedReader = new BufferedReader(new FileReader(inFilename));
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

    @Override
    public void formulateOracleQuestion() throws IOException {
        // transform the current problem into a SAT problem and write it (oracleInFilename) in a format
        //  understood by the oracle

        FileWriter myWriter = new FileWriter(oracleInFilename);
        int hardClauses = relationships;
        int softClauses = mobFamilies;
        int hardWeight = mobFamilies + 1;
        int softWeight = 1;

        myWriter.write("p wcnf" + " " + mobFamilies + " " + (hardClauses + softClauses) + " " + (mobFamilies + 1) + "\n");


        // Write soft clauses
        for (int i = 1; i <= mobFamilies; i++) {
            myWriter.write(softWeight + " " + -i + " " + "0\n");
        }

        // Write hard clauses
        for (int i = 1; i < mobFamilies; i++) {
            for (int j = i; j <= mobFamilies; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    myWriter.write(hardWeight + " " + i + " " + j + " " + "0\n");
                }
            }
        }

        myWriter.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // extract the current problem's answer from the answer given by the oracle (oracleOutFilename)

        bufferedReader = new BufferedReader(new FileReader(oracleOutFilename));
        correctAnswer = bufferedReader.readLine();
        correctAnswer = bufferedReader.readLine();
    }

    @Override
    public void writeAnswer() throws IOException {
        // write the answer to the current problem (outFilename)

        FileWriter myWriter = new FileWriter(outFilename);

        String[] answer = correctAnswer.split(" ");
        for (String s : answer) {
            if (Integer.parseInt(s) > 0) {
                myWriter.write(Integer.parseInt(s) + " ");
            }
        }
        myWriter.close();
    }
}
