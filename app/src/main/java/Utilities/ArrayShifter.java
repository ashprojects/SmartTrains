package Utilities;

/**
 * Created by root on 27/6/17.
 */

public class ArrayShifter<T> {

    public void arrayShiftForward(T[] arr) {
        T last = arr[0], temp;
        T lastInt = arr[arr.length - 1];
        for (int i = 1; i < arr.length; ++i) {
            temp = arr[i];
            arr[i] = last;
            last = temp;
        }
        arr[0] = lastInt;
    }

    public void arrayShiftBackward(T[] arr) {
        T first = arr[0];
        for (int i = 0; i < arr.length - 1; ++i) {
            arr[i] = arr[i + 1];
        }
        arr[arr.length - 1] = first;
    }

    public static void intArrayShiftForward(int[] arr) {
        int last = arr[0], temp;
        int lastInt = arr[arr.length - 1];
        for (int i = 1; i < arr.length; ++i) {
            temp = arr[i];
            arr[i] = last;
            last = temp;
        }
        arr[0] = lastInt;
    }

    public static void intArrayShiftBackward(int[] arr) {
        int first = arr[0];
        for (int i = 0; i < arr.length - 1; ++i) {
            arr[i] = arr[i + 1];
        }
        arr[arr.length - 1] = first;
    }
}
