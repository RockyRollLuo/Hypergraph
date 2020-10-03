import model.Hypergraph;
import org.apache.log4j.Logger;
import util.FileIOUtils;
import util.SetOpt;
import util.SetOpt.Option;
import util.ToolUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'a', usage = "algorithm type, 0:decomposition, 1:incremental, 2:decremental")
    public static int algorithmType = 0;

    @Option(abbr = 'd', usage = "degree distributed, avg,low,high")
    public static String degreeDistribution = "avg";


    public static void main(String[] args) throws IOException {
        /*
        read parameters
         */
        Main main = new Main();
        args = SetOpt.setOpt(main, args);
        LOGGER.info("Run information:");
        System.out.println("algorithm type:" + algorithmType);
        System.out.println("degree distribution :" + degreeDistribution);

        /*
        graph information
         */
        String datasetName = args[0];
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, ToolUtils.getDelim(delimType));
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        System.out.println("dataset:" + datasetName);
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + edgeList.size());

        /*
        degree distribution
         */
        HashMap<Integer, Integer> degreeMap = hypergraph.computeDegree();
        HashMap<Integer, Integer> degreeDistribution = ToolUtils.getDegreeDistribution(degreeMap);
        System.out.println(degreeDistribution.toString());

        /*
        choose dynamic edge
         */
//        degreeMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(degreeMap, 1); //sorted nodes by degree
//        System.out.println(degreeMap.toString());



    }
}
