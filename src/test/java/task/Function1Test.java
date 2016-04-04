package task;

import java.util.Objects;

import static org.junit.Assert.*;


public class Function1Test {

    public static final Function1<Object, Integer> SUCC = new Function1<Object, Integer>() {
        @Override
        public Integer apply(Object x) {
            return (Integer) x + 1;
        }
    };

    public static final Function1<Integer, Integer> SQUARE = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return x * x;
        }
    };

    @org.junit.Test
    public void testApply() throws Exception {
        assertEquals(SUCC.apply(1).intValue(), 2);
        assertEquals(SQUARE.apply(2).intValue(), 4);
    }

    @org.junit.Test
    public void testCompose() throws Exception {
        Function1<Object, Integer> f = SUCC.compose(SQUARE);
        Function1<Integer, Integer> g = SQUARE.compose(SQUARE);
        assertEquals(f.apply(1).intValue(), 4);
        assertEquals(f.apply(2).intValue(), 9);
        assertEquals(g.apply(2).intValue(), 16);
    }


}
