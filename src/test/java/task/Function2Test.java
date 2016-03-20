package task;


import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;

public class Function2Test {

    public static final Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return x + y;
        }
    };

    public static final Function2<Integer, Integer, Integer> mul = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return x * y;
        }
    };

    public static final Function2<Integer, Integer, Integer> pow = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return (int) Math.round(Math.pow(x, y));
        }
    };

    @org.junit.Test
    public void testApply() throws Exception {
        assertEquals(add.apply(1, 3).intValue(), 4);
        assertEquals(mul.apply(2, 5).intValue(), 10);
    }

    @org.junit.Test
    public void testCompose() throws Exception {
        Function2<Integer, Integer, Integer> f = add.compose(Function1Test.square);
        Function2<Integer, Integer, Integer> g = mul.compose(Function1Test.succ);
        assertEquals(f.apply(1, 2).intValue(), 9);
        assertEquals(f.apply(2, 3).intValue(), 25);
        assertEquals(g.apply(2, 4).intValue(), 9);
    }

    @org.junit.Test
    public void testBind() throws Exception {
        Function1<Integer, Integer> f = pow.bind1(2);
        Function1<Integer, Integer> g = pow.bind2(2);
        assertEquals(f.apply(3).intValue(), 8);
        assertEquals(g.apply(3).intValue(), 9);
    }

    @org.junit.Test
    public void testCarry() throws Exception {
        Function1<Integer, Function1<Integer, Integer>> carried = pow.carry();
        assertEquals(carried.apply(2).apply(3).intValue(), 8);
        assertEquals(carried.apply(3).apply(2).intValue(), 9);
    }
}
