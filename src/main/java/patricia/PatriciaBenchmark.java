package patricia;

import org.apache.commons.collections4.set.PatriciaTrieSet;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.SortedMap;
import java.util.SortedSet;

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

    int size = 1000;

    @Setup(Level.Trial)
    public void setup() {
        patriciaTrie.clear();
        patriciaTrieSet.clear();

        Object cnt = new Object();

        patriciaTrie.put("A", cnt);
        patriciaTrie.put("AC", cnt);
        patriciaTrieSet.add("A");
        patriciaTrieSet.add("AC");

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Queue<String> queue = new ArrayDeque<>(size / saltChars.length());
        queue.add("AC");

        int j = 0;
        while (j < size) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < size; i++) {
                String newValue = value + saltChars.charAt(i);
                patriciaTrieSet.add(newValue);
                patriciaTrie.put(newValue, "A");
                queue.add(newValue);
                j++;
            }
        }

    }

    @Benchmark
    public SortedMap<String, Object> subMapPatriciaTrie() {
        return patriciaTrie.subMap("A", "ACC");
    }

    @Benchmark
    public SortedSet<String> subSetPatriciaTrieSet() {
        return patriciaTrieSet.subSet("A", "ACC");
    }

    @Benchmark
    public boolean subMapPatriciaTrieContains() {
        SortedMap<String, Object> map = patriciaTrie.subMap("A", "ACC");
        return map.containsKey("AC");
    }

    @Benchmark
    public boolean subMapPatriciaTrieSetContains() {
        SortedSet<String> subSet = patriciaTrieSet.subSet("A", "ACC");
        return subSet.contains("AC");
    }

    @Benchmark
    public Object subMapPatriciaTrieSetPut() {
        SortedMap<String, Object> map = patriciaTrie.subMap("A", "ACC");
        return map.put("ACAA","A");
    }

    @Benchmark
    public boolean subSetPatriciaTrieSetAdd() {
        SortedSet<String> set = patriciaTrieSet.subSet("A", "ACC");
        return set.add("ACAA");
    }

    @Benchmark
    public SortedMap<String, Object> prefixMapPatriciaTrie() {
        return patriciaTrie.prefixMap("ACC");
    }

    @Benchmark
    public SortedSet<String> prefixSetPatriciaTrieSet() {
        return patriciaTrieSet.prefixSet("ACC");
    }

    @Benchmark
    public SortedMap<String, Object> headMapPatriciaTrie() {
        return patriciaTrie.headMap("ACC");
    }

    @Benchmark
    public SortedSet<String> headSetPatriciaTrieSet() {
        return patriciaTrieSet.headSet("ACC");
    }

    @Benchmark
    public SortedMap<String, Object> tailMapPatriciaTrie() {
        return patriciaTrie.tailMap("ACC");
    }

    @Benchmark
    public SortedSet<String> tailSetPatriciaTrieSet() {
        return patriciaTrieSet.tailSet("ACC");
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


    public static void main(String[] args) throws RunnerException {
     /*   PatriciaTrieSet set = new PatriciaTrieSet();
        PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
        set.add("A");
        set.add("AC");

        patriciaTrie.put("A", "A");
        patriciaTrie.put("AC", "A");


        String saltChars = "ABC";
        Queue<String> queue = new ArrayDeque<>(1000 / saltChars.length());
        queue.add("AC");

        int j = 0;
        while (j < 1000) {
            String value = queue.poll();
            for (int i = 0; i < saltChars.length() && j < 1000; i++) {
                String newValue = value + saltChars.charAt(i);
                set.add(newValue);
                patriciaTrie.put(newValue, "A");
                queue.add(newValue);
                j++;
            }
        }

        SortedSet<String> s = set.subSet("A", "ACC");
        s.contains("AC");
        s.reversed();

        patriciaTrie.subMap("A", "ACC").put("A", "B");
        patriciaTrie.containsKey("AC");*/


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

