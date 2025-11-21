package com.urlshortener.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class HashingBenchmark {

    private static final int SHORT_CODE_LENGTH = 8;
    private String testUrl;

    @Setup(Level.Iteration)
    public void setup() {
        testUrl = "https://www.example.com/benchmark/" + System.nanoTime();
    }

    @Benchmark
    public String benchmarkSha256Hashing() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(testUrl.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        
        String code = encoded.substring(0, Math.min(SHORT_CODE_LENGTH, encoded.length()))
                .replaceAll("[^a-zA-Z0-9]", "A");
        
        while (code.length() < SHORT_CODE_LENGTH) {
            code += "A";
        }
        
        return code.substring(0, SHORT_CODE_LENGTH);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashingBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

