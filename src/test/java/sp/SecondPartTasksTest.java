package sp;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        final String file1 = "src/test/resources/Californication.txt";
        final String file2 = "src/test/resources/Sweet Dreams.txt";

        assertEquals(findQuotes(Collections.emptyList(), ""), Collections.emptyList());
        assertEquals(findQuotes(Arrays.asList(file1), "Dream").stream().distinct().collect(Collectors.toList()),
                Arrays.asList("Dream of silver screen quotations", "Dream of Californication"));
        assertEquals(findQuotes(Arrays.asList(file1), "dreams").stream().distinct().collect(Collectors.toList()),
                Arrays.asList("And if you want these kind of dreams"));
        assertEquals(findQuotes(Arrays.asList(file2), "dreams").stream().distinct().collect(Collectors.toList()),
                Arrays.asList("Sweet dreams are made of this"));
        assertEquals(findQuotes(Arrays.asList(file1, file2), "dreams").stream().distinct().collect(Collectors.toList()),
                Arrays.asList("And if you want these kind of dreams", "Sweet dreams are made of this"));
        assertEquals(findQuotes(Arrays.asList(file1, file2), "Dream").stream().distinct().collect(Collectors.toList()),
                Arrays.asList("Dream of silver screen quotations", "Dream of Californication"));
    }

    @Test
    public void testPiDividedBy4() {
        assertTrue(Math.abs(piDividedBy4() - Math.PI / 4) < 1e-2);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> printers = new HashMap<>();
        assertEquals(findPrinter(printers), "");
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