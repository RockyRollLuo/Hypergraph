package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.ToolUtils;

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

    public Result run() {
        LOGGER.info("Start decomposition...");
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        //temp data
        HashMap<ArrayList<Integer>, Integer> tempCoreEMap = new HashMap<>();
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>();

        //compute degreeMap
        HashMap<Integer, Integer> degreeMap = hypergraph.computeDegree();

        Integer minDegreeNode = hypergraph.getMinDegreeNode(degreeMap);
        int minDegree = degreeMap.get(minDegreeNode);

        for (int k = minDegree; ; k++) {
            if (degreeMap.size() == 0) {
                break;
            }

            ArrayList<Integer> deleteNodes = new ArrayList<>();

            for (Integer v : degreeMap.keySet()) {
                if (degreeMap.get(v) <= k) {
                    deleteNodes.add(v);
                }
            }

            while (!deleteNodes.isEmpty()) {
                ArrayList<Integer> newDeleteNodes = new ArrayList<>();

                for (Integer v : deleteNodes) {

                    ArrayList<ArrayList<Integer>> edgesContainDeleteNode = nodeToEdgesMap.get(v);
                    for (ArrayList<Integer> edge : edgesContainDeleteNode) {
                        for (Integer u : edge) {
                            if (!degreeMap.containsKey(u)) {
                                continue;
                            }
                            int uDegree = degreeMap.get(u) - 1;
                            if (uDegree == k) {
                                newDeleteNodes.add(u);

                                tempCoreEMap.put(edge, k); //core number of edge
                            }
                        }
                    }
                    tempCoreVMap.put(v, k); //core number of node
                    //TODO:update graph,not only remove v,but also remove the nodes in edge
                    degreeMap.remove(v);
                }
                deleteNodes = newDeleteNodes;
            }

        }

        this.coreVMap = tempCoreVMap;
        this.coreEMap = tempCoreEMap;
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info(takenTime);

        return new Result(coreVMap, takenTime, "Decomposition");

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
