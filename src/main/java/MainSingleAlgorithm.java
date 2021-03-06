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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Option(abbr = 'p', usage = "whether print the core number in result")
    public static int printResult = 1;

    @Option(abbr = 'c', usage = "whether to constructe nodeToEdgesMap, false:no, true:yes")
    public static boolean constructStructure = true;


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
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType),constructStructure);
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeList.size());


        /*
        single algorithm
         */

        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();

        /*
        0.decomposition
         */
        if (algorithmType == 0) {
            Decomposition decomposition = new Decomposition(hypergraph);
            Result result_decomposition = decomposition.run();
            result_decomposition.setDatasetName(datasetName);
            result_decomposition.setType("full");
            FileIOUtils.writeCoreNumber(result_decomposition,printResult);

        }else if (algorithmType == 1 || algorithmType == 2) {
            degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 0); //sorted nodes by degree descending
            int index = ToolUtils.getNodeIndexRand(degreePosition,nodeList.size());
            Integer node = (new ArrayList<Integer>(degreeMap.keySet())).get(index);
            ArrayList<Integer> e0 = ToolUtils.getRandomElement(nodeToEdgesMap.get(node));
            LOGGER.info("dynamic edge e0:"+e0.toString());

            /*
            1.incremental
             */
            if (algorithmType == 1) {
                //1.construct graph
                hypergraph.deleteEdge(e0);
                //2.decomposition rest
                Decomposition decomposition_rest = new Decomposition(hypergraph);
                decomposition_rest.run();
                //3.incremental
                Incremental incremental = new Incremental(hypergraph, decomposition_rest.getCoreEMap(), decomposition_rest.getCoreVMap(), e0);
                Result result_incremental = incremental.run();
                result_incremental.setDatasetName(datasetName);
                FileIOUtils.writeCoreNumber(result_incremental,printResult);

            /*
            2.decremental
             */
            } else if (algorithmType == 2) {
                //1.decomposition
                Decomposition decomposition_full = new Decomposition(hypergraph);
                decomposition_full.run();
                //2.decremental
                Decremental decremental = new Decremental(hypergraph, decomposition_full.getCoreEMap(), decomposition_full.getCoreVMap(), e0);
                Result result_dremental = decremental.run();
                result_dremental.setDatasetName(datasetName);
                FileIOUtils.writeCoreNumber(result_dremental,printResult);
            }

            /*
            3.degree distribution
             */
        } else if (algorithmType == 3) {
            HashMap<Integer, Integer> degreeDistribution = ToolUtils.getDegreeDistribution(degreeMap);

            ArrayList<Integer> values = new ArrayList<>(degreeDistribution.values());
            System.out.println(values.toString());
            System.out.println();

            ArrayList<Double> valuesPrecent = new ArrayList<>();
            int nodeListSize = nodeList.size();
            for (int num : values) {
                double precent=num*1.0/nodeListSize;
                if (precent > 0.001) {
                    double precent2 = (new BigDecimal(precent).setScale(3, RoundingMode.FLOOR)).doubleValue();
                    valuesPrecent.add(precent2);
                }
            }
            System.out.println(valuesPrecent.toString());
        }

    }
}
