package com.bham.bc.view.model;

import junit.framework.TestCase;
import org.junit.Test;


public class RecordsHandlerTest extends TestCase {
    private RecordsHandler recordsHandler;




    /**
     * test createRcord and sort function
     */
    @Test
    public void testCreateRecord() {

        recordsHandler=new RecordsHandler();
        recordsHandler.createSampleRecords();
        //the max min and middle value
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dahn","111122",RecordsHandler.getDate()));
        recordsHandler.createRecord(new RecordsHandler.Records("14","Daaa","022",RecordsHandler.getDate()));
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dann","664",RecordsHandler.getDate()));
        recordsHandler.sortAndGetData();
        assertEquals("Dahn",recordsHandler.equalJsonObjectName(1));
        assertEquals("Daaa",recordsHandler.equalJsonObjectName(10));
        assertEquals("Dann",recordsHandler.equalJsonObjectName(6));
        recordsHandler.createRecord(new RecordsHandler.Records("13","Dnnn","665",RecordsHandler.getDate()));
        recordsHandler.sortAndGetData();
        assertEquals("Dnnn",recordsHandler.equalJsonObjectName(6));

    }
}