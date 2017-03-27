package test;

import java.util.ArrayList;
import java.util.List;

import static test.TestCalcAverage.calcSamprateAverage;

/**
* Источник:
* https://hydrogenaud.io/index.php/topic,85125.0.html
* */

public class Test {
    // MPEG versions - use [version]
    private static final int[] MPEG_VERSION = {25, 0, 2, 1};

    // Layers - use [layer]
    private static final int[] MPEG_LAYERS = {0, 3, 2, 1};

    // Bitrates - use [version][layer][bitrate]
    private static final int[][][] MPEG_BITRATES = {
        { // Version 2.5
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Reserved
            { 0,   8,  16,  24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, 0 }, // Layer 3
            { 0,   8,  16,  24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, 0 }, // Layer 2
            { 0,  32,  48,  56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, 0 }  // Layer 1
        },
        { // Reserved
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Invalid
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Invalid
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Invalid
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }  // Invalid
        },
        { // Version 2
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Reserved
            { 0,   8,  16,  24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, 0 }, // Layer 3
            { 0,   8,  16,  24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, 0 }, // Layer 2
            { 0,  32,  48,  56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, 0 }  // Layer 1
        },
        { // Version 1
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0, 0 }, // Reserved
            { 0,  32,  40,  48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 0 }, // Layer 3
            { 0,  32,  48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 384, 0 }, // Layer 2
            { 0,  32,  64,  96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 0 }, // Layer 1
        }
    };

    // Sample rates - use [version][srate]
    private static final int[][] MPEG_STATES = {
            { 11025, 12000,  8000, 0 }, // MPEG 2.5
            {     0,     0,     0, 0 }, // Reserved
            { 22050, 24000, 16000, 0 }, // MPEG 2
            { 44100, 48000, 32000, 0 }  // MPEG 1
    };

    // Samples per frame - use [version][layer]
    private static final int[][] MPEG_FRAME_SAMPLES = {
            //Rsvd     3     2     1  < Layer  v Version
            {    0,  576, 1152,  384 }, //  2.5
            {    0,    0,    0,    0 }, //  Reserved
            {    0,  576, 1152,  384 }, //  2
            {    0, 1152, 1152,  384 }  //  1
    };

    // Slot size (MPEG unit of measurement) - use [layer]
    private static final int[] MPEG_SLOT_SIZE = { 0, 1, 1, 4 }; // Rsvd, 3, 2, 1

    public static List<Integer> mpgGetFrameSize (byte[] hdr, long numberOfFrames) {
        List<Integer> ret = new ArrayList<>();
        List<Integer> averageBrOnSec = new ArrayList<>();

        for (int i=0; i<hdr.length; ) {
           {
                // Data to be extracted from the header
                int ver = (hdr[i] & 0x18) >> 3;   // Version index
                int lyr = (hdr[i] & 0x06) >> 1;   // Layer index    4 - ((nHeader >> 17) & 0x03);
                int pad = (hdr[i] & 0x02) >> 1;   // Padding? 0/1   ((nHeader >> 9) & 0x01)
                int brx = (hdr[i] & 0xf0) >> 4;   // Bitrate index  ((nHeader >> 12) & 0x0F);
                int srx = (hdr[i] & 0x0c) >> 2;   // SampRate index ((nHeader >> 10) & 0x03);


                // Lookup real values of these fields
                int bitrate = MPEG_BITRATES[ver][lyr][brx] * 1000;
                int samprate = MPEG_STATES[ver][srx];
                int samples = MPEG_FRAME_SAMPLES[ver][lyr];
                int slot_size = MPEG_SLOT_SIZE[lyr];

                //System.out.println(samprate + " " + samples);

                // In-between calculations
                //float bps = (float) ((float) samples / 8.0);
                //float fsize = ((bps * (float) bitrate) / (float) samprate) + ((pad == 1) ? slot_size : 0);

                //1сек = х * samples / samprate
               if (samprate!=0)
                ret.add(samprate);
                i++;

                if (i % numberOfFrames == 0) {
                    int ave = calcSamprateAverage(ret);
                    averageBrOnSec.add(ave);
                    ret.clear();
                }
            }
        }
        return averageBrOnSec;
    }
}
