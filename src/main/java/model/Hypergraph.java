package model;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Hypergraph {
    private static final Logger LOGGER = Logger.getLogger(Hypergraph.class);

    private ArrayList<Integer> nodeList;
    private ArrayList<ArrayList<Integer>> edgeList;

    private HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap;

    //constructor
    public Hypergraph(ArrayList<Integer> nodeList, ArrayList<ArrayList<Integer>> edgeList) {
        this.nodeList = nodeList;
        this.edgeList = edgeList;
        constructNodeToEdgesMap();
    }

    /**
     * construct nodeToEdgesMap
     */
    private void constructNodeToEdgesMap() {
        LOGGER.info("Start construct nodeToEdgesMap...");
        long startTime = System.nanoTime();

        for (Integer node : nodeList) {
            ArrayList<ArrayList<Integer>> edgesContainNode = new ArrayList<>();

            for (ArrayList<Integer> edge : edgeList) {
                if (edge.contains(node)) {
                    edgesContainNode.add(edge);
                }
            }
            nodeToEdgesMap.put(node, edgesContainNode);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double)(endTime - startTime) / 1.0E9D);
    }


    /**
     * compute degree of each node
     * @return degreeMap
     */
    public HashMap<Integer,Integer> computeDegree() {
        LOGGER.info("Start computeDegree...");

        HashMap<Integer, Integer> degreeMap = new HashMap<>();
        long startTime = System.nanoTime();

        for (Integer node : nodeToEdgesMap.keySet()) {
            int degree = nodeToEdgesMap.get(node).size();
            degreeMap.put(node, degree);
        }

        long endTime = System.nanoTime();
        LOGGER.info((double)(endTime - startTime) / 1.0E9D);
        return degreeMap;
    }

    /**
     * get the node of the minimum degree by degreeMap
     * @return node ID
     */
    public Integer getMinDegreeNode(HashMap<Integer, Integer> degreeMap) {
        int minDegree = Integer.MAX_VALUE;
        Integer tempNode = null;

        for (Integer node : degreeMap.keySet()) {
            int degree = degreeMap.get(node);
            if (degree < minDegree) {
                minDegree = degree;
                tempNode = node;
            }
        }
        return tempNode;
    }


    /**
     * delete one node, all the edges contain the node will be deleted
     * @param node ID
     */
    public void deleteNode(Integer node) {
        //1.update nodeList
        nodeList.remove(node);

        //2.update edgeList
        ArrayList<ArrayList<Integer>> edgesContainNode = nodeToEdgesMap.get(node);
        edgeList.remove(edgesContainNode);

        //3.update nodeToEdgesMap
        nodeToEdgesMap.remove(node);
    }

    /**
     * delete one edge,update node in the edge
     * @param edge
     */
    public void deleteEdge(ArrayList<Integer> edge) {
        //1.update edgeList
        edgeList.remove(edge);

        //2.update nodeToEdgesMap
        for (Integer node : edge) {
            nodeToEdgesMap.get(node).remove(edge);
            if (nodeToEdgesMap.get(node).size() == 0) {
                nodeToEdgesMap.remove(node);
                //3.update nodelist
                nodeList.remove(node);
            }
        }
    }



    //getter and setter
    public ArrayList<Integer> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Integer> nodeList) {
        this.nodeList = nodeList;
    }

    public ArrayList<ArrayList<Integer>> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(ArrayList<ArrayList<Integer>> edgeList) {
        this.edgeList = edgeList;
    }

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> getNodeToEdgesMap() {
        return nodeToEdgesMap;
    }

    public void setNodeToEdgesMap(HashMap<Integer, ArrayList<ArrayList<Integer>>> nodeToEdgesMap) {
        this.nodeToEdgesMap = nodeToEdgesMap;
    }
}
