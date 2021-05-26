package lessons.lesson1;

import java.util.ArrayList;

public class Box<T extends Fruit> {
    private final ArrayList<T> arrayList = new ArrayList<>();

    public void add(T el) {
        arrayList.add(el);
    }

    public void remove(T el) {
        arrayList.remove(el);
    }

    public double getWeight() {
        double sum = 0;
        for (T el : arrayList) sum += el.getWeigth();
        return sum;
    }

    public boolean compare(Box<?> box) {
        return this.getWeight() == box.getWeight();
    }

    public void moveFruits(Box<T> box) {
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            box.add(arrayList.get(i));
            this.remove(arrayList.get(i));
        }
    }
}
