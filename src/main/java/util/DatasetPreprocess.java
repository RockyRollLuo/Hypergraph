package util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatasetPreprocess {
    public static void main(String[] args) throws IOException {

        String datasetNameList[] = {"test", "congress-bills", "tags-stack-overflow", "coauth-DBLP", "threads-stack-overflow"};

        String dataset = datasetNameList[0];
        String datasetPath = "C:\\Users\\luoqi\\Desktop\\TemporalHypergraphs\\" + dataset;
        String file_nverts = dataset + "-nverts.txt";
        String file_simplices = dataset + "-simplices.txt";
        String file_times = dataset + "-times.txt";

        /*
        read file
         */
        ArrayList<Integer> edgesSizeList = readFile(datasetPath, file_nverts);
        ArrayList<Integer> nodesList = readFile(datasetPath, file_simplices);
        ArrayList<Integer> timesList = readFile(datasetPath, file_times);

        /*
        construct hyperedges
         */
        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();
        for (int edgeSize : edgesSizeList) {
            int index = 0;
            ArrayList<Integer> edge = (ArrayList<Integer>) nodesList.subList(index, index + edgeSize);
            edgeList.add(edge);
            index += edgeSize;
        }
        writeFile(edgeList, datasetPath, dataset);

        /*
        construct timestamp hyperedges
         */
        HashMap<Integer, ArrayList<ArrayList<Integer>>> timeToEdgesListMap = new HashMap<>();
        int edgeSize = edgeList.size();
        for (int index = 0; index < edgeSize; index++) {
            int time = timesList.get(index);
            ArrayList<Integer> edge = edgeList.get(index);

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

        //edges have been sort by time in map
        //TODO:split edges by time, batch insert edges in latter time
        //TODO:write the edges by time
    }

    private static ArrayList<Integer> readFile(String datasetPath, String fileName) throws IOException {
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
        System.out.println(fileName + " READ DONE!: " + (double) (endTime - startTime) / 1.0E9D);

        return list;
    }

    public static void writeFile(ArrayList<ArrayList<Integer>> edgeList, String datasetPath, String fileName) throws IOException {
        long startTime = System.nanoTime();

        String path = datasetPath + "\\" + fileName + ".txt";

        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (ArrayList<Integer> edge : edgeList) {
            String line = edge.toString().replace("[", "").replace("]", "").replace(",", " ");

            bw.write(line);
            bw.newLine();
        }
        bw.close();

        long endTime = System.nanoTime();
        System.out.println(fileName + " WRITE DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }
}
