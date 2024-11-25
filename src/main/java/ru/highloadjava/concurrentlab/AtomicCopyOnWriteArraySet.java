package ru.highloadjava.concurrentlab;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicCopyOnWriteArraySet<E> {
    // Атомарная ссылка на массив элементов
    private final AtomicReference<Object[]> elements = new AtomicReference<>(new Object[0]);

    // Метод добавления элемента
    public boolean add(E element) {
        Object[] currentArray;
        Object[] newArray;
        do {
            currentArray = elements.get(); // Получаем текущий массив
            if (contains(element)) { // Проверяем наличие элемента
                return false; // Элемент уже существует
            }
            // Создаем новый массив и добавляем элемент
            newArray = Arrays.copyOf(currentArray, currentArray.length + 1);
            newArray[currentArray.length] = element;
        } while (!elements.compareAndSet(currentArray, newArray)); // Атомарно обновляем массив
        return true;
    }

    // Метод удаления элемента
    public boolean remove(E element) {
        Object[] currentArray;
        Object[] newArray;
        do {
            currentArray = elements.get(); // Получаем текущий массив
            int index = indexOf(element); // Находим индекс элемента
            if (index == -1) {
                return false; // Элемент не найден
            }
            // Создаем новый массив без удаляемого элемента
            newArray = new Object[currentArray.length - 1];
            System.arraycopy(currentArray, 0, newArray, 0, index);
            System.arraycopy(currentArray, index + 1, newArray, index, currentArray.length - index - 1);
        } while (!elements.compareAndSet(currentArray, newArray)); // Атомарно обновляем массив
        return true;
    }

    // Метод проверки элемента
    public boolean contains(E element) {
        Object[] currentArray = elements.get(); // Получаем текущий массив
        for (Object obj : currentArray) {
            if (obj.equals(element)) {
                return true;
            }
        }
        return false;
    }

    // Метод преобразования в массив
    public Object[] toArray() {
        return Arrays.copyOf(elements.get(), elements.get().length); // Создаем копию
    }

    // Метод получения размера множества
    public int size() {
        return elements.get().length;
    }

    // Вспомогательный метод для поиска индекса элемента
    private int indexOf(E element) {
        Object[] currentArray = elements.get();
        for (int i = 0; i < currentArray.length; i++) {
            if (currentArray[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        AtomicCopyOnWriteArraySet<String> set = new AtomicCopyOnWriteArraySet<>();

        // Добавляем элементы
        System.out.println("Добавление Element1: " + set.add("Element1")); // true
        System.out.println("Добавление Element2: " + set.add("Element2")); // true
        System.out.println("Добавление Element1: " + set.add("Element1")); // false (уже существует)

        // Проверяем элементы
        System.out.println("Содержит Element1: " + set.contains("Element1")); // true
        System.out.println("Содержит Element3: " + set.contains("Element3")); // false

        // Удаляем элемент
        System.out.println("Удаление Element1: " + set.remove("Element1")); // true
        System.out.println("Содержит Element1: " + set.contains("Element1")); // false

        // Вывод всех элементов
        System.out.println("Текущее множество: " + Arrays.toString(set.toArray())); // [Element2]

        // Добавляем еще элементы
        set.add("Element3");
        set.add("Element4");
        System.out.println("Множество после добавлений: " + Arrays.toString(set.toArray())); // [Element2, Element3, Element4]

        // Размер множества
        System.out.println("Размер множества: " + set.size()); // 3
    }

}
