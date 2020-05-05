package liu;

import java.util.Arrays;

public class ToInt {
    /**
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b){
        return new Byte(b).intValue();
    }

    /**
     *
     * @param b
     * @return
     */
    public static int[] byteToInt_arr(byte[] b){
        int i=0;
        int[] nn = new int[b.length];
        for(byte bs : b){
            nn[i++] = byteToInt(bs);
        }
        return nn;
    }
}
