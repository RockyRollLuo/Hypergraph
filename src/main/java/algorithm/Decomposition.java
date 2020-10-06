package algorithm;

import model.Hypergraph;
import model.Result;
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


    /**
     * travel graph by nodeToEdgesMap
     * this method is very slow, due to the comparing edges
     *
     * @return
     */
    public Result run() {
        LOGGER.info("Start decomposition...");
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        //temp data
        HashMap<ArrayList<Integer>, Integer> tempCoreEMap = new HashMap<>();
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>();

        ArrayList<Integer> tempNodeList = new ArrayList<>(nodeList);
        HashMap<Integer, ArrayList<ArrayList<Integer>>> tempNodeToEdgesMap = new HashMap<>(nodeToEdgesMap);

        //compute neighorsMap and degreeMap

        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        Integer minDegreeNode = hypergraph.getMinDegreeNode(degreeMap);
        int minDegree = degreeMap.get(minDegreeNode);

        for (int k = minDegree; ; k++) {
            if (tempNodeList.size() == 0) {
                break;
            }

            //delete nodes
            ArrayList<Integer> deleteNodes = new ArrayList<>();
            for (Integer v : tempNodeList) {
                if (degreeMap.get(v) <= k) {
                    deleteNodes.add(v);
                }
            }

            //new delete nodes
            while (!deleteNodes.isEmpty()) {
                ArrayList<Integer> newDeleteNodes = new ArrayList<>();

                for (Integer v : deleteNodes) {

                    ArrayList<ArrayList<Integer>> deleteEdges = new ArrayList<>();
                    for (ArrayList<Integer> edge : tempNodeToEdgesMap.get(v)) {
                        for (Integer u : edge) {
                            if (!tempNodeList.contains(u)) continue;

                            int uDegree = degreeMap.get(u) - 1;
                            degreeMap.put(u, uDegree); //update degreeMap
                            if (uDegree == k) {
                                newDeleteNodes.add(u);
                                tempCoreEMap.put(edge, k); //core number of edge
                                deleteEdges.add(edge);
                            }
                        }
                    }
                    tempCoreVMap.put(v, k); //core number of node

                    for (ArrayList<Integer> deleteEdge : deleteEdges) {
                        for (Integer w : deleteEdge) {
                            tempNodeToEdgesMap.get(w).remove(deleteEdge);
                        }
                    }
                    //tempNodeToEdgesMap.remove(v); //edges contain v need to update(one edge contain many nodes), very troublesome,
                    tempNodeList.remove(v);
                }
                deleteNodes = newDeleteNodes;
            }
        }

        this.coreVMap = tempCoreVMap;
        this.coreEMap = tempCoreEMap;
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info(takenTime);

        //System.out.println(ToolUtils.getCoreDistribution(coreVMap).toString());

        return new Result(coreVMap, takenTime, "Decomposition");

    }

    /**
     * create in ordinary graph
     * !!!wrong in hyergraphs!!!
     *
     * @return
     */
    public Result run1() {
        LOGGER.info("Start decomposition...");
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();

        //temp data
        HashMap<ArrayList<Integer>, Integer> tempCoreEMap = new HashMap<>();
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>();

        //compute neighorsMap and degreeMap
        HashMap<Integer, ArrayList<Integer>> neighorsMap = hypergraph.getNeighborsMap();

        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        Integer minDegreeNode = hypergraph.getMinDegreeNode(degreeMap);
        int minDegree = degreeMap.get(minDegreeNode);

        for (int k = minDegree; ; k++) {
            if (neighorsMap.size() == 0) {
                break;
            }

            //deleteNodes
            ArrayList<Integer> deleteNodes = new ArrayList<>();
            for (Integer v : neighorsMap.keySet()) {
                if (degreeMap.get(v) <= k) {
                    deleteNodes.add(v);
                }
            }

            //newDeleteNodes
            while (!deleteNodes.isEmpty()) {
                ArrayList<Integer> newDeleteNodes = new ArrayList<>();
                for (Integer v : deleteNodes) {
                    for (Integer v_neighbor : neighorsMap.get(v)) {
                        int deg_v_neighbor = degreeMap.get(v_neighbor) - 1;
                        degreeMap.put(v_neighbor, deg_v_neighbor); //update degreeMap
                        if (deg_v_neighbor == k) { //only be '==' cannot be '<=', duplicated
                            newDeleteNodes.add(v_neighbor);
                        }
                    }
                    tempCoreVMap.put(v, k); //core number of node
                    hypergraph.deleteNodeFromNeigborsMap(neighorsMap, v);
//                    degreeMap.remove(v);
                }
                deleteNodes = newDeleteNodes;
            }
        }
        //compute coreEMap
        for (ArrayList<Integer> e : edgeList) {
            int core_e = Integer.MAX_VALUE;
            for (Integer v : e) {
                core_e = Math.min(core_e, tempCoreVMap.get(v));
            }
            tempCoreEMap.put(e, core_e);
        }

        this.coreVMap = tempCoreVMap;
        this.coreEMap = tempCoreEMap;
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info(takenTime);

        //System.out.println(ToolUtils.getCoreDistribution(coreVMap).toString());

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
