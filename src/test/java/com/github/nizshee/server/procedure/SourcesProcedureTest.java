package com.github.nizshee.server.procedure;


import com.github.nizshee.server.util.ClientItem;
import com.github.nizshee.server.ServerKeeper;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.List;

public class SourcesProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ServerKeeper keeper = new TestKeeper();
        SourcesProcedure procedure = new SourcesProcedure();

        byte[] from = procedure.request2Byes(TestKeeper.IDENTIFIER);
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        List<ClientItem> list = procedure.bytes2Response(to);

        assertEquals(TestKeeper.SOURCES, list);
    }
}
