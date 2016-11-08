package com.github.nizshee.client.procedure;


import com.github.nizshee.client.ClientKeeper;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ClientKeeper keeper = new TestKeeper();
        GetProcedure procedure = new GetProcedure();

        byte[] from = procedure.request2Byes(TestKeeper.FILE_PART);
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        byte[] bytes = procedure.bytes2Response(to);

        assertArrayEquals(TestKeeper.BYTES, bytes);
    }
}