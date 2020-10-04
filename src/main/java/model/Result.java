package model;

import java.util.HashMap;

public class Result {
    private HashMap<Integer,Integer> output;
    private double takenTime;
    private String algorithmName;
    private String datasetName;
    private String type;



    /**
     * constructor
     */
    public Result(HashMap<Integer,Integer> output, double takenTime, String algorithmName) {
        this.output = output;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
    }

    public Result(HashMap<Integer,Integer> output, double takenTime, String algorithmName, String type) {
        this.output = output;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
        this.type = type;
    }

    public Result(HashMap<Integer,Integer> output, double takenTime, String algorithmName, String datasetName, String type) {
        this.output = output;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
        this.datasetName = datasetName;
        this.type = type;
    }

    /**
     * Getter() and Setter()
     */
    public HashMap<Integer,Integer> getOutput() {
        return output;
    }

    public void setOutput(HashMap<Integer,Integer> output) {
        this.output = output;
    }

    public double getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(long takenTime) {
        this.takenTime = takenTime;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
