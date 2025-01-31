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

@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 7, time = 2)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PatriciaBenchmarkAddExistingValue {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();
    String[] array;

    @Param({"10000"})
    int sizeFill;

    @Param({"10000"})
    int sizeTryToAdd;

    String keyA;
    String keyAc;
    final Object cnt = new Object();

    int idx;

    @Setup(Level.Trial)
    public void setupTrial() {
        keyA = "A";
        keyAc = "AC";

        patriciaTrie.clear();
        patriciaTrieSet.clear();

        array = new String[sizeTryToAdd];

        patriciaTrie.put(keyA, cnt);
        patriciaTrie.put(keyAc, cnt);
        patriciaTrieSet.add(keyA);
        patriciaTrieSet.add(keyAc);

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
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
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
                .include(PatriciaBenchmarkAddExistingValue.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(2))
                .measurementTime(TimeValue.seconds(2))
                .measurementIterations(5)
                .warmupIterations(7)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }
}
