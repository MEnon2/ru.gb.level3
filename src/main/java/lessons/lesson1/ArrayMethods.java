package lessons.lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class ArrayMethods<T> {
    private final T[]  array;

    public ArrayMethods(T... array) {
        this.array = array;
    }

    public void changePosition(int i, int j) {
        if (i < array.length && j < array.length) {
            T el = array[i];
            array[i] = array[j];
            array[j] = el;
        } else {
            System.out.println("Замена не возможна");
        }
    }

    public void print() {
        System.out.println(Arrays.stream(array).collect(Collectors.toList()));
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, array);
        return arrayList;
    }
}
