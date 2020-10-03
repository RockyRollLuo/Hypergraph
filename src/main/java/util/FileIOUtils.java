package util;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class FileIOUtils {

    private static final Logger LOGGER = Logger.getLogger(FileIOUtils.class);

    /**
     * load an input graph in memory
     *
     * @param datasetName dataset name
     * @param delim       seperate sybolm
     * @return a graph
     * @throws IOException
     */
    public static Hypergraph loadGraph(String datasetName, String delim) throws IOException {
        long startTime = System.nanoTime();
        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String path = "datasets" + pathSeparator + datasetName;

        LOGGER.info("Start loading graph: " + path);

        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();
        ArrayList<Integer> tempNodeList = new ArrayList<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment
                continue;
            }

            String[] tokens = line.split(delim);
            ArrayList<Integer> newEdge = new ArrayList<>();

            for (String token : tokens) {
                int node = Integer.parseInt(token);
                newEdge.add(node);
                tempNodeList.add(node);
            }

            edgeList.add(newEdge);
        }

        //construct hypergraph
        HashSet<Integer> nodeSet = new HashSet<>(tempNodeList);
        ArrayList<Integer> nodeList = new ArrayList<>(nodeSet);

        long endTime = System.nanoTime();
        LOGGER.info("TakenTime:"+(double) (endTime - startTime) / 1.0E9D);

        return new Hypergraph(nodeList, edgeList);
    }

    /**
     * read a core number file
     *
     * @param coreFile filename of core number
     * @return coreVMap
     * @throws IOException
     */
    public static HashMap<Integer, Integer> loadCoreFile(String coreFile) throws IOException {
        long startTime = System.nanoTime();
        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String path = "corenumber" + pathSeparator + coreFile;

        LOGGER.info("Start loading core_number_file: " + path);

        HashMap<Integer, Integer> coreVMap = new HashMap<>();

        //read edges
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) {
                continue;
            }

            String[] tokens = line.split("\t");
            Integer node = Integer.parseInt(tokens[0]);
            int coreness = Integer.parseInt(tokens[1]);
            coreVMap.put(node, coreness);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);

        return coreVMap;
    }

    /**
     * write the core number of nodes
     *
     * @param result coreMap
     * @throws IOException
     */
    public static void writeCoreNumber(Result result) throws IOException {
        long startTime = System.nanoTime();
        LOGGER.info("Start writing file... ");

        Hashtable<Object, Integer> output = (Hashtable<Object, Integer>) result.getOutput();
        double takenTime = result.getTakenTime();
        String algorithmName = result.getAlgorithmName();
        String datasetName = result.getDatasetName();
        String type = result.getType();

        //Operate System
        String pathSeparator = "\\";
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            pathSeparator = "/";
        }
        String fileName = "corenumber" + pathSeparator + algorithmName + "_" + datasetName + "_" + type;

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

        bw.write("# takenTime:" + takenTime + "us");
        bw.newLine();

        for (Object key : output.keySet()) {
            bw.write(key.toString() + "\t" + output.get(key));
            bw.newLine();
        }

        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info((double) (endTime - startTime) / 1.0E9D);
    }

}
