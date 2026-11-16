package org.programs;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

@Slf4j
public class AppTest {

    @Test
    public void testApp1() {
        Assert.assertTrue(false);
        log.info("-----Test1 executed successfully after assertion-----");
    }


    @Test
    public void testApp2() {
        Assert.assertTrue(true);
        log.info("-----Test2 executed successfully after assertion-----");
    }


    @Test (priority = -1)
    public void testApp3() {
        Assert.assertTrue(false);
        log.info("-----Test3 executed successfully after assertion-----");
    }


    @Test
    public void testApp4() {
        Assert.assertTrue(true);
        log.info("-----Test4 executed successfully after assertion-----");
    }


    @Test (priority = -2)
    public void testApp5() {
        Assert.assertTrue(true);
        log.info("-----Test5 executed successfully after assertion-----");
    }


    @Test
    public void testApp6() {
        Assert.assertTrue(true);
        log.info("-----Test6 executed successfully after assertion-----");
    }
}