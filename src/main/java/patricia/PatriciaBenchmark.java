package patricia;

import org.apache.commons.collections4.set.PatriciaTrieSet;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PatriciaBenchmark {

    PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
    PatriciaTrieSet patriciaTrieSet = new PatriciaTrieSet();

    @Param({"1000", "10000", "100000"})
    int size;


    String keyA;
    String keyAc;
    String keyAcc;
    String keyAcaa;
    String keyAcca;

    Object instanceForPatriciaTrie;

    @Setup(Level.Trial)
    public void setup() {
        keyA = "A";
        keyAc = "AC";
        keyAcc = "ACC";
        keyAcaa = "ACAA";
        keyAcca = "ACCA";

        patriciaTrie.clear();
        patriciaTrieSet.clear();

        instanceForPatriciaTrie = new Object();

        patriciaTrie.put(keyA, instanceForPatriciaTrie);
        patriciaTrie.put(keyAc, instanceForPatriciaTrie);
        patriciaTrieSet.add(keyA);
        patriciaTrieSet.add(keyAc);

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Queue<String> queue = new ArrayDeque<>(size / saltChars.length());
        queue.add(keyAc);

        int j = 0;
        while (j < size - 2) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < size; i++) {
                String newValue = value + saltChars.charAt(i);
                patriciaTrieSet.add(newValue);
                patriciaTrie.put(newValue, instanceForPatriciaTrie);
                queue.add(newValue);
                j++;
            }
        }

    }

    @Benchmark
    public SortedMap<String, Object> subMapPatriciaTrie() {
        return patriciaTrie.subMap(keyA, keyAcc);
    }

    @Benchmark
    public SortedSet<String> subSetPatriciaTrieSet() {
        return patriciaTrieSet.subSet(keyA, keyAcc);
    }

    @Benchmark
    public boolean subMapPatriciaTrieContains() {
        SortedMap<String, Object> map = patriciaTrie.subMap(keyA, keyAcc);
        return map.containsKey(keyAc);
    }

    @Benchmark
    public boolean subMapPatriciaTrieSetContains() {
        SortedSet<String> subSet = patriciaTrieSet.subSet(keyA, keyAcc);
        return subSet.contains(keyAc);
    }

    @Benchmark
    public Object subMapPatriciaTrieSetPut() {
        SortedMap<String, Object> map = patriciaTrie.subMap(keyA, keyAcc);
        return map.put(keyAcc, instanceForPatriciaTrie);
    }

    @Benchmark
    public boolean subSetPatriciaTrieSetAdd() {
        SortedSet<String> set = patriciaTrieSet.subSet(keyA, keyAcc);
        return set.add(keyAcc);
    }

    @Benchmark
    public SortedMap<String, Object> prefixMapPatriciaTrie() {
        return patriciaTrie.prefixMap(keyAcc);
    }

    @Benchmark
    public SortedSet<String> prefixSetPatriciaTrieSet() {
        return patriciaTrieSet.prefixSet(keyAcc);
    }

    @Benchmark
    public SortedMap<String, Object> headMapPatriciaTrie() {
        return patriciaTrie.headMap(keyAcc);
    }

    @Benchmark
    public SortedSet<String> headSetPatriciaTrieSet() {
        return patriciaTrieSet.headSet(keyAcc);
    }

    @Benchmark
    public SortedMap<String, Object> tailMapPatriciaTrie() {
        return patriciaTrie.tailMap(keyAcc);
    }

    @Benchmark
    public SortedSet<String> tailSetPatriciaTrieSet() {
        return patriciaTrieSet.tailSet(keyAcc);
    }

    @Benchmark
    public String firstPatriciaTrie() {
        return patriciaTrie.firstKey();
    }

    @Benchmark
    public String firstPatriciaTrieSet() {
        return patriciaTrieSet.first();
    }

    @Benchmark
    public String lastPatriciaTrie() {
        return patriciaTrie.lastKey();
    }

    @Benchmark
    public String lastPatriciaTrieSet() {
        return patriciaTrieSet.last();
    }

    @Benchmark
    public int sizePatriciaTrie() {
        return patriciaTrie.size();
    }

    @Benchmark
    public int sizePatriciaTrieSet() {
        return patriciaTrieSet.size();
    }

    @Benchmark
    public boolean isEmptyPatriciaTrie() {
        return patriciaTrie.isEmpty();
    }

    @Benchmark
    public boolean isEmptyPatriciaTrieSet() {
        return patriciaTrieSet.isEmpty();
    }

    @Benchmark
    public boolean containsPatriciaTrie() {
        return patriciaTrie.containsKey(keyAcca);
    }

    @Benchmark
    public boolean containsPatriciaTrieSet() {
        return patriciaTrieSet.contains(keyAcca);
    }

    @Benchmark
    public void iteratorPatriciaTrie(Blackhole bh) {
        for (String s : patriciaTrie.keySet()) {
            bh.consume(s);
        }
    }

    @Benchmark
    public void iteratorPatriciaTrieSet(Blackhole bh) {
        for (String s : patriciaTrieSet) {
            bh.consume(s);
        }
    }

    @Benchmark
    public void iteratorSubSetPatriciaTrie(Blackhole bh) {
        for (String s : patriciaTrie.subMap(keyA, keyAcc).keySet()) {
            bh.consume(s);
        }
    }

    @Benchmark
    public void iteratorSubSetPatriciaTrieSet(Blackhole bh) {
        for (String s : patriciaTrieSet.subSet(keyA, keyAcc)) {
            bh.consume(s);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PatriciaBenchmark.class.getSimpleName())
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
