import algorithm.Decomposition;
import algorithm.Decremental;
import algorithm.Incremental;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.FileIOUtils;
import util.SetOpt;
import util.SetOpt.Option;
import util.ToolUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainBatchDynamic {
    private static final Logger LOGGER = Logger.getLogger(MainBatchDynamic.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'd', usage = "degree position, 0:low,1:avg,2:high")
    public static int degreePosition = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result")
    public static int printResult = 1;

    @Option(abbr = 'c', usage = "whether to constructe nodeToEdgesMap, false:no, true:yes")
    public static boolean constructStructure = true;

    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainBatchDynamic main = new MainBatchDynamic();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("choose node degree position :" + ToolUtils.getDegreePosition(degreePosition));

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType),constructStructure);
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeList.size());


        /*
        choose a set of dynamic edges
         */
        int dynamicNums = Integer.parseInt(args[1]);
        ArrayList<Integer> randIndexsList = ToolUtils.getRandomIndexsList(edgeList.size(), dynamicNums);
        ArrayList<ArrayList<Integer>> dynamicEdges = new ArrayList<>();
        for (int index : randIndexsList) {
            dynamicEdges.add(edgeList.get(index));
        }

        /*
        read decomposition full core file and compute coreEMap
         */
        String coreFile = "Decomposition_" + datasetName + "_full";
        HashMap<Integer, Integer> coreVMap = FileIOUtils.loadCoreFile(coreFile);
        HashMap<ArrayList<Integer>, Integer> coreEMap = hypergraph.computeCoreEMapByCoreVMap(edgeList, coreVMap);

        /*
        decremental
         */
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>(coreVMap);
        HashMap<ArrayList<Integer>, Integer> tempCoreEMap = new HashMap<>(coreEMap);

        //TODO:exist problem
        Result result_decremental = null;
        for (ArrayList<Integer> e0 : dynamicEdges) {
            Decremental decremental = new Decremental(hypergraph, tempCoreEMap, tempCoreVMap, e0);
            result_decremental = decremental.run();
            tempCoreVMap = decremental.getCoreVMap();
            tempCoreEMap = decremental.getCoreEMap();
        }
        result_decremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_decremental, printResult);

        /*
        decomposition rest graph
         */
        Decomposition decomposition_rest = new Decomposition(hypergraph);  //the hypergraph is already the REST by decremental
        Result result_decomposition_rest = decomposition_rest.run();
        result_decomposition_rest.setDatasetName(datasetName);
        result_decomposition_rest.setType("rest");
        FileIOUtils.writeCoreNumber(result_decomposition_rest, printResult);

        /*
        incremental
         */
        Result result_incremental = null;
        for (ArrayList<Integer> e0 : dynamicEdges) {
            Incremental incremental = new Incremental(hypergraph, tempCoreEMap, tempCoreVMap, e0);
            result_incremental = incremental.run();
            tempCoreVMap = incremental.getCoreVMap();
            tempCoreEMap = incremental.getCoreEMap();
        }
        result_incremental.setDatasetName(datasetName);
        FileIOUtils.writeCoreNumber(result_incremental, printResult);
    }
}
