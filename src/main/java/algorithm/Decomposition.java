package algorithm;

import model.Hypergraph;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Decomposition {
    private static final Logger LOGGER = Logger.getLogger(Decomposition.class);

    private Hypergraph hypergraph;
    private HashMap<ArrayList<Integer>, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;

    public Decomposition(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    public void run() {
        LOGGER.info("Start decomposition...");
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        //compute degreeMap
        HashMap<Integer, Integer> degreeMap = hypergraph.computeDegree();

        Integer minDegreeNode = hypergraph.getMinDegreeNode(degreeMap);
        int minDegree = degreeMap.get(minDegreeNode);

        int k = minDegree;
        while (!nodeList.isEmpty()) {

            ArrayList<Integer> deleteNodes = new ArrayList<>();
            for (Integer v : degreeMap.keySet()) {
                if (degreeMap.get(v) <= k) {
                    ArrayList<ArrayList<Integer>> edgesContainDeleteNode = nodeToEdgesMap.get(v);
                    for (ArrayList<Integer> edge : edgesContainDeleteNode) {
                        for (Integer u : edge) {
                            int uDegree = degreeMap.get(u) - 1;
                            if (uDegree == 0) {
                                nodeList.remove(u);
                                deleteNodes.remove(u);
                            } else {
                                degreeMap.put(u, uDegree);
                            }
                        }
                        edgeList.remove(edge);
                        coreEMap.put(edge, k);
                    }
                    nodeList.remove(v);
                    coreVMap.put(v, k);
                }
            }
            for (Integer v : deleteNodes) {
                degreeMap.remove(v);

            }
            k++;
        }

        long endTime = System.nanoTime();
        double durationTime = (endTime - startTime) / 1.0E9D;
    }

    /**
     * getter and setter
     */

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public void setHypergraph(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    public HashMap<ArrayList<Integer>, Integer> getCoreEMap() {
        return coreEMap;
    }

    public void setCoreEMap(HashMap<ArrayList<Integer>, Integer> coreEMap) {
        this.coreEMap = coreEMap;
    }

    public HashMap<Integer, Integer> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Integer> coreVMap) {
        this.coreVMap = coreVMap;
    }
}
