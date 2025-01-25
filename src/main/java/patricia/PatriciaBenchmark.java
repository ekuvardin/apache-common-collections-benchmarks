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
    PatriciaTrieSet set = new PatriciaTrieSet();


    @Setup(Level.Trial)
    public void setup() {
        patriciaTrie.clear();
        set.clear();

        Object cnt = new Object();
        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        patriciaTrie.put("A", cnt);
        patriciaTrie.put("AC", cnt);
        set.add("A");
        set.add("AC");
        for (int i = 0; i < saltChars.length(); i++) {
            patriciaTrie.put("AC" + saltChars.charAt(i), cnt);
            set.add("AC" + saltChars.charAt(i));
        }

    }

   /* @Benchmark
    public SortedMap<String, Object> subMapPatriciaTrie() {
        return patriciaTrie.subMap("A", "ACC");
    }

    @Benchmark
    public SortedSet<String> subMapPatriciaTrieSet() {
        return set.subSet("A", "ACC");
    }

    @Benchmark
    public boolean subMapPatriciaTrieContains() {
        SortedMap<String, Object> map = patriciaTrie.subMap("A", "ACC");
        return map.containsKey("AC");
    }

    @Benchmark
    public boolean subMapPatriciaTrieSetContains() {
        SortedSet<String> subSet = set.subSet("A", "ACC");
        return subSet.contains("AC");
    }*/

    @Benchmark
    public boolean subMapPatriciaTrieSetAdd() {
        SortedMap<String, Object> map = patriciaTrie.subMap("A", "ACC");
        return true;
    }


    public static void main(String[] args) throws RunnerException {
        PatriciaTrieSet set = new PatriciaTrieSet();
        PatriciaTrie<Object> patriciaTrie = new PatriciaTrie<>();
        set.add("A");
        set.add("AC");

        patriciaTrie.put("A", "A");
        patriciaTrie.put("AC", "A");


        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < saltChars.length(); i++) {
            set.add("AC" + saltChars.charAt(i));
            patriciaTrie.put("AC" + saltChars.charAt(i), "A");
        }
        SortedSet<String> s = set.subSet("A", "ACC");
        s.contains("AC");
        s.reversed();

        patriciaTrie.subMap("A", "ACC").put("A","B");
        patriciaTrie.containsKey("AC");


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

