package com.l2o.minactor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class EventWrapperTest {
    @Mock
    private EventHandler<String> handler1;
    @Mock
    private EventHandler<String> handler2;
    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testHashCode() {
	EventWrapper<String> testee1 = new EventWrapper<String>("event1", handler1);
	EventWrapper<String> testee2 = new EventWrapper<String>("event1", handler1);
	assertEquals(testee1.hashCode(),  testee2.hashCode());
    }

    @Test
    public void testEqualsObject() {
	EventWrapper<String> testee1 = new EventWrapper<String>("event1", handler1);
	EventWrapper<String> testee2 = new EventWrapper<String>("event1", handler1);
	EventWrapper<String> testee3 = new EventWrapper<String>("event2", handler1);
	EventWrapper<String> testee4 = new EventWrapper<String>("event1", handler2);
	assertEquals(testee1,  testee2);
	assertNotEquals(testee1, new Object());
	assertNotEquals(testee1, testee3);
	assertNotEquals(testee1, testee4);
    }

}
