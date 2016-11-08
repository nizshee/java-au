package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import org.junit.Test;

import static org.junit.Assert.*;


public class UpdateProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ServerKeeper keeper = new TestKeeper();
        UpdateProcedure procedure = new UpdateProcedure();

        byte[] from = procedure.request2Byes(TestKeeper.UPDATE_ITEM);
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        boolean res = procedure.bytes2Response(to);

        assertTrue(res);
    }
}
