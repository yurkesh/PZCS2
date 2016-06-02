package edu.kpi.nesteruk.pzcs.planning.processors;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Yurii on 2016-06-02.
 */
public class ProcessorExecutionTest {

    private ProcessorExecution execution;

    @BeforeTest
    public void init() {
        execution = new ProcessorExecution("nope", 1);
        execution.assignTask(0, "a", 2);
        execution.assignTask(4, "b", 2);
        execution.assignTask(8, "c", 2);
        /*
        0 1 2 3 4 5 6 7 8 9 10 11 12 13 14
        a a 0 0 b b 0 0 c c 0  0  0  0  0
         */
    }

    @Test
    public void testGetIdleTime() throws Exception {
        assertEquals(execution.getIdleTime(0), 0);
        assertEquals(execution.getIdleTime(1), 0);
        assertEquals(execution.getIdleTime(2), 0);
        assertEquals(execution.getIdleTime(3), 1);
        assertEquals(execution.getIdleTime(4), 2);
        assertEquals(execution.getIdleTime(5), 0);
        assertEquals(execution.getIdleTime(6), 0);
        assertEquals(execution.getIdleTime(7), 1);
        assertEquals(execution.getIdleTime(8), 2);
        assertEquals(execution.getIdleTime(9), 0);
        assertEquals(execution.getIdleTime(10), 0);
        assertEquals(execution.getIdleTime(11), 1);
        assertEquals(execution.getIdleTime(12), 2);
        assertEquals(execution.getIdleTime(13), 3);
    }
}