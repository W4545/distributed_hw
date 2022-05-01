package dev.jacaro.school.distributed;

import dev.jacaro.school.distributed.algorithms.SortingAlgorithms;

public class SortThread extends Thread {

    private final int[] array;
    private volatile int[] resultArray;
    private final SortThread mergeThread;

    public SortThread(int[] array, SortThread mergeThread) {
        this.array = array;
        this.mergeThread = mergeThread;
        resultArray = null;
    }

    @Override
    public void run() {
        SortingAlgorithms.bubbleSort(array);

        if (mergeThread == null)
            resultArray = array;
        else {
            try {
                mergeThread.join();
                int[] mergeThreadResult = mergeThread.getResultArray();
                int[] tempResult = new int[mergeThreadResult.length + array.length];

                System.arraycopy(array, 0, tempResult, 0, array.length);
                System.arraycopy(mergeThreadResult, 0, tempResult, array.length, mergeThreadResult.length);

                SortingAlgorithms.bubbleSort(tempResult);
                resultArray = tempResult;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int[] getResultArray() {
        return resultArray;
    }
}
