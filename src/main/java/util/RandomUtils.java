package util;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static Logger LOGGER = Logger.getLogger(RandomUtils.class);


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


}

