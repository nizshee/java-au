package sp;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {


    @Test
    @SuppressWarnings("all")
    public void testFindQuotes() {
        final String file1 = "src/test/resources/Californication.txt";
        final String file2 = "src/test/resources/Sweet Dreams.txt";

        assertEquals(findQuotes(Collections.emptyList(), ""), Collections.emptyList());
        assertEquals(new HashSet(findQuotes(Arrays.asList(file1, file2), "Dream")),
                ImmutableSet.of("Dream of silver screen quotations", "Dream of Californication"));
        assertEquals(new HashSet(findQuotes(Arrays.asList(file1), "dreams")),
                ImmutableSet.of("And if you want these kind of dreams"));
        assertEquals(new HashSet(findQuotes(Arrays.asList(file2), "dreams")),
                ImmutableSet.of("Sweet dreams are made of this"));
        assertEquals(new HashSet(findQuotes(Arrays.asList(file1, file2), "dreams")),
                ImmutableSet.of("And if you want these kind of dreams", "Sweet dreams are made of this"));
        assertEquals(new HashSet(findQuotes(Arrays.asList(file1, file2), "Dream")),
                ImmutableSet.of("Dream of silver screen quotations", "Dream of Californication"));
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(piDividedBy4(), Math.PI / 4, 1e-2);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> printers = new HashMap<>();
        assertEquals(findPrinter(printers), null);
        printers.put("a1", Arrays.asList(multiConcat("a", 10), multiConcat("b", 10), multiConcat("c", 10)));
        printers.put("a2", Arrays.asList(multiConcat("a", 8), multiConcat("b", 16), multiConcat("c", 10)));
        printers.put("a3", Arrays.asList(multiConcat("a", 10), multiConcat("b", 22)));
        assertEquals(findPrinter(printers), "a2");
        printers.put("a2", Arrays.asList(multiConcat("a", 10), multiConcat("b", 21)));
        assertEquals(findPrinter(printers), "a3");
    }

    @Test
    public void testCalculateGlobalOrder() {
        List<Map<String, Integer>> orders = Arrays.asList(
                ImmutableMap.of("a", 1, "d", 1),
                ImmutableMap.of("a", 1, "b", 2, "c", 3),
                ImmutableMap.of("b", 3, "c", 2, "d", 1),
                ImmutableMap.of("a", 3, "b", 1, "c", 2, "d", 4)
        );
        assertEquals(calculateGlobalOrder(orders), ImmutableMap.of("a", 5, "b", 6, "c", 7, "d", 6));

        assertEquals(calculateGlobalOrder(Collections.emptyList()), Collections.emptyMap());

    }

    private String multiConcat(String base, int times) {
        return new String(new char[times]).replace("\0", base);
    }
}