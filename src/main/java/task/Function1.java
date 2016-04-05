package task;


public abstract class Function1<X, Y> {
    public abstract Y apply(X x);

    public <Z> Function1<X, Z> compose(Function1<? super Y, Z> other) {
        Function1<X, Y> self = this;
        return new Function1<X, Z>() {
            private final Function1<X, Y> f = self;
            private final Function1<? super Y, Z> g = other;

            @Override
            public Z apply(X x) {
                return g.apply(f.apply(x));
            }
        };
    }


}
