package org.jspringbot.keyword.expression;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class ELUtilsTest {
    @Test
    public void base64Image() throws Exception {
        System.out.println("result: " + ELUtils.base64Image("classpath:images/captcha.jpg", "jpeg"));
        //System.out.println("result: " + ELUtils.base64Image("file:C:\\workspace\\qat-w88-web\\target\\robotframework-reports\\screen_capture_1566531624359_1.png", "jpeg"));
    }
}