package patricia;

import org.apache.commons.collections4.set.PatriciaTrieSet;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/*
Benchmark                                      (sizeFill)  (sizeTryToRemove)  Mode  Cnt    Score   Error  Units
PatriciaBenchmarkRemove.removePatriciaTrie         100000             100000  avgt    5  133,199 ? 0,989  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet      100000             100000  avgt    5  134,057 ? 0,960  ns/op
 */
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 10, time = 5)
@Measurement(iterations = 5, time = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PatriciaBenchmarkRemove {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();
    String[] array;

    @Param({"100000"})
    int sizeFill;

    @Param({"100000"})
    int sizeTryToRemove;

    String keyA;
    String keyAc;
    // Final object is correct for simplicity we want to inline this object in methods
    final Object cnt = new Object();
    int idx;

    @Setup(Level.Trial)
    public void setupTrial() {
        keyA = "A";
        keyAc = "AC";

        patriciaTrie.clear();
        patriciaTrieSet.clear();

        array = new String[sizeTryToRemove];

        patriciaTrie.put(keyA, cnt);
        patriciaTrie.put(keyAc, cnt);
        patriciaTrieSet.add(keyA);
        patriciaTrieSet.add(keyAc);

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Queue<String> queue = new ArrayDeque<>(sizeFill / saltChars.length());

        queue.add(keyAc);

        int j = 2;
        while (j < sizeFill) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < sizeFill; i++) {
                String newValue = value + saltChars.charAt(i);
                patriciaTrieSet.add(newValue);
                patriciaTrie.put(newValue, cnt);
                queue.add(newValue);
                array[j] = newValue;
                j++;
            }
        }

        queue.clear();
        queue.add("DE");

        j = 0;
        while (j < sizeTryToRemove) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < sizeTryToRemove; i++) {
                String newValue = value + saltChars.charAt(i);
                queue.add(newValue);
                patriciaTrie.put(newValue, cnt);
                array[j] = newValue;
                j++;
            }
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        for (String s : array) {
            patriciaTrie.put(s, cnt);
            patriciaTrieSet.add(s);
        }
    }

    @Benchmark
    public Object removePatriciaTrie() {
        return patriciaTrie.remove(array[(idx++) % sizeTryToRemove]);
    }

    @Benchmark
    public boolean removePatriciaTrieSet() {
        return patriciaTrieSet.remove(array[(idx++) % sizeTryToRemove]);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PatriciaBenchmarkRemove.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(2))
                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .warmupIterations(7)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }
}
