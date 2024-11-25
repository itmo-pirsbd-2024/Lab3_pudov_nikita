# Lab3 Concurrent Data Structure

В качестве задания была взята структура CopyOnWriteArraySet 
и имплементированы две реализации - через Atomic Reference (AtomicCopyOnWriteArraySet) и ReentrantLock (ThreadSafeCopyOnWriteArraySet)

Для каждой реализации был создан бенчмарк, осуществлено профилирование и продемонстрированы фрагменты JIT-компиляции

## Результаты бенчмарка

![result_benchmark_lab3.png](assets/result_benchmark_lab3.png)

По результатам бенчмарка видно,что операции добавления, удаления, проверки наличия элементов происходит за сопоставимое время у обеих реализаций.

## Результаты профилирования

Операции добавления AtomicCopyOnWriteArraySet и ThreadSafeCopyOnWriteArraySet

![AtomicCopyOnWriteArraySetAdd_flamegraph.png](assets/AtomicCopyOnWriteArraySetAdd_flamegraph.png)

![ThreadSafeCopyOnWriteArraySetAdd_flamegraph.png](assets/ThreadSafeCopyOnWriteArraySetAdd_flamegraph.png)

Операции удаления AtomicCopyOnWriteArraySet и ThreadSafeCopyOnWriteArraySet

![AtomicCopyOnWriteArraySetRemove_flamegraph.png](assets/AtomicCopyOnWriteArraySetRemove_flamegraph.png)

![ThreadSafeCopyOnWriteArraySetRemove_flamegraph.png](assets/ThreadSafeCopyOnWriteArraySetRemove_flamegraph.png)

Операции проверки наличия элемента AtomicCopyOnWriteArraySet и ThreadSafeCopyOnWriteArraySet

![AtomicCopyOnWriteArraySetContains_flamegraph.png](assets/AtomicCopyOnWriteArraySetContains_flamegraph.png)

![ThreadSafeCopyOnWriteArraySetContains_flamegraph.png](assets/ThreadSafeCopyOnWriteArraySetContains_flamegraph.png)

## JIT-компиляция

В первом случае мы видим процесс работы реализации на Atomic

![AtomicCopyOnWriteArraySet_JIT_1.png](assets/AtomicCopyOnWriteArraySet_JIT_1.png)

Во втором случае уже реализация на основе блокировок

![ThreadSafeCopyOnWriteArraySet_JIT_2.png](assets/ThreadSafeCopyOnWriteArraySet_JIT_2.png)


