package com.github.nizshee;


import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LazyFactoryTest {

    @Test
    public void createLazySimpleTest() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<String> supplier = () -> {
            int count = counter.incrementAndGet();
            return new String(new char[count]).replace("\0", "a");
        };
        Lazy<String> lazy = LazyFactory.createLazySimple(supplier);

        IntStream.range(0, 4).forEach(i -> assertEquals(lazy.get(), "a"));
        assertEquals(1, counter.get());
    }

    @Test
    public void createLazyConcurrentTest() throws Exception {
        IntStream.range(0, 4).forEach(i -> {
            final AtomicInteger counter = new AtomicInteger(0);
            Supplier<String> supplier = () -> {
                int count = counter.incrementAndGet();
                return new String(new char[count]).replace("\0", "a");
            };
            Lazy<String> lazy = LazyFactory.createLazyConcurent(supplier);

            IntStream.range(0, 10000).parallel().forEach(j -> assertEquals(lazy.get(), "a"));
            assertEquals(counter.get(), 1);
        });
    }

    @Test
    public void createLazyLockFreeTest() throws Exception {
        IntStream.range(0, 4).forEach(i -> {
            final AtomicInteger counter = new AtomicInteger(0);
            Supplier<String> supplier = () -> {
                int count = counter.incrementAndGet();
                return new String(new char[count]).replace("\0", "a");
            };
            Lazy<String> lazy = LazyFactory.createLazyLockFree(supplier);

            IntStream.range(0, 10000).parallel().forEach(j -> assertEquals(lazy.get(), "a"));
            assertTrue(counter.get() > 0);
        });
    }
}
