package com.yoloho.spring.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SpElUtilTest {
    @Test
    public void test() {
        assertEquals("Hello World!", SpElUtil.compute("'Hello World!'"));
        assertEquals(-3, SpElUtil.compute("1+2-3*4/2"));
        assertEquals("dataSource0", SpElUtil.compute("'dataSource'+(0/256)"));
        assertEquals("dataSource0", SpElUtil.compute("'dataSource'+(255/256)"));
        assertEquals("dataSource1", SpElUtil.compute("'dataSource'+(256/256)"));
        assertEquals("dataSource2", SpElUtil.compute("'dataSource'+(512/256)"));
        assertEquals("dataSource2000000", SpElUtil.compute("'dataSource'+(5120000000l/2560)"));
    }
}
