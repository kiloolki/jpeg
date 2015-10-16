package codec;

public class Constants {

    // array for quantizing coefficients of image macro-blocks
    private static final int[] QUANTIZATION_VALUES_Y = new int[]{16, 11, 10,
        16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24,
        40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68,
        109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87,
        103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99};
    private static final char EOB = '#'; // end-of-block character

    private static final int[] QUANTIZATION_VALUES_CbCr = new int[]{
        17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 99, 99,
        99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
        99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
        99, 99, 99, 99};

    public static int[] getQuantizationValues_Y() {
        return QUANTIZATION_VALUES_Y;
    }

    public static int[] getQuantizationValues_CbCr() {
        return QUANTIZATION_VALUES_CbCr;
    }

    public static char getEob() {
        return EOB;
    }

}
