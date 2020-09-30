import org.apache.log4j.Logger;
import util.*;
import util.SetOpt.Option;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

public class Main {
    private static Logger LOGGER = Logger.getLogger(Main.class);
    @Option(abbr = 'p', usage = "Print trussMap")
    public static int print = 1;

    @Option(abbr = 's', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static String delim = "\t";

    @Option(abbr = 'd', usage = "dynamic type, 0:static TrussDecomp, 1:MultiEdgesInsertion, 2:MultiEdgesDeletion, 3:SupTrussnessParallel")
    public static int dynamicType = 0;

    @Option(abbr = 'a', usage = "algorithm type, 0:TCPIndex, 1:SupTruss, 2:ParaTruss")
    public static int algorithmType = 0;

    @Option(abbr = 'o', usage = "orders of magnitude,number=2^o,o=0,1,2,3,4,5,6")
    public static int order = 6;

    @Option(abbr = 't', usage = "max thread number")
    public static int threadNum = 1;


    public static void main(String[] args) throws IOException {
        //read parameters
        Main main = new Main();
        args = SetOpt.setOpt(main, args);

        LOGGER.info("Basic information:");
        System.err.println("Dynamic edges:" + (int) Math.pow(10, order));
        System.err.println("Dynamic type:" + dynamicType);
        System.err.println("Algorithm type:" + algorithmType);
        System.err.println("Thread number:" + threadNum);

        //full graph
        String datasetName = args[0];

    }
}
