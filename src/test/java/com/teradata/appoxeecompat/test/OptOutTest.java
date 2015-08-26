package com.teradata.appoxeecompat.test;

import com.appoxee.Appoxee;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by yotam on 8/26/15.
 */
public class OptOutTest extends TestCase {

    @Before
    public void setup() {

    }

    @Test
    public void testInboxOptIn()
    {
        assertTrue(Appoxee.OptOut("inbox", true));
    }

    @Test
    public void testPushOptIn()
    {
        assertTrue(Appoxee.OptOut("pushToken",true));

    }

    @Test
    public void testPushOptOut()
    {
        assertTrue(Appoxee.OptOut("pushToken",false));

    }

    @Test
    public void testInboxOptOut()
    {
        assertTrue(Appoxee.OptOut("inbox",false));

    }
}
