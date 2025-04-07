package com.vg.webflux.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SimpleTest {

    @Test
    public void test1() {
            assertEquals(1,1);
    }

    @Test
    public void test2() {
        TestI test = mock(TestI.class);

        when(test.getInt()).thenReturn(10);

        assertEquals(10, test.getInt());
    }

    @Test
    public void test3() {
        TestI test = new TestI() {
            @Override
            public int getInt() {
                return 0;
            }
        };


        TestI testSpy = spy(test);

        when(testSpy.getInt()).thenReturn(11);

        int rez = testSpy.getInt();

        verify(testSpy).getInt();
        assertEquals(0, 0);
    }

    interface TestI {
        public int getInt();
    }
}
