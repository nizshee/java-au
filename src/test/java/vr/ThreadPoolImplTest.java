package vr;

import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.IntStream;

import static org.junit.Assert.*;


public class ThreadPoolImplTest {

    public static final int THREAD_COUNT = 4;
    public static final int TIME_TO_SLEEP = 10;
    public static final int COUNT_OF_TASKS = 100;

    @Test
    public void getTest() throws Exception {
        LightFuture[] futures = new LightFuture[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);

        IntStream.range(0, COUNT_OF_TASKS).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            return i;
        }));

        for (int i = 0; i < COUNT_OF_TASKS; ++i) {
            assertEquals(i, futures[i].get());
        }

        pool.shutdown();
    }

    @Test
    @SuppressWarnings("all")
    public void numberOfThreadsTest() throws Exception {
        LightFuture[] futures = new LightFuture[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);
        Set<Long> threadIds = new ConcurrentSkipListSet<>(); // hope i can use it instead of synchronized
        IntStream.range(0, COUNT_OF_TASKS).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            threadIds.add(Thread.currentThread().getId());
            return i;
        }));

        for (int i = 0; i < COUNT_OF_TASKS; ++i) {
            futures[i].get();
        }
        if (TIME_TO_SLEEP >= 10) { // if time to small, then one process can do all work
            assertEquals(threadIds.size(), THREAD_COUNT);
        }

        pool.shutdown();
    }

    @Test
    public void isReadyTest() throws Exception {
        LightFuture[] futures = new LightFuture[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);

        IntStream.range(0, COUNT_OF_TASKS).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            return i;
        }));

        for (int i = 0; i < COUNT_OF_TASKS; ++i) {
            futures[i].isReady();
            futures[i].get();
            assertTrue(futures[i].isReady());
        }

        pool.shutdown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void thenApplyTest() throws Exception {
        LightFuture<Integer>[] futures = new LightFuture[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);

        IntStream.range(0, COUNT_OF_TASKS).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            return i;
        }));

        for (int i = 0; i < COUNT_OF_TASKS / 2; ++i) {
            futures[i] = futures[i].thenApply(n -> n + 2);
        }

        futures[COUNT_OF_TASKS - 1].get(); // wait until all tasks complete

        for (int i = COUNT_OF_TASKS / 2; i < COUNT_OF_TASKS; ++i) {
            futures[i] = futures[i].thenApply(n -> n + 2);
        }

        for (int i = 0; i < COUNT_OF_TASKS; ++i) {
            assertEquals(i + 2, futures[i].get().intValue());
        }

        pool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void thenExceptionAfterShutdown() throws Exception {
        LightFuture[] futures = new LightFuture[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);

        IntStream.range(0, COUNT_OF_TASKS).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            return i;
        }));

        pool.shutdown();

        for (int i = 0; i < COUNT_OF_TASKS; ++i) {
            futures[i].get();
        }
    }

    @Test(expected = LightExecutionException.class)
    public void thenExceptionInside() throws Exception {
        LightFuture[] futures = new LightFuture[COUNT_OF_TASKS + 1];
        boolean[] array = new boolean[COUNT_OF_TASKS];
        ThreadPoolImpl pool = new ThreadPoolImpl(THREAD_COUNT);

        IntStream.range(0, COUNT_OF_TASKS + 1).forEach(i -> futures[i] = pool.submit(() -> {
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException ignored) {
            }
            array[i] = true; // out of bounds exception
            return i;
        }));

        pool.shutdown();

        for (int i = 0; i < COUNT_OF_TASKS + 1; ++i) {
            futures[i].get();
        }

        assertFalse(array[COUNT_OF_TASKS]);
    }
}
