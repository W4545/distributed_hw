package dev.jacaro.school.distributed.algorithms;

public class SortingAlgorithms {

    private static void swap(int[] array, int first, int second) {
        int temp = array[first];
        array[first] = array[second];
        array[second] = temp;
    }

    public static void bubbleSort(int[] array) {
        var swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < array.length - 1; i++) {
                if (array[i] > array[i + 1]) {
                    swapped = true;
                    swap(array, i, i + 1);
                }
            }
        }
    }
}
