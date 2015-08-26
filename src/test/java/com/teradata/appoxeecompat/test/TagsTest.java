package com.teradata.appoxeecompat.test;

import com.appoxee.Appoxee;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by yotam on 8/26/15.
 */
public class TagsTest extends TestCase {
        @Before
        public void setup() {

        }

    @Test
    public void testGetApplicationTags()
    {
        assertNotNull(Appoxee.getTagList());
    }

    @Test
    public void testGetDeviceTags()
    {
        assertNotNull(Appoxee.getDeviceTags()) ;
    }


    @Test
    public void testSetDeviceTags()
    {
        assertTrue(Appoxee.addTagsToDevice(null)) ;
    }

    @Test
    public void testRemoveDeviceTags()
    {
        assertTrue(Appoxee.removeTagsFromDevice(null)) ;
    }
}
