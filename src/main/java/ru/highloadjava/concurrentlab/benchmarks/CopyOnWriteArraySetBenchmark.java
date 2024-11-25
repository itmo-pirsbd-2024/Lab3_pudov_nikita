package ru.highloadjava.concurrentlab.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.highloadjava.concurrentlab.AtomicCopyOnWriteArraySet;
import ru.highloadjava.concurrentlab.ThreadSafeCopyOnWriteArraySet;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS) // Единица измерения
@State(Scope.Thread) // Каждому потоку выделяется своя копия состояния
@Warmup(iterations = 3, time = 300, timeUnit = TimeUnit.MILLISECONDS)
public class CopyOnWriteArraySetBenchmark {

    @Param({"10000"})
    private int SIZE;

    private AtomicCopyOnWriteArraySet<Integer> atomicSet;
    private ThreadSafeCopyOnWriteArraySet<Integer> threadSafeSet;
    private Random random = new Random(5L);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CopyOnWriteArraySetBenchmark.class.getSimpleName())
                .forks(1)
                .jvmArgs("-Xms4G", "-Xmx8G", "-XX:+UnlockDiagnosticVMOptions",
                        "-XX:+PrintCompilation", "-XX:+PrintInlining",
                        "-Djmh.stack.profiles=true")  // Включение профилирования стека
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Iteration)
    public void setup() {
        atomicSet = new AtomicCopyOnWriteArraySet<>();
        threadSafeSet = new ThreadSafeCopyOnWriteArraySet<>();
        random = new Random();

        for (int i = 0; i < SIZE; i++) {
            atomicSet.add(i);
            threadSafeSet.add(i);
        }
    }

    @Benchmark
    public void testAtomicCopyOnWriteArraySetAdd(Blackhole bh) {
        int value = random.nextInt(SIZE * 2);
        bh.consume(atomicSet.add(value));
    }

    @Benchmark
    public void testThreadSafeCopyOnWriteArraySetAdd(Blackhole bh) {
        int value = random.nextInt(SIZE * 2);
        bh.consume(threadSafeSet.add(value));
    }

    @Benchmark
    public void testAtomicCopyOnWriteArraySetRemove(Blackhole bh) {
        int value = random.nextInt(SIZE);
        bh.consume(atomicSet.remove(value));
    }

    @Benchmark
    public void testThreadSafeCopyOnWriteArraySetRemove(Blackhole bh) {
        int value = random.nextInt(SIZE);
        bh.consume(threadSafeSet.remove(value));
    }

    @Benchmark
    public void testAtomicCopyOnWriteArraySetContains(Blackhole bh) {
        int value = random.nextInt(SIZE * 2);
        bh.consume(atomicSet.contains(value));
    }

    @Benchmark
    public void testThreadSafeCopyOnWriteArraySetContains(Blackhole bh) {
        int value = random.nextInt(SIZE * 2);
        bh.consume(threadSafeSet.contains(value));
    }

    //@Benchmark
    public void testAtomicCopyOnWriteArraySetSize(Blackhole bh) {
        bh.consume(atomicSet.size());
    }

    //@Benchmark
    public void testThreadSafeCopyOnWriteArraySetSize(Blackhole bh) {
        bh.consume(threadSafeSet.size());
    }
}
