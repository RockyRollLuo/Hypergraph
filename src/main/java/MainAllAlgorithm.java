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

public class MainAllAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainAllAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'd', usage = "degree position, 0:low,1:avg,2:high")
    public static int degreePosition = 1;


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
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType));
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeList.size());


        /*
        choose dynamic edge
         */
        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 0); //sorted nodes by degree descending
        Integer node = ((ArrayList<Integer>) degreeMap.keySet()).get((int) (nodeList.size() * ToolUtils.getNodeIndexPro(degreePosition)));
        ArrayList<Integer> e0 = ToolUtils.getRandomElement(nodeToEdgesMap.get(node));


        /*
        read decomposition full core file and compute coreEMap
         */
        String coreFile = "Decomposition_" + datasetName + "full";
        HashMap<Integer, Integer> coreVMap = FileIOUtils.loadCoreFile(coreFile);
        HashMap<ArrayList<Integer>, Integer> coreEMap = hypergraph.computeCoreEMapByCoreVMap(edgeList, coreVMap);

        /*
        decremental
         */
        Decremental decremental = new Decremental(hypergraph, coreEMap, coreVMap, e0);
        Result result_dremental = decremental.run();
        result_dremental.setDatasetName(datasetName);


        /*
        construct rest graph
         */
        //TODO:see the hypergraph is rest?

        /*
        decomposition rest graph
         */
        Decomposition decomposition_rest = new Decomposition(hypergraph); //TODO:see the hypergraph is rest?
        Result result_decomposition_rest = decomposition_rest.run();
        result_decomposition_rest.setDatasetName(datasetName);
        result_decomposition_rest.setType("rest");

        /*
        incremental
         */
        Incremental incremental = new Incremental(hypergraph, decomposition_rest.getCoreEMap(), decomposition_rest.getCoreVMap(), e0);
        Result result_incremental = incremental.run();
        result_incremental.setDatasetName(datasetName);


    }
}
