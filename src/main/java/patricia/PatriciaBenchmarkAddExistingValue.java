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
Benchmark                                             (sizeFill)  Mode  Cnt    Score   Error  Units
PatriciaBenchmarkAddExistingValue.addPatriciaTrie          10000  avgt    5  108,823 ? 2,308  ns/op
PatriciaBenchmarkAddExistingValue.addPatriciaTrieSet       10000  avgt    5  139,719 ? 2,609  ns/op
 */
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 7, time = 2)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PatriciaBenchmarkAddExistingValue {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();
    String[] array;

    /* Initial PatriciaTrie and PatriciaTrieSet size
        It starts with values
        A
        AC
        ACA
        ACB
        ACC
        ....
        ACAA
        ACAB
        Until reaches sizeFill elements
    */
    @Param({"10000"})
    int sizeFill;

    // Move constant at benchmark level to prevent constant folding
    String keyA;
    String keyAc;

    // Final object is correct for simplicity we want to inline this object in methods
    final Object cnt = new Object();

    // Counter for tests
    int idx;

    @Setup(Level.Trial)
    public void setupTrial() {
        keyA = "A";
        keyAc = "AC";

        patriciaTrie.clear();
        patriciaTrieSet.clear();

        array = new String[sizeFill];

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
        return patriciaTrie.put(array[(idx++) % sizeFill], cnt);
    }

    @Benchmark
    public boolean addPatriciaTrieSet() {
        return patriciaTrieSet.add(array[(idx++) % sizeFill]);
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
