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
Benchmark                                      (size)  Mode  Cnt   Score   Error  Units
PatriciaBenchmarkRemove.removePatriciaTrie       1000  avgt    5  20,971 � 0,255  ns/op
PatriciaBenchmarkRemove.removePatriciaTrie      10000  avgt    5  20,980 � 0,248  ns/op
PatriciaBenchmarkRemove.removePatriciaTrie     100000  avgt    5  22,997 � 0,655  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet    1000  avgt    5  21,295 � 0,266  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet   10000  avgt    5  21,595 � 0,158  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet  100000  avgt    5  23,114 � 0,473  ns/op

Don't INLINE
Benchmark                                      (size)  Mode  Cnt   Score   Error  Units
PatriciaBenchmarkRemove.removePatriciaTrie       1000  avgt    5  24,099 � 0,285  ns/op
PatriciaBenchmarkRemove.removePatriciaTrie      10000  avgt    5  23,887 � 0,207  ns/op
PatriciaBenchmarkRemove.removePatriciaTrie     100000  avgt    5  26,691 � 0,862  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet    1000  avgt    5  25,114 � 0,214  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet   10000  avgt    5  24,364 � 0,417  ns/op
PatriciaBenchmarkRemove.removePatriciaTrieSet  100000  avgt    5  26,751 � 0,905  ns/op
 */
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 7)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PatriciaBenchmarkRemove {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();
    static String[] array;

    @Param({"1000","10000","100000"})
    int size;

    String keyA;
    String keyAc;
    String keyAcc;
    String keyAcaa;
    String keyAcca;
    Object cnt;

    @Setup(Level.Trial)
    public void setupTrial() {
        keyA = "A";
        keyAc = "AC";
        keyAcc = "ACC";
        keyAcaa = "ACAA";
        keyAcca = "ACCA";

        patriciaTrie.clear();
        patriciaTrieSet.clear();

        array = new String[size - 2];
        cnt = new Object();

        patriciaTrie.put(keyA, cnt);
        patriciaTrie.put(keyAc, cnt);
        patriciaTrieSet.add(keyA);
        patriciaTrieSet.add(keyAc);

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Queue<String> queue = new ArrayDeque<>(size / saltChars.length());

        queue.add(keyAc);

        int j = 0;
        while (j < size - 2) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < size - 2; i++) {
                String newValue = value + saltChars.charAt(i);
                patriciaTrieSet.add(newValue);
                patriciaTrie.put(newValue, cnt);
                queue.add(newValue);
                array[j] = newValue;
                j++;
            }
        }
    }

    @State(Scope.Benchmark)
    public static class NextToIterate {
        String[] values;

        int idx;

        public NextToIterate() {
            values = array;
            idx = 0;
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public int getNextIdx() {
            return (idx++) % values.length;
        }

        @CompilerControl(CompilerControl.Mode.DONT_INLINE)
        public String getNextValue() {
            return values[getNextIdx()];
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
    public Object removePatriciaTrie(NextToIterate nextToIterate) {
        return patriciaTrie.remove(nextToIterate.getNextValue());
    }

    @Benchmark
    public boolean removePatriciaTrieSet(NextToIterate nextToIterate) {
        return patriciaTrieSet.remove(nextToIterate.getNextValue());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PatriciaBenchmarkRemove.class.getSimpleName())
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
