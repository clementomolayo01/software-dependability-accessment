package com.urlshortener.benchmark;

import com.urlshortener.entity.ShortUrl;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.service.UrlShortenerService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class UrlShortenerBenchmark {

    private ConfigurableApplicationContext context;
    private UrlShortenerService urlShortenerService;
    private ShortUrlRepository shortUrlRepository;
    private String testUrl;
    private String existingShortCode;

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(com.urlshortener.UrlShortenerApplication.class);
        urlShortenerService = context.getBean(UrlShortenerService.class);
        shortUrlRepository = context.getBean(ShortUrlRepository.class);
        testUrl = "https://www.example.com/benchmark";
        existingShortCode = urlShortenerService.shortenUrl(testUrl, "benchmark");
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Benchmark
    public String benchmarkShortenUrl() {
        return urlShortenerService.shortenUrl(testUrl + System.nanoTime(), "benchmark");
    }

    @Benchmark
    public void benchmarkGetOriginalUrl() {
        urlShortenerService.getOriginalUrl(existingShortCode);
    }

    @Benchmark
    public void benchmarkGetStatistics() {
        urlShortenerService.getStatistics(existingShortCode);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UrlShortenerBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

