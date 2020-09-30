package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Decomposition {
    private static final Logger LOGGER = Logger.getLogger(Decomposition.class);

    private Hypergraph hypergraph;

    public Decomposition(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    public void run() {
        LOGGER.info("Start decomposition...");
        long startTime = System.nanoTime();

        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();

        while (!nodeList.isEmpty()){
            Integer deleteNode = hypergraph.getMinimumDegreeNode();
            
        }




        long endTime = System.nanoTime();
        double durationTime = (endTime - startTime) / 1.0E9D;
    }

}
