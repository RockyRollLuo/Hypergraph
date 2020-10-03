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

public class MainSingleAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainSingleAlgorithm.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'a', usage = "algorithm type, 0:decomposition, 1:incremental, 2:decremental, 3:degreeDistribution")
    public static int algorithmType = 0;

    @Option(abbr = 'd', usage = "degree position, 0:low,1:avg,2:high")
    public static int degreePosition = 1;


    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        MainSingleAlgorithm main = new MainSingleAlgorithm();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("algorithm type:" + ToolUtils.getAlgorithmType(algorithmType));
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
        HashMap<Integer, Integer> degreeMap = hypergraph.computeDegree();
        degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 0); //sorted nodes by degree descending
        int index = (int) (nodeList.size() * ToolUtils.getNodeIndexPro(degreePosition));
        Integer node = ((ArrayList<Integer>) degreeMap.keySet()).get(index);
        ArrayList<Integer> e0 = ToolUtils.getRandomElement(nodeToEdgesMap.get(node));


        switch (algorithmType) {
            case 0:
                Decomposition decomposition = new Decomposition(hypergraph);
                Result result_decomposition = decomposition.run();
                result_decomposition.setDatasetName(datasetName);
                result_decomposition.setType("full");
                //TODO:write result
                break;
            case 1:
                //1.construct graph
                hypergraph.deleteEdge(e0);
                //2.decomposition rest
                Decomposition decomposition_rest = new Decomposition(hypergraph);
                decomposition_rest.run();
                //3.incremental
                Incremental incremental = new Incremental(hypergraph, decomposition_rest.getCoreEMap(), decomposition_rest.getCoreVMap(), e0);
                Result result_incremental = incremental.run();
                result_incremental.setDatasetName(datasetName);
                //TODO:write result
                break;
            case 2:
                //1.decomposition
                Decomposition decomposition_full = new Decomposition(hypergraph);
                decomposition_full.run();
                //2.decremental
                Decremental decremental = new Decremental(hypergraph, decomposition_full.getCoreEMap(), decomposition_full.getCoreVMap(), e0);
                Result result_dremental = decremental.run();
                result_dremental.setDatasetName(datasetName);
                //TODO:write result
                break;
            case 3:
                HashMap<Integer, Integer> degreeDistribution = ToolUtils.getDegreeDistribution(degreeMap);
                System.out.println(degreeDistribution.toString());
                break;
            default:
                break;

        }

    }
}