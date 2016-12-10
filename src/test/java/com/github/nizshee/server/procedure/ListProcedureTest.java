package com.github.nizshee.server.procedure;


import com.github.nizshee.server.util.FileDescriptor;
import com.github.nizshee.server.ServerKeeper;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.List;

public class ListProcedureTest {

    @Test
    public void executeDumpTest() throws Exception {
        ServerKeeper keeper = new TestKeeper();
        ListProcedure procedure = new ListProcedure();

        byte[] from = procedure.request2Byes(new Object());
        byte[] to = procedure.executeDump(new byte[0], keeper, from);
        List<FileDescriptor> list = procedure.bytes2Response(to);

        assertEquals(TestKeeper.LIST, list);
    }
}
