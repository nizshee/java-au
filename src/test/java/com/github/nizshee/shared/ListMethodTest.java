package com.github.nizshee.shared;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ListMethodTest {

    private final static ListMethod method = new ListMethod();

    @Test
    public void applyTest() throws Exception {

        @SuppressWarnings("ConstantConditions")
        List<RemoteFile> result = method.execute(getClass().getClassLoader().getResource("test/").getPath());
        assertEquals(Arrays.asList(
                new RemoteFile("a", false),
                new RemoteFile("b", false),
                new RemoteFile("c", true)
        ), result);

    }

    @Test
    public void valueTest() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        method.writeRequest(dos, "testString1");
        method.writeRequest(dos, "testString2");

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream dis = new DataInputStream(bis);

        assertEquals("testString1", method.readRequest(dis));
        assertEquals("testString2", method.readRequest(dis));
    }

    @Test
    public void resultTest() throws Exception {
        List<RemoteFile> list1 = new LinkedList<>();
        List<RemoteFile> list2 = Arrays.asList(
                new RemoteFile("1", true),
                new RemoteFile("2", false),
                new RemoteFile("3", true)
        );
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        method.writeResponse(dos, list1);
        method.writeResponse(dos, list2);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream dis = new DataInputStream(bis);

        assertEquals(list1, method.readResult(dis));
        assertEquals(list2, method.readResult(dis));
    }
}
