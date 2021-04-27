package com.bham.bc.view.model;

import junit.framework.TestCase;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class RecordsHandlerTest extends TestCase {
    private RecordsHandler recordsHandler;


    public void createSampleRecords(RecordsHandler recordsHandler) {

        //write to Json file
        recordsHandler.createRecord(new RecordsHandler.Records("Dou",999));
        recordsHandler.createRecord(new RecordsHandler.Records("YIFAN",888));
        recordsHandler.createRecord(new RecordsHandler.Records("Alex",777));
        recordsHandler.createRecord(new RecordsHandler.Records("Mantas",666));
        recordsHandler.createRecord(new RecordsHandler.Records("Najd",555));
        recordsHandler.createRecord(new RecordsHandler.Records("Justin",444));
        recordsHandler.createRecord(new RecordsHandler.Records("John",333));
        recordsHandler.createRecord(new RecordsHandler.Records("Shan",222));
        recordsHandler.createRecord(new RecordsHandler.Records("Juily",111));
        recordsHandler.createRecord(new RecordsHandler.Records("Berry",99));

        //first step is to sort before add new records
        recordsHandler.jsonArrayToFile=recordsHandler.jsonArraySort(recordsHandler.jsonArrayToFile);
        //second step is to add new records




    }
    /**
     * to test if user gets the right rank
     * @param i rank
     * @return the name who has the rank "i"
     */
    public String equalJsonObjectName(int i,RecordsHandler recordsHandler){

        return recordsHandler.jsonArrayToFile.getJSONObject(i-1).getString("name");
    }



    /**
     * test createRcord and sort function
     */
    @Test
    public void shouldSortScoresCorrectly() {

        recordsHandler=new RecordsHandler();
        createSampleRecords(recordsHandler);
        //the max min and middle value
        recordsHandler.createRecord(new RecordsHandler.Records("Dahn",111122));
        recordsHandler.createRecord(new RecordsHandler.Records("Daaa",022));
        recordsHandler.createRecord(new RecordsHandler.Records("Dann",664));
        recordsHandler.sortAndGetData();
        assertEquals("Dahn",equalJsonObjectName(1,recordsHandler));
        assertEquals("Daaa",equalJsonObjectName(10,recordsHandler));
        assertEquals("Dann",equalJsonObjectName(6,recordsHandler));
        recordsHandler.createRecord(new RecordsHandler.Records("Dnnn",665));
        recordsHandler.sortAndGetData();
        assertEquals("Dnnn",equalJsonObjectName(6,recordsHandler));

    }
}