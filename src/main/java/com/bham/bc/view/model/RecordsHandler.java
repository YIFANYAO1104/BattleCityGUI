package com.bham.bc.view.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RecordsHandler {
    private static ArrayList<Records> records;
    private static JSONArray jsonArrayToFile;
    private static JSONArray jsonArray;


    public RecordsHandler() {
        jsonArrayToFile=new JSONArray();
        records=new ArrayList<>();
    }

    /**
     * to test if user gets the right rank
     * @param i rank
     * @return the name who has the rank "i"
     */
    public String equalJsonObjectName(int i){

        return jsonArrayToFile.getJSONObject(i-1).getString("name");
    }

    /**
     * temp
     * @return
     */
    public void createSampleRecords() {

        //write to Json file
        createRecord(new Records("1st","Dou","222","7/3"));
        createRecord(new Records("2nd","YIFAN","782","7/3"));
        createRecord(new Records("3rd","Alex","762","7/3"));
        createRecord(new Records("4th","Mantas","622","7/3"));
        createRecord(new Records("5th","Najd","792","7/3"));
        createRecord(new Records("6th","Justin","892","7/3"));
        createRecord(new Records("7th","John","792","7/3"));
        createRecord(new Records("8th","Shan","792","7/3"));
        createRecord(new Records("9th","Juily","992","7/3"));
        createRecord(new Records("10th","Berry","792","7/3"));

        //first step is to sort before add new records
        jsonArrayToFile=jsonArraySort(jsonArrayToFile);
        //second step is to add new records
        createRecord(new Records("11th","Kitty","122","7/3"));
        createRecord(new Records("12th","Jog","212","7/3"));



    }

    public ObservableList<Records> sortAndGetData(){
        //third step is to sort after add the new records
        sort();
        try {
            writeJsonToFile("src\\main\\java\\com\\bham\\bc\\view\\menu\\test.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //read from Json file
        try {
            parseJsonFile("src\\main\\java\\com\\bham\\bc\\view\\menu\\test.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<Records> data = FXCollections.observableArrayList(records);

        return data;
    }

    /**
     * to format json
     */
    public static class Tool {
        private boolean isTab = true;
        public String stringToJSON(String strJson) {
            int tabNum = 0;
            StringBuffer jsonFormat = new StringBuffer();
            int length = strJson.length();
            for (int i = 0; i < length; i++) {
                char c = strJson.charAt(i);
                if (c == '{') {
                    tabNum++;
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                } else if (c == '}') {
                    tabNum--;
                    jsonFormat.append("\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                    jsonFormat.append(c);
                } else if (c == ',') {
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                } else {
                    jsonFormat.append(c);
                }
            }
            return jsonFormat.toString();
        }
        public String getSpaceOrTab(int tabNum) {
            StringBuffer sbTab = new StringBuffer();
            for (int i = 0; i < tabNum; i++) {
                if (isTab) {
                    sbTab.append('\t');
                } else {
                    sbTab.append("    ");
                }
            }
            return sbTab.toString();
        }
    }

    /**
     * sort the socres automatically.
     */
    public static void sort(){
        // keep the first 10 record. And when new record come, delete some old records
        int len=jsonArrayToFile.length();
        jsonArray=new JSONArray();
        if (jsonArrayToFile.length()>10){
            for (int i=0;i<len;i++){
                if (10+i<len){
                    jsonArray.put(i,jsonArrayToFile.get(10+i));
                }
                if (10+i>=len){
                    jsonArray.put(i,jsonArrayToFile.get(i-(len-10)));
                }
            }

        }else {
            jsonArray=jsonArrayToFile;
        }
        System.out.println("jsonArray"+jsonArray.toString());
        for (int i=0;i<len;i++) {
            if (i<10){
                jsonArrayToFile.put(i,jsonArray.get(i));
            }

            if (i>=10){
                jsonArrayToFile.remove(i);
                i--;
                len--;
            }
        }



        //sort the first 10 records of array
        jsonArrayToFile=jsonArraySort(jsonArrayToFile);

        //set the rank according to the order of array
        for (int i=0;i<jsonArrayToFile.length();i++){
            JSONObject jsonObject=(JSONObject) jsonArrayToFile.get(i);
            jsonObject.put("rank",(i+1)+"");
        }
    }


    /**
     * sort the Json Array according to score
     * @param jsonArr json array to be sorted
     * @return return the sorted json array
     */
    public static JSONArray jsonArraySort(JSONArray jsonArr) {

        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "score";
            int score1;
            int score2;
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    score1= Integer.valueOf(a.getString(KEY_NAME));
                    score2= Integer.valueOf(b.getString(KEY_NAME));
                } catch (JSONException e) {
                    // 处理异常
                }
                //这里是按照时间逆序排列,不加负号为正序排列
                return -score1+score2;
            }
        });
        for (int i = 0; i < jsonArr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    /**
     * write Json array to the file
     * @param filename
     */
    public static void writeJsonToFile(String filename) throws Exception {
        Tool tool=new Tool();

        String jsonString=jsonArrayToFile.toString();//to string
        //System.out.println(jsonString);
        String JsonString=tool.stringToJSON(jsonString);//format string

        Files.write(Paths.get(filename), JsonString.getBytes());
    }


    /**
     * create the class for data in the table
     */
    public static class Records{
        private final SimpleStringProperty rank;
        private final SimpleStringProperty name;
        private final SimpleStringProperty score;
        private final SimpleStringProperty date;

        public Records(String rank, String name,String  score, String date) {
            this.rank = new SimpleStringProperty(rank);
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleStringProperty(score);
            this.date =new SimpleStringProperty(date);
        }

        public String getRank() {
            return rank.get();
        }

        public SimpleStringProperty rankProperty() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank.set(rank);
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getScore() {
            return score.get();
        }

        public SimpleStringProperty scoreProperty() {
            return score;
        }

        public void setScore(String score) {
            this.score.set(score);
        }

        public String getDate() {
            return date.get();
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        /**
         * to convert java object to JSon Object
         * @return
         */
        public JSONObject toJSON() {

            JSONObject jo = new JSONObject();
            jo.put("rank", rank.getValue());
            jo.put("name", name.getValue());
            jo.put("score",score.getValue());
            jo.put("date",date.getValue());

            return jo;
        }

        /**
         * put json object in to Json array (for Json file)
         */
        public void putIntoArray(){
            jsonArrayToFile.put(toJSON());
        }
    }

    /**
     * create a record and put into a Json array
     * @param record
     */
    public void createRecord(Records record){
        record.putIntoArray();
    }


    /**
     * read file to get a string and parse string
     * @param fileName
     * @throws IOException
     */
    public static void parseJsonFile(String fileName) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(fileName);
        byte[] array=new byte[1024*1024];
        int num=fileInputStream.read(array);
        String s=new String(array);

        parse(s);
    }

    /**
     * parse the string and make a array of records
     * @param responseBody
     * @return
     */
    public static ArrayList<Records> parse(String responseBody){
        JSONArray albums = new JSONArray(responseBody);
        System.out.println(albums.length());
        for (int i = 0; i < albums.length(); i++){
            JSONObject album = albums.getJSONObject(i);
            String rank = album.getString("rank");
            String name = album.getString("name");
            String score = album.getString("score");
            String date = album.getString("date");
            records.add(i,new Records(rank,name,score,date));
            System.out.println(rank + " | " + name + " | " + score+" | "+date);

        }
        return records;
    }
}
