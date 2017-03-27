package test;

import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.DecoderException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static test.Test.mpgGetFrameSize;
import static test.TestCalcAverage.calcSamprateAverage;

public class TestJ { //23.03.17

    public static final File file = new File("src/main/resources/1.mp3");
    public static List<Integer> averageBrOnXsec = new ArrayList<>();
    private static int countFrameInX = 4;

    public static void main(String[] args) throws DecoderException, BitstreamException, IOException, InvalidAudioFrameException {
        MP3AudioHeader mp = new MP3AudioHeader(file);
        int samples = calcSamplerPerFrame(mp.getFormat());      //Сэмплов на фрейм (Sampler Per Frame)
        int samprate = mp.getSampleRateAsNumber();              //Индекс частоты дискретизации (Sampling rate index)
        int framesInXSec = (samprate / samples)/countFrameInX;  //int frames = samprate / samples * sec;
        long frameSize = 144*mp.getBitRateAsNumber()*1000/mp.getSampleRateAsNumber()+(mp.isPadding()? 1 : 0);
        List<Integer> calcBhFrames = new ArrayList<>();

        FileInputStream fin = new FileInputStream(file);

        byte[] buffer = new byte[fin.available()];
        fin.read(buffer, 0, fin.available());
        List<Integer> frames = mpgGetFrameSize(buffer, frameSize);

        fin.close();

        for (int i=0; i<frames.size(); i++){
            calcBhFrames.add(frames.get(i));
            if (i%framesInXSec==0){
                averageBrOnXsec.add(calcSamprateAverage(calcBhFrames));
                calcBhFrames.clear();
            }
        }

        calcBhFrames = averageBrOnXsec;
        averageBrOnXsec = new ArrayList<>();
        frames = null;

        int difference = calcBhFrames.size()/(calcBhFrames.size()-mp.getTrackLength()*countFrameInX)+1;
        assert calcBhFrames.size()!=0;
        for (int i=1 ; i<calcBhFrames.size()-1; i++){
            if (i%difference==0) {averageBrOnXsec.add((calcBhFrames.get(i+1)+calcBhFrames.get(i))/2); i++;}
            else averageBrOnXsec.add(calcBhFrames.get(i));
        }

        calcBhFrames = null;

        System.out.println(framesInXSec);
        System.out.println(mp.getTrackLength());
        System.out.println("----------------------");
        float i=0;
        for (Integer aa : averageBrOnXsec){
            System.out.println(String.format("%.2f sec : "+aa, i));
            i+=0.25;
        }
    }
    private static int calcSamplerPerFrame(String format){
        if (format.equals("MPEG-1 Layer 3") || format.equals("MPEG-2 Layer 2")
                || format.equals("MPEG-1 Layer 2") || format.equals("MPEG-2.5 Layer 2")){
            return 1152;
        }
        if (format.equals("MPEG-1 Layer 1") || format.equals("MPEG-2 Layer 1")
                || format.equals("MPEG-2.5 Layer 1")){
            return 384;
        }
        if (format.equals("MPEG-2 Layer 3") || format.equals("MPEG-2.5 Layer 3")){
            return 384;
        }
        return 0;
    }
}