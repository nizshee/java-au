package task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Collections {

    public static <X, Y> List<Y> map(Function1<? super X, Y> f, Iterable<X> a) {
        List<Y> list = new LinkedList<>();
        for (X x : a) {
            list.add(f.apply(x));
        }
        return list;
    }

    public static <X> List<X> filter(Predicate<? super X> p, Iterable<X> a) {
        List<X> list = new LinkedList<>();
        for (X x : a) {
            if (p.apply(x)) {
                list.add(x);
            }
        }
        return list;
    }

    public static <X> List<X> takeWhile(Predicate<? super X> p, Iterable<X> a) {
        List<X> list = new LinkedList<>();
        for (X x : a) {
            if (!p.apply(x)) break;
            list.add(x);
        }
        return list;
    }

    public static <X> List<X> takeUnless(Predicate<? super X> p, Iterable<X> a) {
        return takeWhile(p.not(), a);
    }

    public static <X, Y> Y foldr(Function2<? super Y, ? super X, ? extends Y> f, Y base, Iterable<X> a) {
        return myFoldr(f, base, a.iterator());
    }

    public static <X, Y> Y foldl(Function2<? super Y, ? super X, ? extends Y> f, Y base, Iterable<X> a) {
        for (X x : a) {
            base = f.apply(base, x);
        }
        return base;
    }

    private static <X, Y> Y myFoldr(Function2<? super Y, ? super X, ? extends Y> f, Y base, Iterator<X> a) {
        if (!a.hasNext()) {
            return base;
        } else {
            X x = a.next();
            return f.apply(myFoldr(f, base, a), x);
        }
    }
}
