package com.github.nizshee.shared;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.*;


public class GetMethodTest {

    private final static GetMethod method = new GetMethod();

    @SuppressWarnings("ConstantConditions")
    @Test
    public void applyTest() throws Exception {
        byte[] bytes1 = method.apply(getClass().getClassLoader().getResource("test/a").getPath());
        byte[] bytes2 = method.apply(getClass().getClassLoader().getResource("test/b").getPath());

        assertArrayEquals("123".getBytes(), bytes1);
        assertArrayEquals("12345".getBytes(), bytes2);
    }

    @Test
    public void valueTest() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        method.writeValue(dos, "anotherTestString1");
        method.writeValue(dos, "anotherTestString2");

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream dis = new DataInputStream(bis);

        assertEquals("anotherTestString1", method.readValue(dis));
        assertEquals("anotherTestString2", method.readValue(dis));
    }

    @Test
    public void resultTest() throws Exception {
        byte[] bytes1 = {};
        byte[] bytes2 = {1, 2, 3, 4};

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        method.writeResult(dos, bytes1);
        method.writeResult(dos, bytes2);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DataInputStream dis = new DataInputStream(bis);

        assertArrayEquals(bytes1, method.readResult(dis));
        assertArrayEquals(bytes2, method.readResult(dis));
    }
}
