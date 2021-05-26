package lessons.lesson1;

import java.util.ArrayList;

public class LessonApp {
    public static void main(String[] args) {
        doFirstExercise();
        doSecondExercise();
        doThirdExercise();
    }

    /*
    1. Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
    */
    public static void doFirstExercise() {
        ArrayMethods<String> arr1 = new ArrayMethods<>("a", "b", "c");
        arr1.print();

        arr1.changePosition(1, 2);
        arr1.print();

        ArrayMethods<Integer> arr2 = new ArrayMethods<>(1, 2, 3);
        arr2.print();

        arr2.changePosition(0, 2);
        arr2.print();
    }


    /*
    2. Написать метод, который преобразует массив в ArrayList;
    */

    public static void doSecondExercise() {
        ArrayMethods<String> arr1 = new ArrayMethods<>("a", "b", "c");
        arr1.print();

        ArrayList<String> arrayListString = arr1.toArrayList();
        System.out.println(arrayListString);

        ArrayMethods<Integer> arr2 = new ArrayMethods<>(1, 2, 3);
        arr2.print();

        ArrayList<Integer> arrayListInt = arr2.toArrayList();
        System.out.println(arrayListInt);
    }


    /*
    3. Большая задача:
        a. Есть классы Fruit -> Apple, Orange;(больше фруктов не надо)
        b. Класс Box в который можно складывать фрукты, коробки условно сортируются по типу фрукта, поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
        c. Для хранения фруктов внутри коробки можете использовать ArrayList;
        d. Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов и вес одного фрукта(вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
        e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут в compare в качестве параметра,
        true - если их веса равны, false в противном случае(коробки с яблоками мы можем сравнивать с коробками с апельсинами);
        f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку(помним про сортировку фруктов,
         нельзя яблоки высыпать в коробку с апельсинами), соответственно в текущей коробке фруктов не остается, а в другую
          перекидываются объекты, которые были в этой коробке;
        g. Не забываем про метод добавления фрукта в коробку.
     */

    public static void doThirdExercise() {

        Box<Apple> boxApple = new Box<>();
        Box<Orange> boxOrange = new Box<>();

        for (int i = 0; i < 2; i++) {
            boxApple.add(new Apple());
        }

        for (int i = 0; i < 3; i++) {
            boxOrange.add(new Orange());
        }

        System.out.println(boxApple.getWeight());
        System.out.println(boxOrange.getWeight());

        System.out.println(boxApple.compare(boxOrange));

        Box<Apple> newBoxApple = new Box<>();
        Box<Orange> newBoxOrange = new Box<>();

        boxApple.moveFruits(newBoxApple);
        boxOrange.moveFruits(newBoxOrange);

        System.out.println(boxApple.getWeight());
        System.out.println(boxOrange.getWeight());

        System.out.println(newBoxApple.getWeight());
        System.out.println(newBoxOrange.getWeight());

    }


}
