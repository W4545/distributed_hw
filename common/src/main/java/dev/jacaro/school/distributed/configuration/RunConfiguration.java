package dev.jacaro.school.distributed.configuration;

import java.io.Serializable;
import java.util.Arrays;

public record RunConfiguration(int threadCount, int arraySize, int[] startArray) {

    public String objectSerial() {
        String[] strings = new String[startArray.length];
        for (int i = 0; i < strings.length; i++)
            strings[i] = Integer.toString(startArray[i]);
        return String.format("%d;%d;%s", threadCount, arraySize, String.join(",", strings));
    }

    public static RunConfiguration objectDeSerial(String string) {
        var split = string.split(";");

        int threadCount = Integer.parseInt(split[0]);
        int arraySize = Integer.parseInt(split[1]);

        var intSplit = split[2].split(",");
        int[] arr = new int[intSplit.length];

        for (int i = 0; i < intSplit.length; i++)
            arr[i] = Integer.parseInt(intSplit[i]);

        return new RunConfiguration(threadCount, arraySize, arr);
    }
}
