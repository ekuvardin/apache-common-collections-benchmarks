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

/*
Benchmark                                             (size)  Mode  Cnt        Score       Error  Units
PatriciaBenchmarkSimpleOperation.containsPatriciaTrie                  1000  avgt    5       48,596 ?     1,230  ns/op
PatriciaBenchmarkSimpleOperation.containsPatriciaTrie                 10000  avgt    5       51,476 ?     0,824  ns/op
PatriciaBenchmarkSimpleOperation.containsPatriciaTrie                100000  avgt    5       51,834 ?     0,413  ns/op
PatriciaBenchmarkSimpleOperation.containsPatriciaTrieSet               1000  avgt    5       48,540 ?     1,174  ns/op
PatriciaBenchmarkSimpleOperation.containsPatriciaTrieSet              10000  avgt    5       52,369 ?     0,398  ns/op
PatriciaBenchmarkSimpleOperation.containsPatriciaTrieSet             100000  avgt    5       52,190 ?     0,547  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrie                     1000  avgt    5        6,270 ?     0,093  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrie                    10000  avgt    5        6,262 ?     0,061  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrie                   100000  avgt    5        6,230 ?     0,187  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrieSet                  1000  avgt    5        6,817 ?     0,066  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrieSet                 10000  avgt    5        6,751 ?     0,088  ns/op
PatriciaBenchmarkSimpleOperation.firstPatriciaTrieSet                100000  avgt    5        6,737 ?     0,085  ns/op
PatriciaBenchmarkSimpleOperation.headMapPatriciaTrie                   1000  avgt    5        6,687 ?     0,023  ns/op
PatriciaBenchmarkSimpleOperation.headMapPatriciaTrie                  10000  avgt    5        6,808 ?     0,231  ns/op
PatriciaBenchmarkSimpleOperation.headMapPatriciaTrie                 100000  avgt    5        6,819 ?     0,319  ns/op
PatriciaBenchmarkSimpleOperation.headSetPatriciaTrieSet                1000  avgt    5        9,267 ?     0,075  ns/op
PatriciaBenchmarkSimpleOperation.headSetPatriciaTrieSet               10000  avgt    5        9,343 ?     0,211  ns/op
PatriciaBenchmarkSimpleOperation.headSetPatriciaTrieSet              100000  avgt    5        9,330 ?     0,154  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrie                   1000  avgt    5        0,792 ?     0,019  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrie                  10000  avgt    5        0,799 ?     0,005  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrie                 100000  avgt    5        0,889 ?     0,060  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrieSet                1000  avgt    5        1,021 ?     0,180  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrieSet               10000  avgt    5        1,186 ?     0,195  ns/op
PatriciaBenchmarkSimpleOperation.isEmptyPatriciaTrieSet              100000  avgt    5        1,024 ?     0,155  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrie                  1000  avgt    5    10405,896 ?   202,444  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrie                 10000  avgt    5   123151,828 ? 34419,185  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrie                100000  avgt    5  1727588,628 ? 25520,548  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrieSet               1000  avgt    5    10049,078 ?   130,071  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrieSet              10000  avgt    5   141096,432 ?  1309,961  ns/op
PatriciaBenchmarkSimpleOperation.iteratorPatriciaTrieSet             100000  avgt    5  1664385,856 ?  8252,919  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrie            1000  avgt    5     6866,638 ?   235,368  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrie           10000  avgt    5    27320,421 ?   359,976  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrie          100000  avgt    5   718470,198 ? 16591,016  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrieSet         1000  avgt    5     7169,227 ?    51,483  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrieSet        10000  avgt    5    27232,041 ?   819,069  ns/op
PatriciaBenchmarkSimpleOperation.iteratorSubSetPatriciaTrieSet       100000  avgt    5   742578,217 ?  5893,876  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrie                      1000  avgt    5       16,504 ?     0,077  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrie                     10000  avgt    5       16,445 ?     0,250  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrie                    100000  avgt    5       23,058 ?     0,407  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrieSet                   1000  avgt    5       18,047 ?     0,138  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrieSet                  10000  avgt    5       17,766 ?     0,502  ns/op
PatriciaBenchmarkSimpleOperation.lastPatriciaTrieSet                 100000  avgt    5       24,725 ?     0,513  ns/op
PatriciaBenchmarkSimpleOperation.prefixMapPatriciaTrie                 1000  avgt    5        8,907 ?     0,046  ns/op
PatriciaBenchmarkSimpleOperation.prefixMapPatriciaTrie                10000  avgt    5        8,943 ?     0,153  ns/op
PatriciaBenchmarkSimpleOperation.prefixMapPatriciaTrie               100000  avgt    5        8,887 ?     0,013  ns/op
PatriciaBenchmarkSimpleOperation.prefixSetPatriciaTrieSet              1000  avgt    5       11,372 ?     0,046  ns/op
PatriciaBenchmarkSimpleOperation.prefixSetPatriciaTrieSet             10000  avgt    5       11,398 ?     0,036  ns/op
PatriciaBenchmarkSimpleOperation.prefixSetPatriciaTrieSet            100000  avgt    5       11,341 ?     0,012  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrie                      1000  avgt    5        0,948 ?     0,119  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrie                     10000  avgt    5        0,803 ?     0,052  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrie                    100000  avgt    5        0,889 ?     0,073  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrieSet                   1000  avgt    5        1,038 ?     0,137  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrieSet                  10000  avgt    5        1,102 ?     0,201  ns/op
PatriciaBenchmarkSimpleOperation.sizePatriciaTrieSet                 100000  avgt    5        1,250 ?     0,248  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrie                    1000  avgt    5        8,460 ?     0,051  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrie                   10000  avgt    5        8,982 ?     0,036  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrie                  100000  avgt    5        8,413 ?     0,044  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieContains            1000  avgt    5       25,776 ?     0,592  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieContains           10000  avgt    5       26,233 ?     0,247  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieContains          100000  avgt    5       26,073 ?     0,307  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieSetContains         1000  avgt    5       26,864 ?     0,812  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieSetContains        10000  avgt    5       27,708 ?     0,398  ns/op
PatriciaBenchmarkSimpleOperation.subMapPatriciaTrieSetContains       100000  avgt    5       26,643 ?     0,286  ns/op
PatriciaBenchmarkSimpleOperation.subSetPatriciaTrieSet                 1000  avgt    5       11,157 ?     0,400  ns/op
PatriciaBenchmarkSimpleOperation.subSetPatriciaTrieSet                10000  avgt    5       11,364 ?     0,049  ns/op
PatriciaBenchmarkSimpleOperation.subSetPatriciaTrieSet               100000  avgt    5       11,009 ?     0,293  ns/op
PatriciaBenchmarkSimpleOperation.tailMapPatriciaTrie                   1000  avgt    5        6,842 ?     0,057  ns/op
PatriciaBenchmarkSimpleOperation.tailMapPatriciaTrie                  10000  avgt    5        6,839 ?     0,034  ns/op
PatriciaBenchmarkSimpleOperation.tailMapPatriciaTrie                 100000  avgt    5        6,850 ?     0,029  ns/op
PatriciaBenchmarkSimpleOperation.tailSetPatriciaTrieSet                1000  avgt    5        9,305 ?     0,007  ns/op
PatriciaBenchmarkSimpleOperation.tailSetPatriciaTrieSet               10000  avgt    5        9,508 ?     0,196  ns/op
PatriciaBenchmarkSimpleOperation.tailSetPatriciaTrieSet              100000  avgt    5        9,338 ?     0,036  ns/op
 */
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 7, time = 2)
@Measurement(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PatriciaBenchmarkSimpleOperation {

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
                .include(PatriciaBenchmarkSimpleOperation.class.getSimpleName())
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
