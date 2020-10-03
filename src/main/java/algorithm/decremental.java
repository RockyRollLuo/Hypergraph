package algorithm;

import model.Hypergraph;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class decremental {
    private static final Logger LOGGER = Logger.getLogger(decremental.class);

    private Hypergraph hypergraph;
    private HashMap<ArrayList<Integer>, Integer> coreEMap;
    private HashMap<Integer, Integer> coreVMap;
    private final ArrayList<Integer> e0;  //the inserted edge

    /**
     * constructor
     */
    public decremental(Hypergraph hypergraph, HashMap<ArrayList<Integer>, Integer> coreEMap, HashMap<Integer, Integer> coreVMap, ArrayList<Integer> e0) {
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
         compute pre core and update graph
         1.update nodeList
         2.update edgeList
         3.update nodeToEdgesMap
         */
        boolean newNodeFlag = false;
        int pre_core_e0 = Integer.MAX_VALUE;
        for (Integer v : e0) {
            if (nodeList.contains(v)) {
                int core_v = coreVMap.get(v);
                pre_core_e0 = Math.min(core_v, pre_core_e0);

                //3.update nodeToEdgesMap
                ArrayList<ArrayList<Integer>> edgesContainV = nodeToEdgesMap.get(v);
                edgesContainV.add(e0);
                nodeToEdgesMap.put(v, edgesContainV);

            } else {
                coreVMap.put(v, 1);  // the core number of new node is 1
                newNodeFlag = true;

                nodeList.add(v); //1.update nodeList

                //3.update nodeToEdgesMap
                ArrayList<ArrayList<Integer>> edgesContainV = new ArrayList<>();
                edgesContainV.add(e0);
                nodeToEdgesMap.put(v, edgesContainV);
            }
        }
        if (newNodeFlag) {
            coreEMap.put(e0, 1); // the core nubmer of new edge is 1
        } else {
            coreEMap.put(e0, pre_core_e0);
        }
        edgeList.add(e0); //2.update edgeList


        /*
        traversal
        1.compute node support correlate with e_0
        2.shrink nodes cannot be (k+1)-core
        3.update core number of the nodes and edges in (k+1)-core
         */

        /*
        1.compute node support correlate with e_0
         */
        int core_root = pre_core_e0;
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
                            int support = supportMap.get(v)==null?1:(supportMap.get(v)+1);
                            supportMap.put(v, support);
                        }

                        if (core_e_contain_v== core_root && !visitedEdge.get(e_contain_v)) {
                            stack.push(e_contain_v);
                            visitedEdge.put(e_contain_v, true);
                        }
                    }
                }
            }
        }

        /*
        2.shrink nodes cannot be (k+1)-core
         */
        HashMap<Integer, Boolean> visitedNode = new HashMap<>();
        for (Integer node : nodeList) {
            visitedNode.put(node, false);
        }
        ArrayList<Integer> deleteNodes = new ArrayList<>();
        for (Integer v : supportMap.keySet()) {
            visitedNode.put(v, true);
            if (supportMap.get(v) <= core_root) {
                deleteNodes.add(v);
                for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                    for (Integer u : e_contain_v) {
                        if (supportMap.containsKey(u) && !visitedNode.get(u)) {
                            int support_u = supportMap.get(u) - 1;
                            if (support_u <= core_root) {
                                deleteNodes.add(u);
                            }
                        }
                    }
                }
            }
        }
        for (Integer v : deleteNodes) {
            supportMap.remove(v);
        }

        /*
        3.update core number of the nodes and edges in (k+1)-core
         */
        for (Integer v : supportMap.keySet()) {
            coreVMap.put(v, core_root+1); //the core of each node in supportMap is core_root

            for (ArrayList<Integer> e_contain_v : nodeToEdgesMap.get(v)) {
                if (coreEMap.get(e_contain_v)==core_root) { //only the core_root edges may be increase
                    int core_min = Integer.MAX_VALUE;
                    for (Integer u : e_contain_v) {
                        core_min = Math.min(core_min, coreVMap.get(u)); //update the core of edge
                    }
                    coreEMap.put(e_contain_v, core_min);
                }
            }
        }

        long endTime = System.nanoTime();
        LOGGER.info((double)(endTime - startTime) / 1.0E9D);
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
