package algorithm;

import model.Hypergraph;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Incremental {
    private static final Logger LOGGER = Logger.getLogger(Incremental.class);

    private Hypergraph hypergraph;
    private HashMap<ArrayList<Integer>, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;
    private ArrayList<Integer> e0;  //the inserted edge

    /**
     * constructor
     */
    public Incremental(Hypergraph hypergraph, HashMap<ArrayList<Integer>, Integer> coreEMap, HashMap<Integer, Integer> coreVMap, ArrayList<Integer> e0) {
        this.hypergraph = hypergraph;
        this.coreEMap = coreEMap;
        this.coreVMap = coreVMap;
        this.e0 = e0;
    }

    public void run() {
        LOGGER.info("Start incremental...");
        long startTime = System.nanoTime();

        /*
        private properties of hypergraph
         */
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        /*
        compute supportMap
         */
        HashMap<Integer, Integer> supportMap = hypergraph.computSupport(coreEMap, coreVMap);

        /*
         compute pre core
         */
        boolean newNodeFlag = false;
        int pre_core_e0 = Integer.MAX_VALUE;
        for (Integer v : e0) {
            if (nodeList.contains(v)) {
                int core_v = coreVMap.get(v);
                pre_core_e0 = Math.min(core_v, pre_core_e0);
            } else {
                coreVMap.put(v, 1);  // the core number of new ndoe is 1
                newNodeFlag = true;
            }
        }
        if (newNodeFlag) {
            coreEMap.put(e0, 1); // the core nubmer of new edge is 1
        } else {
            coreEMap.put(e0, pre_core_e0);
        }


        /*
        traversal
         */
        int root_core = pre_core_e0;
        Stack stack = new Stack();

        for (Integer v : e0) {
            if (coreVMap.get(v) == root_core) {
                for (ArrayList<Integer> e : nodeToEdgesMap.get(v)) {
                    if (coreEMap.get(e) == root_core) {
                        for (Integer u : e) {



                        }
                    }

                }


            }

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
