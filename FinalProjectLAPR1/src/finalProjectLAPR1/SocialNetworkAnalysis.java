package finalProjectLAPR1;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;
import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

public class SocialNetworkAnalysis {

    public static final int BRANCHESFILE_ARGUMENT_POSITION_N = 2;
    public static final int BRANCHESFILE_ARGUMENT_POSITION_T = 4;
    public static final int NODESFILE_ARGUMENT_POSITION_N = 1;
    public static final int NODESFILE_ARGUMENT_POSITION_T = 3;
    public static final int POWER_ARGUMENT_POSITION = 2;
    public static final int MAX_NUMBER_COLUMN_BRANCH_MATRIX = 2;
    public static final int MAX_NUMBER_NODES = 200;
    public static final int N_MAX_COLUMN_FILE_ONE = 4;
    public static final int N_MAX_COLUMN_FILE_TWO = 3;

    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) throws FileNotFoundException {
        String[][] nodes = new String[MAX_NUMBER_NODES][N_MAX_COLUMN_FILE_ONE];
        double[][] adjacencyMatrix = new double[MAX_NUMBER_NODES][MAX_NUMBER_NODES];
        int maximumNumBranches = (int) computeMaximumNumberOfBranches(MAX_NUMBER_NODES);
        String[][] branches = new String[maximumNumBranches][MAX_NUMBER_COLUMN_BRANCH_MATRIX];
        int numExistingNodes = 0, numExistingBranches = 0;
        verifyPath(args, nodes, adjacencyMatrix, numExistingNodes, numExistingBranches, branches);
    }

    public static void verifyPath(String[] args, String[][] nodes, double[][] adjacencyMatrix, int numExistingNodes, int numExistingBranches, String[][] branches) throws FileNotFoundException {
        if (args[0].equals("-n")) {
            menuPath(args, nodes, numExistingNodes, numExistingBranches, adjacencyMatrix, branches);
        } else {
            if (args[0].equals("-t")) {
                verifyTPath(args, nodes, numExistingNodes, numExistingBranches, adjacencyMatrix, branches);
            } else {
                System.out.println("\nO primeiro argumento é inválido. Verifique o comando introduzido.\n");
                System.exit(0);
            }
        }
    }

    public static void verifyTPath(String[] args, String[][] nodes, int numExistingNodes, int numExistingBranches, double[][] adjacencyMatrix, String[][] branches) throws FileNotFoundException {
        if (args[1].equals("-k")) {
            verifyKValue(args[2]);
            outputPath(args, nodes, numExistingNodes, numExistingBranches, adjacencyMatrix, branches);
        } else {
            System.out.println("\nO segundo argumento é inválido. Verifique o comando introduzido.\n");
            System.exit(0);
        }
    }

    public static void menuPath(String[] args, String[][] nodes, int numExistingNodes, int numExistingBranches, double[][] adjacencyMatrix, String[][] branches) throws FileNotFoundException {
        String nodesFileName = args[NODESFILE_ARGUMENT_POSITION_N];
        String branchesFileName = args[BRANCHESFILE_ARGUMENT_POSITION_N];
        numExistingNodes = readNodesInfo(nodesFileName, nodes, numExistingNodes);
        numExistingBranches = readBranchesInfo(args, branchesFileName, numExistingNodes, numExistingBranches, nodes, adjacencyMatrix, branches);
        mainLoop(adjacencyMatrix, numExistingNodes, numExistingBranches, nodes, branches);
    }

    public static void outputPath(String[] args, String[][] nodes, int numExistingNodes, int numExistingBranches, double[][] adjacencyMatrix, String[][] branches) throws FileNotFoundException {
        String nodesFileName = args[NODESFILE_ARGUMENT_POSITION_T];
        String branchesFileName = args[BRANCHESFILE_ARGUMENT_POSITION_T];
        String socialNetwork = obtainSocialNetwork(nodesFileName);
        numExistingNodes = readNodesInfo(nodesFileName, nodes, numExistingNodes);
        numExistingBranches = readBranchesInfo(args, branchesFileName, numExistingNodes, numExistingBranches, nodes, adjacencyMatrix, branches);
        String nameFile = createNameFile(socialNetwork);
        openOutputFile(nameFile, args, adjacencyMatrix, numExistingNodes, numExistingBranches, nodes, socialNetwork, branches);
    }

    public static void openOutputFile(String nameFile, String[] args, double[][] adjacencyMatrix, int numExistingNodes, int numExistingBranches, String[][] nodes, String socialNetwork, String[][] branches) throws FileNotFoundException {
        Formatter out = new Formatter(new File(nameFile + ".txt"));
        Matrix adjacencyMatrixAsMatrix = new Basic2DMatrix(adjacencyMatrix);
        int multiplicity = Integer.parseInt(args[POWER_ARGUMENT_POSITION]);
        out.format("%30s%n%n%n", "Informações sobre a rede social " + socialNetwork + " :");
        System.out.println("\n\nAguarde um pouco. A informação está a ser carregada...\n\n");
        downloadInfoToOutputFile(out, nodes, numExistingNodes, numExistingBranches, adjacencyMatrix, multiplicity, adjacencyMatrixAsMatrix, branches);
        out.close();
        System.out.println("Dados carregados para o ficheiro " + nameFile + ".txt" + " com sucesso!\n");
    }

    public static void downloadInfoToOutputFile(Formatter out, String[][] nodes, int numExistingNodes, int numExistingBranches, double[][] adjacencyMatrix, int multiplicity, Matrix adjacencyMatrixAsMatrix, String[][] branches) {
        printNodes(out, nodes, numExistingNodes);
        out.format("%n");
        printBranches(out, branches, numExistingBranches, numExistingNodes);
        out.format("%n");
        double[] nodeDegreeMatrix = nodeDegree(out, adjacencyMatrix, numExistingNodes, nodes);
        out.format("%n");
        eigenVectorCentrality(out, adjacencyMatrixAsMatrix, numExistingNodes, nodes);
        out.format("%n");
        averageDegree(out, numExistingNodes, nodeDegreeMatrix);
        out.format("%n");
        printDensity(out, numExistingNodes, numExistingBranches);
        out.format("%n");
        powerTheAdjacencyMatrix(out, adjacencyMatrix, multiplicity, numExistingNodes, nodes);
    }

    public static String obtainSocialNetwork(String nodesFileName) {
        String[] aux = nodesFileName.split("_");
        String socialNetwork = aux[1];
        return socialNetwork;
    }

    public static boolean test_obtainSocialNetework(String nodesFileName, String expectedResult) {
        String result = obtainSocialNetwork(nodesFileName);

        return result.equalsIgnoreCase(expectedResult);
    }

    public static String createNameFile(String socialNetwork) {
        String actualDate = getAtualDate();
        String nameFile = "out" + "_" + socialNetwork + "_" + actualDate;
        return nameFile;
    }

    public static boolean test_createNameFile(String socialNetwork, String expectedResult) {
        String result = createNameFile(socialNetwork);

        return result.equalsIgnoreCase(expectedResult);
    }

    public static String getAtualDate() {
        Calendar today = Calendar.getInstance();
        String day = Integer.toString(today.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(today.get(Calendar.MONTH) + 1);
        String year = Integer.toString(today.get(Calendar.YEAR));
        String atualDate = day + month + year;
        return atualDate;
    }

    public static boolean test_getAtualDate(String expectedResult) {
        String result = getAtualDate();

        return result.equalsIgnoreCase(expectedResult);
    }

    public static void verifyKValue(String value) {
        if (verifyIfKValueIsNumber(value)) {
            System.out.println("\n\nO valor de k (expoente da matriz) é válido. O programa pode proceder.\n");
        } else {
            System.out.println("\n\nO valor de k introduzido não é válido.\nApenas pode introduzir números naturais.");
            System.out.println("Valor de k inserido: " + value + "\n");
            System.exit(0);
        }
    }

    public static boolean verifyIfKValueIsNumber(String value) {
        if (value.matches("^[0-9]*$")) {
            int valueone = Integer.parseInt(value);
            return valueone > 0;
        }
        return false;
    }

    public static boolean test1_verifyIfKValueIsNumber() {
        String value = "123";
        boolean result = verifyIfKValueIsNumber(value);

        boolean expResult = true;

        return result == expResult;
    }

    public static boolean test2_verifyIfKValueIsNumber() {
        String value = "asd";
        boolean result = verifyIfKValueIsNumber(value);

        boolean expResult = false;

        return result == expResult;
    }

    public static void printMenu() {
        System.out.println("Análise de Redes Sociais | " + printDateForMenu());
        System.out.println("1 - Imprimir a matriz de adjacências.");
        System.out.println("2 - Imprimir os nós da rede social e os respetivos ids.");
        System.out.println("3 - Imprimir os ramos da rede social.");
        System.out.println("4 - Calcular o grau de cada nó.");
        System.out.println("5 - Calcular a centralidade de cada nó.");
        System.out.println("6 - Calcular o grau médio dos nós.");
        System.out.println("7 - Calcular a densidade da rede social.");
        System.out.println("8 - Calcular a potência das matrizes de adjacências.");
        System.out.println();
        System.out.println("0 - Sair");
    }

    public static void mainLoop(double[][] adjacencyMatrix, int numExistingNodes, int numExistingBranches, String[][] nodes, String[][] branches) throws FileNotFoundException {
        double[] nodeDegreeMatrix = new double[numExistingNodes];
        Formatter formatter = new Formatter(System.out);
        Scanner input = new Scanner(System.in);
        String opt;
        do {
            System.out.println("\n");
            printMenu();
            System.out.println();
            formatter.format("\nEscolha uma opção: ");
            opt = input.nextLine();
            System.out.println();

            switch (opt) {
                case "1":
                    System.out.println("\nMatriz de adjacências:\n\n");
                    printAdjacencyMatrix(formatter, numExistingNodes, nodes, adjacencyMatrix);
                    break;
                case "2":
                    printNodes(formatter, nodes, numExistingNodes);
                    break;
                case "3":
                    printBranches(formatter, branches, numExistingBranches, numExistingNodes);
                    break;
                case "4":
                    nodeDegreeMatrix = nodeDegree(formatter, adjacencyMatrix, numExistingNodes, nodes);
                    break;
                case "5":
                    System.out.println("\n\nAguarde um pouco. A informação está a ser carregada...\n\n");
                    Matrix adjacencyMatrixAsMatrix = new Basic2DMatrix(adjacencyMatrix);
                    eigenVectorCentrality(formatter, adjacencyMatrixAsMatrix, numExistingNodes, nodes);
                    break;
                case "6":
                    averageDegree(formatter, numExistingNodes, nodeDegreeMatrix);
                    break;
                case "7":
                    printDensity(formatter, numExistingNodes, numExistingBranches);
                    break;
                case "8":
                    int multiplicity = verifyIfMultiplicityIsValid();
                    powerTheAdjacencyMatrix(formatter, adjacencyMatrix, multiplicity, numExistingNodes, nodes);
                    break;
                case "0":
                    opt = askUserIfHeWantsToCloseProgram(adjacencyMatrix, nodes);
                    break;
                default:
                    formatter.format("\nOpção inválida. Tente novamente\n");
            }
        } while (!opt.equals("0"));
    }

    //UtilitÃ¡rios:
    public static String askUserIfHeWantsToCloseProgram(double[][] adjacencyMatrix, String[][] nodes) throws FileNotFoundException {
        String opt;
        do {
            opt = askForChoice("Tem a certeza que pretende sair?\n");
            opt = opt.toLowerCase();
            opt = checkUserAnswer(opt);
        } while (!opt.equals("n"));
        opt = "1";
        return opt;
    }

    public static boolean test_askUserIfHeWantsToCloseProgram(double[][] adjacencyMatrix, String[][] nodes, String expResult) throws FileNotFoundException {
        String result = askUserIfHeWantsToCloseProgram(adjacencyMatrix, nodes);

        return result.equalsIgnoreCase(expResult);
    }

    public static String askForChoice(String mesage) {
        Scanner in = new Scanner(System.in);
        System.out.println(mesage + " (Responda 'S' (Sim) ou 'N'(Não))");
        String answer = in.nextLine();
        return answer;
    }

    public static boolean test_askForChoice(String mesage, String expResult) {
        String result = askForChoice(mesage);

        return result.equalsIgnoreCase(expResult);
    }

    public static int readNodesInfo(String nodesFileName, String[][] nodes, int numExistingNodes) throws FileNotFoundException {
        try {
            numExistingNodes = openNodesFile(nodesFileName, numExistingNodes, nodes);
            System.out.println("\n\nFicheiro dos nós lido com sucesso.\n\n");
        } catch (FileNotFoundException ex) {
            System.out.printf("Surgiu um problema ao tentar ler o ficheiro dos nós : %s\n", ex.getMessage());
            System.exit(0);
        }
        verifyIfNumExistingNodesEqualsMaxNumberNodes(numExistingNodes);
        return numExistingNodes;
    }

    public static int openNodesFile(String nodesFileName, int numExistingNodes, String[][] nodes) throws FileNotFoundException {
        Scanner nodesFile = new Scanner(new File(nodesFileName));
        skipFilesHeader(nodesFile);
        while (nodesFile.hasNext() && numExistingNodes < MAX_NUMBER_NODES) {
            String line = nodesFile.nextLine();
            if ((line.trim()).length() > 0) {
                numExistingNodes = verifyNodesFileErrors(line, nodes, numExistingNodes);
            }
        }
        nodesFile.close();
        return numExistingNodes;
    }

    public static void verifyIfNumExistingNodesEqualsMaxNumberNodes(int numExistingNodes) {
        if (numExistingNodes == MAX_NUMBER_NODES) {
            System.out.println("Número limite de nós ultrapassado.");
            System.out.println("Verifique novamente o ficheiro.");
            System.exit(0);
        }
    }

    public static void printAdjacencyMatrix(Formatter formatter, int numExistingNodes, String[][] nodes, double[][] adjacencyMatrix) {
        for (int i = 0; i < numExistingNodes; i++) {
            formatter.format("\t" + nodes[i][0]);
        }
        formatter.format("%n");
        for (int i = 0; i < numExistingNodes; i++) {
            formatter.format(nodes[i][0] + "\t");
            for (int j = 0; j < numExistingNodes; j++) {
                formatter.format((int) adjacencyMatrix[i][j] + "\t");
            }
            formatter.format("%n");
        }
    }

    public static int verifyNodesFileErrors(String line, String[][] nodes, int numExistingNodes) {
        int numErrors = 0;
        String[] aux = line.split(",");
        checkNumberOfColumns(aux, N_MAX_COLUMN_FILE_ONE);
        numErrors = checkIfIDAlreadyExists(aux, numExistingNodes, nodes, numErrors);
        numErrors = checkIfNodeAlreadyExists(aux, numExistingNodes, nodes, numErrors);
        closeProgramIfDetectErrors(numErrors, "nós");
        numExistingNodes = addNode(nodes, numExistingNodes, aux);
        return numExistingNodes;
    }

    public static boolean test_verifyFileOneErrors(String line, String[][] nos, int numExistingNodes, int expResult) {
        int result = verifyNodesFileErrors(line, nos, numExistingNodes);

        return result == expResult;
    }

    public static int checkIfNodeAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors) {
        if (checkInfoNodesFile(aux[1], numExistingNodes, nodes, 1)) {
            numErrors = showErrorMesage("O nó " + aux[1] + " já existe.", numErrors);
        }
        return numErrors;
    }

    public static boolean test1_checkIfNodeAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors, int expResult) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i][1] = aux[1] + "lol";
        }
        int result = checkIfIDAlreadyExists(aux, numExistingNodes, nodes, numErrors);

        return result == expResult;
    }

    public static boolean test2_checkIfNodeAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors, int expResult) {
        nodes[0][1] = aux[1];
        int result = checkIfIDAlreadyExists(aux, numExistingNodes, nodes, numErrors);

        return result == expResult;
    }

    public static int checkIfIDAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors) {
        if (checkInfoNodesFile(aux[0], numExistingNodes, nodes, 0)) {
            numErrors = showErrorMesage("O id " + aux[0] + " já existe.", numErrors);
        }
        return numErrors;
    }

    public static boolean test1_checkIfIDAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors, int expResult) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i][0] = aux[0] + "lol";
        }
        int result = checkIfIDAlreadyExists(aux, numExistingNodes, nodes, numErrors);

        return result == expResult;
    }

    public static boolean test2_checkIfIDAlreadyExists(String[] aux, int numExistingNodes, String[][] nodes, int numErrors, int expResult) {
        nodes[0][0] = aux[0];
        int result = checkIfIDAlreadyExists(aux, numExistingNodes, nodes, numErrors);

        return result == expResult;
    }

    public static void checkNumberOfColumns(String[] aux, int i) {
        if (aux.length != i) {
            System.out.println("O número de colunas é diferente de " + i + ".");
            System.out.println("Verifique novamente o ficheiro.");
            System.exit(0);
        }
    }

    public static int showErrorMesage(String mesage, int numErrors) {
        Formatter formatter = new Formatter(System.out);
        formatter.format("%n%s%n", mesage);
        numErrors++;
        return numErrors;
    }

    public static boolean test_showErrorMesage(String mesage, int numErrors, int expResult) {
        int result = showErrorMesage(mesage, numErrors);

        return result == expResult;
    }

    public static int addNode(String[][] nodes, int numExistingNodes, String[] aux) {
        nodes[numExistingNodes][0] = aux[0];
        nodes[numExistingNodes][1] = aux[1];
        numExistingNodes++;
        return numExistingNodes;
    }

    public static boolean test_addNode(String[][] nodes, int numExistingNodes, String[] aux, int expResult) {
        int result = addNode(nodes, numExistingNodes, aux);

        return result == expResult;
    }

    public static boolean checkInfoNodesFile(String info, int numExistingNodes, String[][] nodes, int column) {
        for (int i = 0; i < numExistingNodes; i++) {
            if (nodes[i][column].equals(info)) {
                return true;
            }
        }
        return false;
    }

    public static boolean test1_checkInfoNodesFile(String info, int numExistingNodes, String[][] nodes, int column, boolean expResult) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i][column] = info + "asd";
        }
        boolean result = checkInfoNodesFile(info, numExistingNodes, nodes, column);

        return result == expResult;
    }

    public static boolean test2_checkInfoNodesFile(String info, int numExistingNodes, String[][] nodes, int column, boolean expResult) {
        nodes[0][0] = info;
        boolean result = checkInfoNodesFile(info, numExistingNodes, nodes, column);

        return result == expResult;
    }

    public static int readBranchesInfo(String[] args, String branchesFileName, int numExistingNodes, int numExistingBranches, String[][] nodes, double[][] adjacencyMatrix, String[][] branch) throws FileNotFoundException {
        if (numExistingNodes == 0) {
            System.out.println("\nNão existem nós. Verifique novamente o ficheiro dos nós.\n");
            System.exit(0);
        } else {
            try {
                numExistingBranches = openBranchesFile(branchesFileName, args, numExistingNodes, numExistingBranches, nodes, adjacencyMatrix, branch);
                System.out.println("\n\nFicheiro dos ramos lido com sucesso.\n\n");
            } catch (FileNotFoundException ex) {
                System.out.printf("Surgiu um problema ao tentar ler o ficheiro dos ramos : %s\n", ex.getMessage());
                System.exit(0);
            }
        }
        return numExistingBranches;
    }

    public static int openBranchesFile(String branchesFileName, String[] args, int numExistingNodes, int numExistingBranches, String[][] nodes, double[][] adjacencyMatrix, String[][] branch) throws FileNotFoundException {
        int invalidBranches = 0;
        Scanner readBranchesInfo = new Scanner(new File(branchesFileName));
        skipFilesHeader(readBranchesInfo);
        while (readBranchesInfo.hasNext()) {
            String line = readBranchesInfo.nextLine();
            if ((line.trim()).length() > 0) {
                invalidBranches = addBranch(args, line, branchesFileName, nodes, numExistingNodes, adjacencyMatrix, invalidBranches, branch);
                numExistingBranches++;
            }
        }
        numExistingBranches = numExistingBranches - invalidBranches;
        readBranchesInfo.close();
        return numExistingBranches;
    }

    public static void skipFilesHeader(Scanner readInfo) {
        readInfo.nextLine();
    }

    public static int addBranch(String[] args, String line, String branchesFileName, String[][] nodes, int numExistingNodes, double[][] adjacencyMatrix, int invalidBranches, String[][] branch) throws FileNotFoundException {
        String[] aux = line.split(",");
        int indexLine = findId(aux[0], nodes, numExistingNodes);
        int indexColumn = findId(aux[1], nodes, numExistingNodes);
        invalidBranches = verifyBranchesFileErrors(args, indexLine, indexColumn, aux, adjacencyMatrix, invalidBranches, branch);
        return invalidBranches;
    }

    public static boolean test_addBranch(String[] args, String line, String branchesFileName, String[][] nodes, int numExistingNodes, double[][] adjacencyMatrix, int invalidBranches, String[][] branch, int expResult) throws FileNotFoundException {
        int result = addBranch(args, line, branchesFileName, nodes, numExistingNodes, adjacencyMatrix, invalidBranches, branch);

        return result == expResult;
    }

    public static int verifyBranchesFileErrors(String[] args, int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix, int invalidBranches, String[][] branch) {
        int numErrors = 0;
        checkNumberOfColumns(aux, N_MAX_COLUMN_FILE_TWO);
        numErrors = checkSelfConnectedBranches(aux, numErrors);
        numErrors = checkBranchLength(aux, numErrors);
        numErrors = checkDuplicatedNodes(indexLine, indexColumn, aux, numErrors);
        closeProgramIfDetectErrors(numErrors, "ramos");
        invalidBranches = checkDuplicatedBranches(args, indexLine, indexColumn, aux, adjacencyMatrix, invalidBranches, branch);
        addBranchToMatrix(indexLine, indexColumn, aux, adjacencyMatrix);
        return invalidBranches;
    }

    public static boolean test_verifyBranchesFileErrors(String[] args, int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix, int invalidBranches, String[][] branch, int expResult) {
        int result = verifyBranchesFileErrors(args, indexLine, indexColumn, aux, adjacencyMatrix, invalidBranches, branch);

        return result == expResult;
    }

    public static void addBranchToMatrix(int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix) {
        adjacencyMatrix[indexLine][indexColumn] = Integer.parseInt(aux[2]);
        adjacencyMatrix[indexColumn][indexLine] = Integer.parseInt(aux[2]);
    }

    public static int checkDuplicatedNodes(int indexLine, int indexColumn, String[] aux, int numErrors) {
        if (indexLine == -1) {
            numErrors = showErrorMesage("O id " + aux[0] + " presente no ficheiro não existe.", numErrors);
        }
        if (indexColumn == -1) {
            numErrors = showErrorMesage("O id " + aux[1] + " presente no ficheiro não existe.", numErrors);
        }
        return numErrors;
    }

    public static boolean test1_checkDuplicatedNodes(int indexLine, int indexColumn, String[] aux, int numErrors, int expResult) {
        indexLine = -1;
        indexColumn = -1;
        int result = checkDuplicatedNodes(indexLine, indexColumn, aux, numErrors);

        return result == expResult;
    }

    public static boolean test2_checkDuplicatedNodes(int indexLine, int indexColumn, String[] aux, int numErrors, int expResult) {
        indexLine = -2;
        indexColumn = -2;
        int result = checkDuplicatedNodes(indexLine, indexColumn, aux, numErrors);

        return result == expResult;
    }

    public static void closeProgramIfDetectErrors(int numErrors, String fileType) {
        if (numErrors > 0) {
            Formatter formatter = new Formatter(System.out);
            formatter.format("%s%n", "Verifique novamente o ficheiro dos " + fileType + ".");
            System.exit(0);
        }
    }

    public static int checkBranchLength(String[] aux, int numErrors) {
        if (!aux[2].equals("1")) {
            numErrors = showErrorMesage("O comprimento do ramo " + aux[0] + " -> " + aux[1] + " é diferente de 1.", numErrors);
        }
        return numErrors;
    }

    public static boolean test1_checkBranchLength(String[] aux, int numErrors, int expResult) {
        aux[2] = "1";
        int result = checkBranchLength(aux, numErrors);

        return result == expResult;
    }

    public static boolean test2_checkBranchLength(String[] aux, int numErrors, int expResult) {
        aux[2] = "asdas";
        int result = checkBranchLength(aux, numErrors);

        return result == expResult;
    }

    public static int checkDuplicatedBranches(String args[], int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix, int invalidBranches, String[][] branch) {
        if (adjacencyMatrix[indexLine][indexColumn] == 1) {
            String answer;
            invalidBranches++;
            if (args[0].equals("-n")) {
                do {
                    answer = askForChoice("\nO ramo: " + aux[0] + " -> " + aux[1] + " está duplicado. Deseja sair?");
                    answer = answer.toLowerCase();
                    answer = checkUserAnswer(answer);
                } while (!answer.equals("n"));
            }
        } else {
            addBranchToBranchMatrix(branch, aux);
        }
        return invalidBranches;
    }

    public static boolean test1_checkDuplicatedBranches(String args[], int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix, int invalidBranches, String[][] branch, int expResult) {
        adjacencyMatrix[indexLine][indexColumn] = 1;
        int result = checkDuplicatedBranches(args, indexLine, indexColumn, aux, adjacencyMatrix, invalidBranches, branch);

        return result == expResult;
    }

    public static boolean test2_checkDuplicatedBranches(String args[], int indexLine, int indexColumn, String[] aux, double[][] adjacencyMatrix, int invalidBranches, String[][] branch, int expResult) {
        adjacencyMatrix[indexLine][indexColumn] = 2;
        int result = checkDuplicatedBranches(args, indexLine, indexColumn, aux, adjacencyMatrix, invalidBranches, branch);

        return result == expResult;
    }

    public static void addBranchToBranchMatrix(String[][] branch, String[] aux) {
        for (int line = 0; line < branch.length; line++) {
            if (branch[line][0] == null) {
                branch[line][0] = aux[0];
                branch[line][1] = aux[1];
                break;
            }
        }
    }

    public static String checkUserAnswer(String answer) {
        switch (answer) {
            case "n":
                break;
            case "s":
                System.exit(0);
                break;
            default:
                System.out.println("\n\nResposta inválida.\nResponda apenas com 'S' ou 'N'.\n\n");
        }
        return answer;
    }

    public static int checkSelfConnectedBranches(String[] aux, int numErrors) {
        if (aux[0].equals(aux[1])) {
            numErrors = showErrorMesage("O nó com o id " + aux[0] + " conecta-se a si mesmo.", numErrors);
        }
        return numErrors;
    }

    public static boolean test1_checkSelfConnectedBranches(String[] aux, int numErrors, int expResult) {
        aux[0] = aux[1] + "asdasdasd";
        int result = checkSelfConnectedBranches(aux, numErrors);

        return result == expResult;
    }

    public static boolean test2_checkSelfConnectedBranches(String[] aux, int numErrors, int expResult) {
        aux[0] = aux[1];
        int result = checkSelfConnectedBranches(aux, numErrors);

        return result == expResult;
    }

    public static int findId(String id, String[][] nodes, int numExistingNodes) {
        for (int index = 0; index < numExistingNodes; index++) {
            if (nodes[index][0].equals(id)) {
                return index;
            }
        }
        return -1;
    }

    public static boolean test1_findId(String id, String[][] nodes, int numExistingNodes, int expResult) {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 1; j < nodes[i].length; j++) {
                nodes[i][j] = id + "dsfs";
            }
        }
        int result = findId(id, nodes, numExistingNodes);

        return result == expResult;
    }

    public static boolean test2_findId(String id, String[][] nodes, int numExistingNodes, int expResult) {
        nodes[0][0] = id;
        int result = findId(id, nodes, numExistingNodes);

        return result == expResult;
    }

    public static double[] nodeDegree(Formatter formatter, double[][] adjacencyMatrix, int numExistingNodes, String[][] nodes) {
        double[] nodeDegreeMatrix = new double[numExistingNodes];
        for (int line = 0; line < numExistingNodes; line++) {
            double nodeDegree = calculateNodeDegree(adjacencyMatrix, line);
            nodeDegreeMatrix[line] = nodeDegree;
        }
        showNodeDegreeMatrix(formatter, nodeDegreeMatrix, nodes);
        return nodeDegreeMatrix;
    }

    public static boolean test_nodeDegree(Formatter formatter, double[][] adjacencyMatrix, int numExistingNodes, String[][] nodes, double[] expResult) {
        double[] result = nodeDegree(formatter, adjacencyMatrix, numExistingNodes, nodes);

        return result == expResult;
    }

    public static double calculateNodeDegree(double[][] adjacencyMatrix, int line) {
        double nodeDegree = 0;
        for (int column = 0; column < adjacencyMatrix[line].length; column++) {
            nodeDegree = nodeDegree + adjacencyMatrix[line][column];
        }
        return nodeDegree;
    }

    public static boolean test_calculateNodeDegree(double[][] adjacencyMatrix, int line, double expResult) {
        double result = calculateNodeDegree(adjacencyMatrix, line);

        return result == expResult;
    }

    public static void showNodeDegreeMatrix(Formatter formatter, double[] nodeDegreeMatrix, String[][] nodes) {
        formatter.format("%n%s%n%n", "Grau de cada nó:");
        for (int line = 0; line < nodeDegreeMatrix.length; line++) {
            formatter.format("%s%n", "O grau de " + nodes[line][1] + " é: " + nodeDegreeMatrix[line]);
        }
    }

    public static void eigenVectorCentrality(Formatter formatter, Matrix adjacencyMatrixAsMatrix, int numExistingNodes, String[][] nodes) {
        EigenDecompositor eigenD = new EigenDecompositor(adjacencyMatrixAsMatrix);
        double eigenVectorCentralityMatrix[] = new double[numExistingNodes];
        Matrix decomposedMatrix[] = eigenD.decompose();
        double eigenVectorMatrix[][] = decomposedMatrix[0].toDenseMatrix().toArray();
        double eigenValueMatrix[][] = decomposedMatrix[1].toDenseMatrix().toArray();
        double maxEigenValue = findMaxEigenValue(eigenValueMatrix);
        int maxEigenValueIndexColumn = findMaxEigenValueIndexColumn(maxEigenValue, eigenValueMatrix);
        addEigenVectorToMatrix(maxEigenValueIndexColumn, eigenVectorMatrix, eigenVectorCentralityMatrix);
        showEigenVectorCentralityMatrix(formatter, eigenVectorCentralityMatrix, nodes);
    }

    public static double findMaxEigenValue(double[][] eigenValueMatrix) {
        double maxEigenValue = eigenValueMatrix[0][0];
        for (double[] eigenValueMatrix1 : eigenValueMatrix) {
            for (int column = 0; column < eigenValueMatrix1.length; column++) {
                if (eigenValueMatrix1[column] > maxEigenValue) {
                    maxEigenValue = eigenValueMatrix1[column];
                }
            }
        }
        return maxEigenValue;
    }

    public static boolean test1_findMaxEigenValue(double[][] eigenValueMatrix, double expResult) {
        eigenValueMatrix[0][1] = expResult;
        double result = findMaxEigenValue(eigenValueMatrix);

        return result == expResult;
    }

    public static boolean test2_findMaxEigenValue(double[][] eigenValueMatrix, double expResult) {
        for (int i = 0; i < eigenValueMatrix.length; i++) {
            for (int j = 1; j < eigenValueMatrix[i].length; j++) {
                eigenValueMatrix[i][j] = eigenValueMatrix[0][0] - 1;
            }
        }
        double result = findMaxEigenValue(eigenValueMatrix);

        return result == expResult;
    }

    public static int findMaxEigenValueIndexColumn(double maxEigenValue, double[][] eigenValueMatrix) {
        for (double[] eigenValueMatrix1 : eigenValueMatrix) {
            for (int column = 0; column < eigenValueMatrix1.length; column++) {
                if (eigenValueMatrix1[column] == maxEigenValue) {
                    return column;
                }
            }
        }
        return -1;
    }

    public static boolean test1_findMaxEigenValueIndexColumn(double maxEigenValue, double[][] eigenValueMatrix, int expResult) {
        eigenValueMatrix[0][0] = maxEigenValue;
        int result = findMaxEigenValueIndexColumn(maxEigenValue, eigenValueMatrix);

        return result == expResult;
    }

    public static boolean test2_findMaxEigenValueIndexColumn(double maxEigenValue, double[][] eigenValueMatrix) {
        for (int i = 0; i < eigenValueMatrix.length; i++) {
            for (int j = 0; j < eigenValueMatrix[i].length; j++) {
                eigenValueMatrix[i][j] = maxEigenValue - 1;
            }
        }
        int result = findMaxEigenValueIndexColumn(maxEigenValue, eigenValueMatrix);

        return result == -1;
    }

    public static void addEigenVectorToMatrix(int maxEigenValueIndexColumn, double[][] eigenVectorMatrix, double[] eigenVectorCentralityMatrix) {
        for (int line = 0; line < eigenVectorCentralityMatrix.length; line++) {
            eigenVectorCentralityMatrix[line] = eigenVectorMatrix[line][maxEigenValueIndexColumn];
        }
    }

    public static void showEigenVectorCentralityMatrix(Formatter formatter, double[] eigenVectorCentralityMatrix, String[][] nodes) {
        formatter.format("%n%s%n%n", "Centralidade de cada nó:");
        for (int line = 0; line < eigenVectorCentralityMatrix.length; line++) {
            String eigenVectorCentrality = String.format("%.3f", abs(eigenVectorCentralityMatrix[line]));
            formatter.format("%s%n", "A centralidade de " + nodes[line][1] + " é: " + eigenVectorCentrality);
        }
    }

    public static void averageDegree(Formatter formatter, int numExistingNodes, double[] nodeDegreeMatrix) {
        double n = numExistingNodes;
        double sum = 0;
        for (int line = 0; line < n; line++) {
            sum = sum + nodeDegreeMatrix[line];
        }
        double averageDegree = (1 / n) * sum;
        String average = String.format("%.3f", averageDegree);
        formatter.format("%n%s%n", "O grau médio de todos os nós é: " + average + ".");
    }

    public static String askForMultiplicity() {
        Formatter formatter = new Formatter(System.out);
        Scanner in = new Scanner(System.in);
        formatter.format("%s%n%n", "Qual é o expoente da matriz de adjacências?");
        String multiplicity = in.nextLine();
        return multiplicity;
    }

    public static boolean test_askForMultiplicity(String expResult) {
        String result = askForMultiplicity();

        return result.equalsIgnoreCase(expResult);
    }

    public static int verifyIfMultiplicityIsValid() {
        String multiplicityTest = askForMultiplicity();
        while (!verifyIfKValueIsNumber(multiplicityTest)) {
            System.out.println("\nOpção inválida. Insira apenas números naturais.\n");
            multiplicityTest = askForMultiplicity();
        }
        int multiplicity = Integer.parseInt(multiplicityTest);
        return multiplicity;
    }

    public static boolean test_verifyIfMultiplicityIsValid(int expResult) {
        int result = verifyIfMultiplicityIsValid();

        return result == expResult;
    }

    public static void powerTheAdjacencyMatrix(Formatter formatter, double[][] adjacencyMatrix, int multiplicity, int numExistingNodes, String[][] nodes) {
        double[][] resultantAdjacencyMatrix = adjacencyMatrix.clone();
        formatter.format("%n%s%n%n", "Matriz de adjacências elevada a (k): 1");
        printAdjacencyMatrix(formatter, numExistingNodes, nodes, adjacencyMatrix);
        for (int i = 0; i < multiplicity - 1; i++) {
            printHeader(formatter, i + 2);
            resultantAdjacencyMatrix = multiplyMatrices(adjacencyMatrix, resultantAdjacencyMatrix);
            printAdjacencyMatrix(formatter, numExistingNodes, nodes, resultantAdjacencyMatrix);
            emptyLine();
        }
    }

    public static double[][] multiplyMatrices(double[][] adjacencyMatrix, double[][] resultantAdjacencyMatrix) {
        double[][] auxiliarMatrix = new double[adjacencyMatrix.length][adjacencyMatrix[0].length];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                for (int n = 0; n < adjacencyMatrix[0].length; n++) {
                    auxiliarMatrix[i][j] += adjacencyMatrix[i][n] * resultantAdjacencyMatrix[n][j];
                }
            }
        }
        return auxiliarMatrix;
    }

    public static boolean test_multiplyMatrices(double[][] adjacencyMatrix, double[][] resultantAdjacencyMatrix, double[][] expResult) {
        double[][] result = multiplyMatrices(adjacencyMatrix, resultantAdjacencyMatrix);

        return result == expResult;
    }

    public static void printHeader(Formatter formatter, int multiplicity) {
        formatter.format(" ");
        formatter.format("%n%s%n%n", "Matriz de adjacências elevada a (k): " + multiplicity);
    }

    public static void emptyLine() {
        System.out.println();
    }

    public static void printDensity(Formatter formatter, int numExistingBranches, int numExistingNodes) {
        double maximumNumBranches = computeMaximumNumberOfBranches(numExistingNodes);
        String density = getNetworkDensity(numExistingBranches, maximumNumBranches);
        formatter.format("%s%n", "A densidade da rede social é: " + density + ".");
    }

    public static double computeMaximumNumberOfBranches(int numExistingNodes) {
        double maximumNumBranches = numExistingNodes * (numExistingNodes - 1) * (0.5);
        return maximumNumBranches;
    }

    public static boolean test_computeMaximumNumberOfBranches(int numExistingNodes, double expResult) {
        double result = computeMaximumNumberOfBranches(numExistingNodes);

        return result == expResult;
    }

    public static String getNetworkDensity(double numExistingBranches, double maximumNumBranches) {
        double density = numExistingBranches / maximumNumBranches;
        String densityAsString = String.format("%.3f", density);
        return densityAsString;
    }

    public static boolean test_getNetworkDensity(double numExistingBranches, double maximumNumBranches, String expResult) {
        String result = getNetworkDensity(numExistingBranches, maximumNumBranches);

        return result.equalsIgnoreCase(expResult);
    }

    public static void printNodes(Formatter formatter, String[][] nodes, int numExistingNodes) {
        formatter.format("%s%n%n", "Lista dos nós da rede social e respetivos ids:");
        for (int line = 0; line < numExistingNodes; line++) {
            formatter.format("%s%n", "O nó com id " + nodes[line][0] + " tem como nome: " + nodes[line][1]);
        }
        formatter.format("%n%s%n", "O número de nós existente na rede social é: " + numExistingNodes + ".");
    }

    public static void printBranches(Formatter formatter, String[][] branches, int numExistingBranches, int numExistingNodes) {
        int maximumNumBranches = (int) computeMaximumNumberOfBranches(numExistingNodes);
        formatter.format("%n%s%n%n", "Lista dos ramos da rede social:");
        for (int line = 0; line < numExistingBranches; line++) {
            formatter.format("%s%n", branches[line][0] + " --> " + branches[line][1]);
        }
        formatter.format("%n%s%n", "O número total de ramos existente na rede social é: " + numExistingBranches + ".");
        formatter.format("%s%n", "O número máximo de ramos possível para esta rede é: " + maximumNumBranches + "." + "\n\n");
    }

    public static Date printDateForMenu() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return date;
    }
}
