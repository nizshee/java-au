package task;


public abstract class Function2<X, Y, Z> {
    public abstract Z apply(X x, Y y);

    public <Z1> Function2<X, Y, Z1> compose(Function1<? super Z, Z1> other) {
        Function2<X, Y, Z> self = this;
        return new Function2<X, Y, Z1>() {
            private Function1<? super Z, Z1> f = other;
            private Function2<X, Y, Z> g = self;

            @Override
            public Z1 apply(X x, Y y) {
                return f.apply(g.apply(x, y));
            }
        };
    }

    public Function1<Y, Z> bind1(X x) {
        Function2<X, Y, Z> self = this;
        return new Function1<Y, Z>() {
            @Override
            public Z apply(Y y) {
                return self.apply(x, y);
            }
        };
    }

    public Function1<X, Z> bind2(Y y) {
        Function2<X, Y, Z> self = this;
        return new Function1<X, Z>() {
            @Override
            public Z apply(X x) {
                return self.apply(x, y);
            }
        };
    }

    public Function1<X, Function1<Y, Z>> carry() {
        Function2<X, Y, Z> self = this;
        return new Function1<X, Function1<Y, Z>>() {
            @Override
            public Function1<Y, Z> apply(X x) {
                return new Function1<Y, Z>() {
                    @Override
                    public Z apply(Y y) {
                        return self.apply(x, y);
                    }
                };
            }
        };
    }
}
