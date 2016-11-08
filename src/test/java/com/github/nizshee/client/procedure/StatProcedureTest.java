package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class StatProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ClientKeeper keeper = new TestKeeper();
        StatProcedure procedure = new StatProcedure();

        byte[] from = procedure.request2Byes(TestKeeper.IDENTIFIER);
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        Set<Integer> stat = procedure.bytes2Response(to);

        assertEquals(TestKeeper.STAT, stat);
    }
}