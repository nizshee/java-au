package com.github.nizshee.server.procedure;


import com.github.nizshee.server.ServerKeeper;
import org.junit.Test;

import static org.junit.Assert.*;

public class UploadProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ServerKeeper keeper = new TestKeeper();
        UploadProcedure procedure = new UploadProcedure();

        byte[] from = procedure.request2Byes(TestKeeper.UPLOAD_ITEM);
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        int identifier = procedure.bytes2Response(to);

        assertEquals(TestKeeper.IDENTIFIER, identifier);
    }

}
