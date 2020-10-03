package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ToolUtils {
    private static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static int getRandomInt(int max) {
        return getRandom().nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return getRandom().nextInt(max - min + 1) + min;
    }

    public static <E> E getRandomElement(List<E> list) {
        return list.get(getRandomInt(list.size()));
    }

    /**
     * random choose k form N
     * Reservoir sampling
     *
     * @param list
     * @param k
     * @return
     */
    public static <E> E getRandomKFormN(List<E> list, int k) {
        int N = list.size();
        List<E> kList = null;

        for (int i = 0; i < k; i++) {
            kList.add(list.get(i));
        }
        for (int i = k; i < N; i++) {
            int r = getRandomInt(k + 1);
            if (r < k) {
                kList.add(r, list.get(i));
                kList.remove(r + 1);
            }
        }
        return (E) kList;
    }

    /**
     * sorted key in map by value
     * flag = 1 ascending order
     * flag = 0 descending order
     *
     * @param map
     * @param flag
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, int flag) {
        Map<K, V> sortMap = new LinkedHashMap<>();
        if (flag == 1) {
            map.entrySet().stream()
                    .sorted((o1, o2) -> o1.getValue().compareTo(o2.getValue()))
                    .forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        } else {
            map.entrySet().stream()
                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                    .forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        }
        return sortMap;
    }

    public static String getDelim(int delimType) {
        String delim="\t";
        switch (delimType) {
            case 0:
                delim="\t";
                break;
            case 1:
                delim=" ";
                break;
            case 2:
                delim = ",";
                break;
            default:
                break;
        }
        return delim;
    }

    public static String getAlgorithmType(int algorithmType) {
        String algorithm="decomposition";
        switch (algorithmType) {
            case 0:
                algorithm="decomposition";
                break;
            case 1:
                algorithm="incremental";
                break;
            case 2:
                algorithm = "decremental";
                break;
            case 3:
                algorithm = "degreeDistribution";
                break;
            default:
                break;
        }
        return algorithm;
    }

    public static String getDegreePosition(int degreePosition) {
        String degreePos="high";
        switch (degreePosition) {
            case 0:
                degreePos="low";
                break;
            case 1:
                degreePos="avg";
                break;
            case 2:
                degreePos = "high";
                break;
            default:
                break;
        }
        return degreePos;
    }



    public static HashMap<Integer, Integer> getDegreeDistribution(HashMap<Integer, Integer> degreeMap) {
        HashMap<Integer, Integer> degreeNums = new HashMap<>();
        for (int value : degreeMap.values()) {
            int num=degreeNums.get(value)==null?1:degreeNums.get(value)+1;
            degreeNums.put(value, num);
        }
        return degreeNums;
    }


    /**
     * get the node index position probability
     * @param degreePosition
     * @return
     */
    public static double getNodeIndexPro(int degreePosition) {
        double pro= 0.2;
        switch (degreePosition) {
            case 0:
                pro = 0.5;
                break;
            case 1:
                pro = 0.8;
                break;
            case 2:
                pro = 0.2;
                break;
            default:
                break;
        }
        return pro;
    }



}

