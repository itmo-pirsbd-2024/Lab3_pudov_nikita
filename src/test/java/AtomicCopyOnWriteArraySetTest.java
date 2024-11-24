import org.junit.jupiter.api.Test;
import ru.highloadjava.concurrentlab.AtomicCopyOnWriteArraySet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AtomicCopyOnWriteArraySetTest {

    @Test
    void testAddAndContains() {
        AtomicCopyOnWriteArraySet<String> set = new AtomicCopyOnWriteArraySet<>();
        assertTrue(set.add("Element1"));
        assertTrue(set.add("Element2"));
        assertFalse(set.add("Element1")); // Дубликат
        assertTrue(set.contains("Element1"));
        assertFalse(set.contains("Element3"));
    }

    @Test
    void testRemove() {
        AtomicCopyOnWriteArraySet<String> set = new AtomicCopyOnWriteArraySet<>();
        set.add("Element1");
        set.add("Element2");
        assertTrue(set.remove("Element1"));
        assertFalse(set.contains("Element1"));
        assertFalse(set.remove("Element3")); // Элемента нет
    }

    @Test
    void testConcurrency() throws InterruptedException {
        AtomicCopyOnWriteArraySet<Integer> set = new AtomicCopyOnWriteArraySet<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Добавляем элементы из нескольких потоков
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    set.add(threadId * 100 + j);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Проверяем размер множества
        assertEquals(1000, set.size());
    }

    @Test
    void testToArray() {
        AtomicCopyOnWriteArraySet<String> set = new AtomicCopyOnWriteArraySet<>();
        set.add("Element1");
        set.add("Element2");
        Object[] elements = set.toArray();
        assertEquals(2, elements.length);
        assertArrayEquals(new Object[]{"Element1", "Element2"}, elements);
    }
}
