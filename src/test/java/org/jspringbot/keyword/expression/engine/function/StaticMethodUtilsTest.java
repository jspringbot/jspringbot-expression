package org.jspringbot.keyword.expression.engine.function;

import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class StaticMethodUtilsTest {

    public static int getMonth() {
        return 1;
    }

    @Test
    public void getMethod() throws Exception {
        assertThat(StaticMethodUtils.getMethod(StaticMethodUtilsTest.class, "int getMonth()"), notNullValue());
    }

}