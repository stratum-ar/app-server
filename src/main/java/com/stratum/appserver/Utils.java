package com.stratum.appserver;

import java.util.Arrays;

public class Utils {
    public static byte[] concatenateTwoArrays(byte[] array1, byte[] array2) {
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        array2 = Arrays.copyOf(result, result.length);

        return array2;
    }
}
