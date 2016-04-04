package task;


import static org.junit.Assert.assertEquals;

public class Function2Test {

    public static final Function2<Object, Object, Integer> ADD = new Function2<Object, Object, Integer>() {
        @Override
        public Integer apply(Object x, Object y) {
            return (Integer) x + (Integer) y;
        }
    };

    public static final Function2<Object, Object, Integer> MUL = new Function2<Object, Object, Integer>() {
        @Override
        public Integer apply(Object x, Object y) {
            return (Integer) x * (Integer) y;
        }
    };

    public static final Function2<Object, Object, Integer> SND = new Function2<Object, Object, Integer>() {
        @Override
        public Integer apply(Object x, Object y) {
            return (Integer) y;
        }
    };

    public static final Function2<Object, Object, Integer> POW = new Function2<Object, Object, Integer>() {
        @Override
        public Integer apply(Object x, Object y) {
            return (int) Math.round(Math.pow((Integer)x, (Integer)y));
        }
    };

    @org.junit.Test
    public void testApply() throws Exception {
        assertEquals(ADD.apply(1, 3).intValue(), 4);
        assertEquals(MUL.apply(2, 5).intValue(), 10);
    }

    @org.junit.Test
    public void testCompose() throws Exception {
        Function2<Object, Object, Integer> f = ADD.compose(Function1Test.SQUARE);
        Function2<Object, Object, Integer> g = MUL.compose(Function1Test.SUCC);
        assertEquals(f.apply(1, 2).intValue(), 9);
        assertEquals(f.apply(2, 3).intValue(), 25);
        assertEquals(g.apply(2, 4).intValue(), 9);
    }

    @org.junit.Test
    public void testBind() throws Exception {
        Function1<Object, Integer> f = POW.bind1(2);
        Function1<Object, Integer> g = POW.bind2(2);
        assertEquals(f.apply(3).intValue(), 8);
        assertEquals(g.apply(3).intValue(), 9);
    }

    @org.junit.Test
    public void testCarry() throws Exception {
        Function1<Object, Function1<Object, Integer>> carried = POW.carry();
        assertEquals(carried.apply(2).apply(3).intValue(), 8);
        assertEquals(carried.apply(3).apply(2).intValue(), 9);
    }
}
