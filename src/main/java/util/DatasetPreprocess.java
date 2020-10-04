package util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DatasetPreprocess {
    private static final Logger LOGGER = Logger.getLogger(DatasetPreprocess.class);

    public static void main(String[] args) throws IOException {

        String datasetNameList[] = {"test", "congress-bills", "tags-stack-overflow", "coauth-DBLP", "threads-stack-overflow"};

        boolean constructTimeToEdgesMap = false;
        String dataset = datasetNameList[4];
        String datasetPath = "C:\\Users\\luoqi\\Desktop\\TemporalHypergraphs\\" + dataset;
        String file_nverts = dataset + "-nverts.txt";
        String file_simplices = dataset + "-simplices.txt";
        String file_times = dataset + "-times.txt";

        /*
        read file
         */
        ArrayList<Integer> edgesSizeList = readIntegerFile(datasetPath, file_nverts);
        ArrayList<Integer> nodesList = readIntegerFile(datasetPath, file_simplices);

        /*
        construct hyperedges
         */
        ArrayList<ArrayList<Integer>> tempEdgeList = new ArrayList<>();
        int index = 0;
        for (int edgeSize : edgesSizeList) {
            ArrayList<Integer> edge = new ArrayList<Integer>(nodesList.subList(index, index + edgeSize));
            tempEdgeList.add(edge);
            index += edgeSize;
        }
        ArrayList<ArrayList<Integer>> edgeList;
        if (!constructTimeToEdgesMap) {
            /*
            remove duplicates edges and write file
             */
            HashSet<ArrayList<Integer>> tempEdgeSet = new HashSet<>(tempEdgeList);
            edgeList = new ArrayList<>(tempEdgeSet);
            writeFile(edgeList, datasetPath, dataset);
        } else {
             /*
            construct timestamp hyperedges
            */
            edgeList = tempEdgeList;
            ArrayList<String> timesList = readStringFile(datasetPath, file_times);
            HashMap<String, ArrayList<ArrayList<Integer>>> timeToEdgesListMap = new HashMap<>();
            int edgeSize = edgeList.size();
            for (int i = 0; i < edgeSize; i++) {
                String time = timesList.get(i);
                ArrayList<Integer> edge = edgeList.get(i);

                if (timeToEdgesListMap.containsKey(time)) {
                    ArrayList<ArrayList<Integer>> edges = timeToEdgesListMap.get(time);
                    edges.add(edge);
                    timeToEdgesListMap.put(time, edges);
                } else {
                    ArrayList<ArrayList<Integer>> edges = new ArrayList<>();
                    edges.add(edge);
                    timeToEdgesListMap.put(time, edges);
                }
            }
            //System.out.println(timeToEdgesListMap.toString());
            //sort edges by time in map
            //TODO:split edges by time, batch insert edges in latter time
            //TODO:write the edges by time
        }
    }

    private static ArrayList<Integer> readIntegerFile(String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName;
        ArrayList<Integer> list = new ArrayList<>();
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            Integer value = Integer.parseInt(line);
            list.add(value);
        }

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " READ DONE!: " + (double) (endTime - startTime) / 1.0E9D);

        return list;
    }

    private static ArrayList<String> readStringFile(String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName;
        ArrayList<String> list = new ArrayList<>();
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            list.add(line);
        }

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " READ DONE!: " + (double) (endTime - startTime) / 1.0E9D);

        return list;
    }

    public static void writeFile(ArrayList<ArrayList<Integer>> edgeList, String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName+"-hyperedges" + ".txt";

        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (ArrayList<Integer> edge : edgeList) {

            String line = edge.toString().replace("[", "").replace("]", "").replace(",", "");
            bw.write(line);
            bw.newLine();
        }
        bw.close();

        long endTime = System.nanoTime();
        LOGGER.info(fileName + " WRITE DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }
}
