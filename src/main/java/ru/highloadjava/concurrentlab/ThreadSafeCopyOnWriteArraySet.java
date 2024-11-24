package ru.highloadjava.concurrentlab;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeCopyOnWriteArraySet<E> {
    private volatile Object[] elements = new Object[0]; // Хранение элементов
    private final ReentrantLock lock = new ReentrantLock(); // Используем ReentrantLock для управления доступом

    // Метод добавления элемента
    public boolean add(E element) {
        lock.lock(); // Блокируем доступ
        try {
            if (contains(element)) { // Проверяем наличие элемента
                return false;
            }
            // Создаем новый массив и добавляем элемент
            Object[] newElements = Arrays.copyOf(elements, elements.length + 1);
            newElements[elements.length] = element;
            elements = newElements; // Обновляем ссылку на массив
            return true;
        } finally {
            lock.unlock(); // Разблокируем доступ
        }
    }

    // Метод удаления элемента
    public boolean remove(E element) {
        lock.lock(); // Блокируем доступ
        try {
            int index = indexOf(element); // Находим индекс элемента
            if (index == -1) {
                return false; // Элемент не найден
            }
            // Создаем новый массив без удаляемого элемента
            Object[] newElements = new Object[elements.length - 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
            elements = newElements; // Обновляем ссылку на массив
            return true;
        } finally {
            lock.unlock(); // Разблокируем доступ
        }
    }

    // Метод проверки элемента
    public boolean contains(E element) {
        Object[] currentArray = elements; // Локальная копия массива
        for (Object obj : currentArray) {
            if (obj.equals(element)) {
                return true;
            }
        }
        return false;
    }

    // Получение всех элементов
    public Object[] toArray() {
        return Arrays.copyOf(elements, elements.length); // Создаем копию
    }

    // Метод получения размера множества
    public int size() {
        return elements.length;
    }

    // Поиск индекса элемента
    private int indexOf(E element) {
        Object[] currentArray = elements;
        for (int i = 0; i < currentArray.length; i++) {
            if (currentArray[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    // Тестирование многопоточности
    public static void main(String[] args) {
        ThreadSafeCopyOnWriteArraySet<String> set = new ThreadSafeCopyOnWriteArraySet<>();

        // Поток для добавления элементов
        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Writer1 добавляет: Element" + i + " -> " + set.add("Element" + i));
                try {
                    Thread.sleep(50); // Задержка для демонстрации
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Еще один поток для добавления других элементов
        Thread writer2 = new Thread(() -> {
            for (int i = 3; i < 8; i++) {
                System.out.println("Writer2 добавляет: Element" + i + " -> " + set.add("Element" + i));
                try {
                    Thread.sleep(50); // Задержка для демонстрации
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Поток для проверки наличия элементов
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 8; i++) {
                System.out.println("Reader проверяет Element" + i + ": " + set.contains("Element" + i));
                try {
                    Thread.sleep(30); // Задержка для демонстрации
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Поток для удаления элементов
        Thread remover = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Remover удаляет Element" + i + " -> " + set.remove("Element" + i));
                try {
                    Thread.sleep(70); // Задержка для демонстрации
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Запуск потоков
        writer1.start();
        writer2.start();
        reader.start();
        remover.start();

        // Ждем завершения всех потоков
        try {
            writer1.join();
            writer2.join();
            reader.join();
            remover.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Итоговый вывод
        System.out.println("Итоговое множество: " + Arrays.toString(set.toArray()));
        System.out.println("Размер множества: " + set.size());
    }
}
