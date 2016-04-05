package task;


public abstract class Predicate<X> extends Function1<X, Boolean> {

    public Predicate<X> or(Predicate<? super X> other) {
        Predicate<X> self = this;
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return self.apply(x) || other.apply(x);
            }
        };
    }

    public Predicate<X> and(Predicate<? super X> other) {
        Predicate<X> self = this;
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return self.apply(x) && other.apply(x);
            }
        };
    }

    public Predicate<X> not() {
        Predicate<X> self = this;
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return !self.apply(x);
            }
        };
    }

    public final static Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };

    public final static Predicate<Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object o) {
            return false;
        }
    };

}
