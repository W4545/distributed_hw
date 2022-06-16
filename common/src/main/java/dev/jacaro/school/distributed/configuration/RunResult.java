package dev.jacaro.school.distributed.configuration;

import java.io.Serializable;
import java.util.Arrays;

public record RunResult(int[] resultArray, long executionTime) {

    public String objectSerial() {
        String[] strings = new String[resultArray.length];
        for (int i = 0; i < strings.length; i++)
            strings[i] = Integer.toString(resultArray[i]);
        return String.format("%s;%d", String.join(",", strings), executionTime);
    }

    public static RunResult objectDeSerial(String string) {
        var split = string.split(";");

        var intSplit = split[0].split(",");
        int[] arr = new int[intSplit.length];

        for (int i = 0; i < intSplit.length; i++)
            arr[i] = Integer.parseInt(intSplit[i]);

        var time = Long.parseLong(split[1]);

        return new RunResult(arr, time);
    }
}
