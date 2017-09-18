package org.jspringbot.keyword.expression;

import org.junit.Test;

import static org.junit.Assert.*;

public class ELUtilsTest {
    @Test
    public void base64Image() throws Exception {
        System.out.println("result: " + ELUtils.base64Image("classpath:threesixty-88vv_1.jpg", "jpeg"));
    }

}