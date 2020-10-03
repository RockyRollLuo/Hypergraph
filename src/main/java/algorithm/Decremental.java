package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;
import util.ToolUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Decremental {
    private static final Logger LOGGER = Logger.getLogger(Decremental.class);

    private Hypergraph hypergraph;
    private HashMap<ArrayList<Integer>, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;
    private final ArrayList<Integer> e0;  //the deleted edge

    /**
     * constructor
     */
    public Decremental(Hypergraph hypergraph, HashMap<ArrayList<Integer>, Integer> coreEMap, HashMap<Integer, Integer> coreVMap, ArrayList<Integer> e0) {
        this.hypergraph = hypergraph;
        this.coreEMap = coreEMap;
        this.coreVMap = coreVMap;
        this.e0 = e0;
    }

    public Result run() {
        LOGGER.info("Start Decremental...");
        long startTime = System.nanoTime();

        /*
        private properties of hypergraph
         */
        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        ArrayList<ArrayList<Integer>> edgeList = hypergraph.getEdgeList();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap = hypergraph.getNodeToEdgesMap();

        /*
         compute pre core and update graph
         1.update nodeList
         2.update edgeList
         3.update nodeToEdgesMap
         */
        for (Integer v : e0) {
            ArrayList<ArrayList<Integer>> edgesContainV = nodeToEdgesMap.get(v);

            if (edgesContainV.size() == 1) {
                nodeList.remove(v); //1.update nodeList

                nodeToEdgesMap.remove(v); //3.update nodeToEdgesMap
            } else {
                //3.update nodeToEdgesMap
                edgesContainV.remove(e0);
                nodeToEdgesMap.put(v, edgesContainV);
            }
        }
        edgeList.remove(e0);  //2.update edgeList

        /*
        traversal
        1.compute node support correlate with e_0
        2.shrink nodes cannot be k-core
        3.update core number of edges in k-core
         */

        /*
        1.compute node support correlate with e_0
         */
        int core_root = coreEMap.get(e0);
        HashMap<Integer, Integer> supportMap = new HashMap<>();
        HashMap<ArrayList<Integer>, Boolean> visitedEdge = new HashMap<>();
        for (ArrayList<Integer> edge : edgeList) {
            visitedEdge.put(edge, false);
        }
        visitedEdge.put(e0, true);
        Stack<ArrayList<Integer>> stack = new Stack<>();
        stack.push(e0);

        while (!stack.isEmpty()) {
            ArrayList<Integer> e_stack = stack.pop();
            for (Integer v : e_stack) {
                if (coreVMap.get(v) == core_root) {

                    for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                        int core_e_contain_v = coreEMap.get(e_contain_v);
                        if (core_e_contain_v >= core_root) {
                            int support = supportMap.get(v) == null ? 1 : (supportMap.get(v) + 1);
                            supportMap.put(v, support);
                        }

                        if (core_e_contain_v == core_root && !visitedEdge.get(e_contain_v)) {
                            stack.push(e_contain_v);
                            visitedEdge.put(e_contain_v, true);
                        }
                    }
                }
            }
        }

        /*
        2.shrink nodes cannot be k-core
         */
        ArrayList<Integer> reduceCoreNodes = new ArrayList<>();
        supportMap = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(supportMap, 1); //sorted by value
        for (Integer v : supportMap.keySet()) {
            if (supportMap.get(v) < core_root) {
                coreVMap.put(v, core_root - 1);
                reduceCoreNodes.add(v);
                for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                    for (Integer u : e_contain_v) {
                        if (supportMap.containsKey(u) && !reduceCoreNodes.contains(u)) {
                            int support_u = supportMap.get(u) - 1;
                            supportMap.put(u, support_u);
                        }
                    }
                }
            }
        }
        for (Integer v : reduceCoreNodes) {
            supportMap.remove(v);
        }

        /*
        3.update core number of edges in k-core
         */
        for (Integer v : reduceCoreNodes) {
            for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                if (coreEMap.get(e_contain_v) == core_root) { //only the core_root edges may be increase
                    int core_min = Integer.MAX_VALUE;
                    for (Integer u : e_contain_v) {
                        core_min = Math.min(core_min, coreVMap.get(u)); //update the core of edge
                    }
                    coreEMap.put(e_contain_v, core_min);
                }
            }
        }

        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info(takenTime);

        return new Result(coreVMap, takenTime, "Decremental","rest");
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
