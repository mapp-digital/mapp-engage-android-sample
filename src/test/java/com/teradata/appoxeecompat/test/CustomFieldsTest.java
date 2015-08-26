package com.teradata.appoxeecompat.test;

import com.appoxee.Appoxee;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by yotam on 8/26/15.
 */
public class CustomFieldsTest extends TestCase {
    @Before
    public void setup() {

    }

    @Test
    public void testGetNumericField()
    {
        ArrayList<String> listOfFieldNames = new ArrayList<String>();
        listOfFieldNames.add("NumFld");
        assertNotNull(Appoxee.getCustomFieldsValues(listOfFieldNames));
    }

    @Test
    public void testGetStringField()
    {
        ArrayList<String> listOfFieldNames = new ArrayList<String>();
        listOfFieldNames.add("StrFld");
        assertNotNull(Appoxee.getCustomFieldsValues(listOfFieldNames));
    }

    @Test
    public void testGetDateField()
    {
        ArrayList<String> listOfFieldNames = new ArrayList<String>();
        listOfFieldNames.add("DateFld");
        assertNotNull(Appoxee.getCustomFieldsValues(listOfFieldNames));
    }

    @Test
    public void testSetNumericField()
    {
       assertTrue(Appoxee.setCustomField("NumFld", 12764));
    }

    @Test
    public void testSetStringField()
    {
        assertTrue(Appoxee.setCustomField("NumFld", "String"));

    }

    @Test
    public void testSetDateField()
    {
        assertTrue( Appoxee.setCustomField("DateFld","/03/02015 05:45"));

    }
}
