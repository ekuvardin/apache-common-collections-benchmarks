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
Benchmark                                        (sizeFill)  (sizeTryToAdd)  Mode  Cnt    Score   Error  Units
PatriciaBenchmarkAddNewValue.addPatriciaTrie          10000          100000  avgt    5  158,198 ? 1,014  ns/op
PatriciaBenchmarkAddNewValue.addPatriciaTrieSet       10000          100000  avgt    5  175,539 ? 3,196  ns/op
 */
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 7, time = 2)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PatriciaBenchmarkAddNewValue {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();
    String[] array;

    @Param({"10000"})
    int sizeFill;

    @Param({"100000"})
    int sizeTryToAdd;

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

        array = new String[sizeTryToAdd];

        patriciaTrieSet.add(keyA);
        patriciaTrieSet.add(keyAc);
        patriciaTrie.put(keyA, cnt);
        patriciaTrie.put(keyAc, cnt);


        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Queue<String> queue = new ArrayDeque<>(sizeFill / saltChars.length());

        queue.add(keyAc);
        array[0] = keyAc;
        array[1] = keyA;
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
        while (j < sizeTryToAdd) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < sizeTryToAdd; i++) {
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
            patriciaTrie.remove(s);
            patriciaTrieSet.remove(s);
        }

        idx = 0;
    }

    @Benchmark
    public Object addPatriciaTrie() {
        return patriciaTrie.put(array[(idx++) % sizeTryToAdd], cnt);
    }

    @Benchmark
    public boolean addPatriciaTrieSet() {
        return patriciaTrieSet.add(array[(idx++) % sizeTryToAdd]);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PatriciaBenchmarkAddNewValue.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(2))
                .measurementTime(TimeValue.seconds(2))
                .measurementIterations(5)
                .warmupIterations(7)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }
}
