package test;


import java.util.List;

public class TestCalcAverage {
    public static int calcSamprateAverage(List<Integer> samprate){
        if (samprate.size()==0) return 0;
        int average = 0;
        for (Integer ll : samprate){
            average += ll;
        }
        average = average / samprate.size();
        return average;
    }
    public static int calcSamplesAverage(List<Integer> samples){
        int average = 0;
        for (Integer ll : samples){
            average += ll;
        }
        average = average / samples.size();
        return average;
    }
}
