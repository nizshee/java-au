package task;

import static org.junit.Assert.*;


public class Function1Test {

    public static final Function1<Integer, Integer> succ = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return x + 1;
        }
    };

    public static final Function1<Integer, Integer> square = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return x * x;
        }
    };

    @org.junit.Test
    public void testApply() throws Exception {
        assertEquals(succ.apply(1).intValue(), 2);
        assertEquals(square.apply(2).intValue(), 4);
    }

    @org.junit.Test
    public void testCompose() throws Exception {
        Function1<Integer, Integer> f = succ.compose(square);
        Function1<Integer, Integer> g = square.compose(square);
        assertEquals(f.apply(1).intValue(), 4);
        assertEquals(f.apply(2).intValue(), 9);
        assertEquals(g.apply(2).intValue(), 16);
    }


}
