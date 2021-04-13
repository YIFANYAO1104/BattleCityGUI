package com.bham.bc.view.model;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RecordsHandlerTest extends TestCase {
    private RecordsHandler recordsHandler;

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {
    }





    public void testCreateRecord() {

        recordsHandler=new RecordsHandler();
        recordsHandler.createSampleRecords();
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dahn","111122","7/3"));
        recordsHandler.createRecord(new RecordsHandler.Records("14","Daaa","022","7/3"));
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dann","666","7/3"));
        recordsHandler.sortAndGetData();
        assertEquals("Dahn",recordsHandler.equalJsonObjectName(1));
        assertEquals("Daaa",recordsHandler.equalJsonObjectName(10));
        assertEquals("Dann",recordsHandler.equalJsonObjectName(7));
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dnnn","786","7/3"));
        recordsHandler.sortAndGetData();
        assertEquals("Dnnn",recordsHandler.equalJsonObjectName(7));

    }
}