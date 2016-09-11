package com.github.nizshee;


import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class LazyFactory {

    public static <T> Lazy<T> createLazySimple(Supplier<T> supplier) {
        return new Lazy<T>() {
            private Optional<T> optional = Optional.empty();

            @Override
            public T get() {
                if (!optional.isPresent()) {
                    optional = Optional.of(supplier.get());
                }
                return optional.get();
            }
        };
    }

    public static <T> Lazy<T> createLazyConcurent(Supplier<T> supplier) {
        return new Lazy<T>() {
            private final Object monitor = new Object();
            private Optional<T> optional = Optional.empty();

            @Override
            public T get() {
                if (!optional.isPresent()) {
                    synchronized (monitor) {
                        if (!optional.isPresent()) {
                            optional = Optional.of(supplier.get());
                        }
                    }
                }
                return optional.get();
            }
        };
    }

    public static <T> Lazy<T> createLazyLockFree(Supplier<T> supplier) {
        return new Lazy<T>() {
            final private AtomicReference<Optional<T>> optionalReference = new AtomicReference<>(Optional.empty());

            @Override
            public T get() {
                if (!optionalReference.get().isPresent()) {
                    Optional<T> newOptional = Optional.of(supplier.get());
                    optionalReference.compareAndSet(Optional.empty(), newOptional);
                }
                return optionalReference.get().get();
            }
        };
    }


}
