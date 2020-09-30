package util;

import model.Hypergraph;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Load an input graph in memory
 */
public class GraphImport {

    private static final Logger LOGGER = Logger.getLogger(GraphImport.class);

    /**
     * load an input graph in memory
     *
     * @param datasetName dataset name
     * @param delim       seperate sybolm
     * @return a graph
     * @throws IOException
     */
    public static Hypergraph load(String datasetName, String delim) throws IOException {
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

            for (int i = 0; i < tokens.length; i++) {
                int node=Integer.parseInt(tokens[i]);
                newEdge.add(node);
                tempNodeList.add(node);
            }

            edgeList.add(newEdge);
        }

        LOGGER.info(" Graph edges read DONE!");

        //construct hypergraph
        HashSet<Integer> nodeSet = new HashSet<>(tempNodeList);
        ArrayList<Integer> nodeList = new ArrayList<>(nodeSet);

        return new Hypergraph(nodeList, edgeList);
    }

    public static void main(String[] args) throws IOException {

        System.out.println(load("test8.txt", "\t"));

    }
}
