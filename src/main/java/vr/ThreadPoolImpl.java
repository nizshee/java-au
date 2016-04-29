package vr;


import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ThreadPoolImpl {

    private final List<Thread> threads = new LinkedList<>();
    private final LinkedList<Future> taskList = new LinkedList<>();

    public ThreadPoolImpl(int threadCount) {
        IntStream.range(0, threadCount).forEach(i -> threads.add(new Thread(() -> {
            Future future = null;
            while (!Thread.interrupted()) {
                try {
                    synchronized (taskList) {
                        while (taskList.isEmpty()) {
                            if (Thread.interrupted()) throw new InterruptedException();
                            taskList.wait();
                        }
                        future = taskList.poll();
                    }
                    future.calculateResult();
                    future = null;
                } catch (InterruptedException e) {
                    break;
                }
            }
        })));
        threads.forEach(Thread::start);
    }

    public void shutdown() {
        synchronized (taskList) {
            taskList.forEach(future -> future.corrupt(new LightExecutionException()));
            taskList.clear();
        }
        threads.forEach(Thread::interrupt);
    }


    public <T> LightFuture<T> submit(Supplier<T> supplier) {
        Future<T> future = new Future<>(supplier);
        synchronized (taskList) {
            taskList.add(future);
            taskList.notify();
        }
        return future;
    }

    public class Future<T> implements LightFuture<T> {

        private final Supplier<T> supplier;
        private volatile boolean isReady = false;
        private volatile Exception exception = null;
        private volatile T result = null;
        private final List<Future> dependentTasks = new LinkedList<>();

        public Future(Supplier<T> supplierForFuture) {
            supplier = supplierForFuture;
        }

        public boolean isReady() {
            return isReady;
        }

        public T get() throws Exception {
            if (!isReady) {
                synchronized (this) {
                    while (!isReady) {
                        this.wait();
                    }
                }
            }
            if (exception != null) throw exception;
            return result;
        }

        public <Y> LightFuture<Y> thenApply(Function<? super T, Y> function) {
            Future<Y> future = new Future<>(() -> function.apply(getResult()));
            synchronized (dependentTasks) {
                if (isReady) {
                    if (exception != null) {
                        future.corrupt(exception);
                    } else {
                        synchronized (taskList) {
                            taskList.add(future);
                            taskList.notify();
                        }
                    }
                } else {
                    dependentTasks.add(future);
                }
            }
            return future;
        }

        private void calculateResult() {
            try {
                result = supplier.get();
                synchronized (dependentTasks) {
                    synchronized (taskList) {
                        taskList.addAll(dependentTasks);
                        taskList.notifyAll();
                        dependentTasks.clear();
                    }
                    isReady = true;
                }
            } catch (Exception e) {
                corrupt(new LightExecutionException());
            }

            synchronized (this) {
                this.notifyAll();
            }
        }

        private T getResult() {
            return result;
        }

        private void corrupt(Exception exception) {
            this.exception = exception;
            synchronized (dependentTasks) {
                dependentTasks.forEach(future -> future.corrupt(exception));
                dependentTasks.clear();
                isReady = true;
            }
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
}
