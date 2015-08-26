package com.teradata.appoxeecompat.test;

import com.appoxee.Appoxee;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by yotam on 8/26/15.
 */
public class AliasTest extends TestCase {

    @Before
    public void setup() {

    }

    @Test
    public void testGetDeviceAlias()
    {
        assertNotNull(Appoxee.getDeviceAlias());
    }

    @Test
    public void testSetDeviceAlias()
    {
        assertTrue(Appoxee.setDeviceAlias("testAlias"));
    }

    @Test
    public void testRemoveDeviceAlias()
    {
        assertTrue(Appoxee.removeDeviceAlias());

    }



}
