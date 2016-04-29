package vr;


import java.util.function.Function;

public interface LightFuture<T> {

    boolean isReady();

    T get() throws Exception;

    <Y> LightFuture<Y> thenApply(Function<? super T, Y> function);
}
