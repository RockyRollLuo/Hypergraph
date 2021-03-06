import algorithm.Decomposition;
import algorithm.Decremental;
import algorithm.Incremental;
import javafx.beans.binding.IntegerBinding;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.DatasetPreprocess;
import util.FileIOUtils;
import util.SetOpt;
import util.SetOpt.Option;
import util.ToolUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainAllAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainAllAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result")
    public static int printResult = 1;

    @Option(abbr = 'm', usage = "the method of choosing dynamic edge")
    public static int method = 1;

    @Option(abbr = 'd', usage = "degree position, 0:low,1:avg,2:high")
    public static int degreePosition = 1;

    @Option(abbr = 'c', usage = "cardinality of dynamic edge")
    public static int cardinality = 3;

    @Option(abbr = 'e', usage = "corenumber of dynamic edge")
    public static int coreE = 5;

    @Option(abbr = 'c', usage = "whether to constructe nodeToEdgesMap, false:no, true:yes")
    public static boolean constructStructure = false;

    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainAllAlgorithm main = new MainAllAlgorithm();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("choose node degree position :" + ToolUtils.getDegreePosition(degreePosition));

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType),constructStructure);
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = new HashMap<>();
        if (constructStructure) {
            nodeToEdgesMap = hypergraph.getNodeToEdgesMap();
        } else {
            nodeToEdgesMap = FileIOUtils.loadNodeToEdgesMap(datasetName);
            hypergraph.setNodeToEdgesMap(nodeToEdgesMap);
        }
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeList.size());

        /*
        read decomposition full core file and compute coreEMap
         */
        String coreFile = "Decomposition_" + datasetName + "_full";
        HashMap<Integer, Integer> coreVMap = FileIOUtils.loadCoreFile(coreFile);
        HashMap<ArrayList<Integer>, Integer> coreEMap = hypergraph.computeCoreEMapByCoreVMap(edgeList, coreVMap);


         /*
        choose dynamic edge
         */
        ArrayList<Integer> e0 = new ArrayList<>();
        if(method==0){
            //1.degree distribution choose
            HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
            degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 0); //sorted nodes by degree descending
            int index = ToolUtils.getNodeIndexRand(degreePosition, nodeList.size());
            Integer node = (new ArrayList<Integer>(degreeMap.keySet())).get(index);
            e0 = ToolUtils.getRandomElement(nodeToEdgesMap.get(node));
            LOGGER.info("e0:" + e0.toString());
        }
        if (method == 1) {
            //2.cardinality choose
            HashMap<Integer, ArrayList<ArrayList<Integer>>> cariToEdgesMap = ToolUtils.getCardiToEdgesMap(edgeList);
            ArrayList<ArrayList<Integer>> cardiEdges = cariToEdgesMap.get(cardinality);
            int index = ToolUtils.getRandomInt(cardiEdges.size());
            e0 = cardiEdges.get(index);
        } else if (method == 2) {
            //3.core number choose
            HashMap<Integer, ArrayList<ArrayList<Integer>>> coreToEdgesMap = ToolUtils.getCoreToEdgesMap(coreEMap);
            ArrayList<ArrayList<Integer>> coreEdges = coreToEdgesMap.get(coreE);
            int index = ToolUtils.getRandomInt(coreEdges.size());
            e0 = coreEdges.get(index);
        }

        /*
        decremental
         */
        Decremental decremental = new Decremental(hypergraph, coreEMap, coreVMap, e0);
        Result result_dcremental = decremental.run();
        result_dcremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_dcremental, printResult);


        /*
        decomposition rest graph
         */
        Decomposition decomposition_rest = new Decomposition(hypergraph); //the hypergraph is already the REST by decremental
        Result result_decomposition_rest = decomposition_rest.run();
        result_decomposition_rest.setDatasetName(datasetName);
        result_decomposition_rest.setType("rest");
        FileIOUtils.writeCoreNumber(result_decomposition_rest, printResult);

        /*
        incremental
         */
        Incremental incremental = new Incremental(hypergraph, decremental.getCoreEMap(), decremental.getCoreVMap(), e0);
        Result result_incremental = incremental.run();
        result_incremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_incremental, printResult);


    }
}
