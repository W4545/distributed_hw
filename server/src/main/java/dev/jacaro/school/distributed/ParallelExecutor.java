package dev.jacaro.school.distributed;

import dev.jacaro.school.distributed.algorithms.SortingAlgorithms;
import dev.jacaro.school.distributed.configuration.RunConfiguration;
import dev.jacaro.school.distributed.configuration.RunResult;

import java.util.Arrays;

public class ParallelExecutor {
    public static RunResult executeConfiguration(RunConfiguration runConfiguration) {
        int threadPayloadSize = runConfiguration.arraySize() / runConfiguration.threadCount();
        int remainder = runConfiguration.arraySize() % runConfiguration.threadCount();

        int[][] arrays = new int[runConfiguration.threadCount()][];

        for (int i = 0; i < runConfiguration.threadCount(); i++) {
            if (i + 1 < runConfiguration.threadCount())
                arrays[i] = Arrays.copyOfRange(runConfiguration.startArray(), i * threadPayloadSize, i * threadPayloadSize + threadPayloadSize);
            else
                arrays[i] = Arrays.copyOfRange(runConfiguration.startArray(), i * threadPayloadSize, i * threadPayloadSize + threadPayloadSize + remainder);
        }

        SortThread[] sortThreads = new SortThread[runConfiguration.threadCount()];

        for (int i = sortThreads.length - 1; i >= 0; i--) {
            if (i == sortThreads.length - 1)
                sortThreads[i] = new SortThread(arrays[i], null);
            else
                sortThreads[i] = new SortThread(arrays[i], sortThreads[i + 1]);
        }

        var startTime = System.currentTimeMillis();
        for (int i = sortThreads.length - 1; i >= 0; i--) {
            sortThreads[i].start();
        }

        var endThread = sortThreads[0];

        try {
            endThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var endTime = System.currentTimeMillis();

        var result = endThread.getResultArray();

        return new RunResult(result, endTime - startTime);
    }
}
