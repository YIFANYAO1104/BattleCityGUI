package com.bham.bc.view.menu;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.NewGameEvent;
import com.bham.bc.view.model.SubMenu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import static com.bham.bc.audio.AudioManager.audioManager;

/**
 * <h1>Main Menu</h1>
 *
 * <p>Represents the primary menu of the game. All the buttons, sub-menus and their functionalities
 * are defined here. The class is created only once by {@link MenuSession} and is observed whenever
 * a game session is not active.</p>
 */
public class MainMenu extends AnchorPane {

    private SubMenu subMenuMain;
    private SubMenu subMenuMode;
    private SubMenu subMenuScores;
    private SubMenu subMenuSettings;

    private final NewGameEvent NEW_GAME_EVENT;

    private TableView tableView;
    private static ArrayList<Records> records=new ArrayList<>();
    private static JSONArray jsonArrayToFile=new JSONArray();
    private static JSONArray jsonArray;

    /**
     * Constructs an AnchorPane layout as the Main Menu
     */
    public MainMenu() {
        NEW_GAME_EVENT = new NewGameEvent(NewGameEvent.START_GAME);
        setMinWidth(MenuSession.WIDTH);
        setMinHeight(MenuSession.HEIGHT);

        initBgDim();

        createSubMenuMain();
        createSubMenuMode();
        createSubMenuScores();
        createSubMenuSettings();

        subMenuMain.show();
    }

    /**
     * Adds background dim to the menu
     */
    private void initBgDim() {
        Rectangle dim = new Rectangle(getWidth(), getHeight());
        dim.setFill(Color.NAVY);
        dim.setOpacity(0.3);
        getChildren().add(dim);
    }

    /**
     * Creates the primary sub-menu for the main menu. This defines the behavior of all the
     * necessary buttons to control the GUI actions and create corresponding sub-menus.
     */
    private void createSubMenuMain() {
        MenuButton btnSolo = new MenuButton("SOLO");
        MenuButton btnCoop = new MenuButton("CO-OP");
        MenuButton btnScores = new MenuButton("HIGH-SCORES");
        MenuButton btnSettings = new MenuButton("SETTINGS");
        MenuButton btnQuit = new MenuButton("QUIT");

        btnSolo.setOnMouseClicked(e -> { NEW_GAME_EVENT.setNumPlayers(1); subMenuMain.hide(); subMenuMode.show(); });
        btnCoop.setOnMouseClicked(e -> { NEW_GAME_EVENT.setNumPlayers(2); subMenuMain.hide(); subMenuMode.show(); });
        btnScores.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuScores.show(); });
        btnSettings.setOnMouseClicked(e -> { subMenuMain.hide(); subMenuSettings.show(); });
        btnQuit.setOnMouseClicked(e -> System.exit(0));

        subMenuMain = new SubMenu(this);
        subMenuMain.getChildren().addAll(btnSolo, btnCoop, btnScores, btnSettings, btnQuit);
    }

    /**
     * Creates the sub-menu for mode selection. This menu is observed whenever "SOLO" or
     * "CO-OP" is clicked and asks {@link com.bham.bc.view.MenuSession} to initiate a
     * single {@link com.bham.bc.view.GameSession} based on the selected parameters
     */
    private void createSubMenuMode() {
        MenuButton btnSurvival = new MenuButton("SURVIVAL");
        MenuButton btnChallenge = new MenuButton("CHALLENGE");
        MenuButton btnBack = new MenuButton("BACK");

        btnSurvival.setOnMouseClicked(e -> { NEW_GAME_EVENT.setMode(MODE.SURVIVAL); NEW_GAME_EVENT.setMapType(MapType.Map1); btnSurvival.fireEvent(NEW_GAME_EVENT); });
        btnChallenge.setOnMouseClicked(e -> { NEW_GAME_EVENT.setMode(MODE.CHALLENGE); NEW_GAME_EVENT.setMapType(MapType.EmptyMap); btnChallenge.fireEvent(NEW_GAME_EVENT);});
        btnBack.setOnMouseClicked(e -> { subMenuMode.hide(); subMenuMain.show(); });

        subMenuMode = new SubMenu(this);
        subMenuMode.getChildren().addAll(btnSurvival, btnChallenge, btnBack);
    }

    /**
     * Creates a sub-menu to view high-scores of both modes. This menu is observed whenever
     * "HIGH-SCORES" is clicked and shows top 10 scores.
     */
    private void createSubMenuScores() {
        subMenuScores=new SubMenu(this);
        subMenuScores.setMinWidth(680);
        subMenuScores.setMinHeight(500);
        subMenuScores.setTranslateX(getMinWidth()*.5 - subMenuScores.getMinWidth()*.5);
        subMenuScores.setPadding(new Insets(20, 2, 2, 2));

        BackgroundImage image=new BackgroundImage(new Image("file:src/main/resources/GUIResources/img_3.png",subMenuScores.getMinWidth(),subMenuScores.getMinHeight(),false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        subMenuScores.setBackground(new Background(image));
        Text text2 = new Text();
        text2.setText("SCORES");
        text2.setStyle(" -fx-font-size: 30px;\n" +
                "-fx-fill: gold;\n"+
                "    -fx-font-family: \"Arial Narrow\";\n" +
                "    -fx-font-weight: bold;\n" +
                "\n" );
        Glow glow1=new Glow();
        text2.setEffect(glow1);
        glow1.setLevel(1);


        // Stylesheet for menu table
        getStylesheets().add(MenuSlider.class.getResource("../../../../../GUIResources/Style.css").toExternalForm());

        createScoreTable();

        subMenuScores.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                subMenuScores.hide();
                subMenuMain.show();

            }
        });
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                subMenuScores.hide();
                subMenuMain.show();
            }
        });
        subMenuScores.getChildren().addAll(text2,tableView);

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
        createRecord(new Records("11th","Kitty","322","7/3"));
        createRecord(new Records("12th","Jog","222","7/3"));

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
        ObservableList data = FXCollections.observableArrayList(records);
        tableView.setItems(data);
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



    public static JSONArray jsonArraySort(JSONArray jsonArr) {

        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "score";
            String string1;
            String string2;
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    string1= a.getString(KEY_NAME);
                    string2= b.getString(KEY_NAME);
                } catch (JSONException e) {
                    // 处理异常
                }
                //这里是按照时间逆序排列,不加负号为正序排列
                return -string1.compareTo(string2);
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
     * @throws Exception
     */

    public static void writeJsonToFile(String filename) throws Exception {
        Tool tool=new Tool();

        String jsonString=jsonArrayToFile.toString();//to string
        System.out.println(jsonString);
        String JsonString=tool.stringToJSON(jsonString);//format string

        Files.write(Paths.get(filename), JsonString.getBytes());
    }


    /**
     * create a record and put into a Json array
     * @param record
     */
    public static void createRecord(Records record){
        record.putIntoArray();
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

    /**
     * create the table of scoreSubMenu
     */
    public void createScoreTable(){
        TableColumn<Records,String> rank=new TableColumn<>("Rank");
        rank.setMinWidth(100);
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        TableColumn<Records,String> name=new TableColumn<>("Name");
        name.setMinWidth(100);
        name.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        TableColumn<Records,String> score=new TableColumn<>("Score");
        score.setMinWidth(100);
        score.setCellValueFactory(
                new PropertyValueFactory<>("score"));
        TableColumn<Records,String> date=new TableColumn<>("Date");
        date.setMinWidth(100);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView=new TableView();
        tableView.getColumns().addAll(rank,name,score,date);

        tableView.setId("table");
        tableView.setMaxSize(subMenuScores.getMinWidth(), subMenuScores.getMinHeight());
    }


    /**
     * Creates a sub-menu for settings. This menu is observed whenever "SETTINGS" is clicked
     * and allows the user to configure UI parameters, such as SFX or MUSIC volume
     */
    private void createSubMenuSettings() {
        MenuSlider musicVolume = new MenuSlider("MUSIC VOLUME", 100);
        MenuSlider sfxVolume = new MenuSlider("EFFECTS VOLUME", 100);
        MenuButton btnBack = new MenuButton("BACK");

        musicVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setMusicVolume(newVal.doubleValue()/100));
        sfxVolume.getValueProperty().addListener((obsVal, oldVal, newVal) -> audioManager.setEffectVolume(newVal.doubleValue()/100));
        btnBack.setOnMouseClicked(e -> { subMenuSettings.hide(); subMenuMain.show(); });

        subMenuSettings = new SubMenu(this);
        subMenuSettings.getChildren().addAll(musicVolume, sfxVolume, btnBack);
    }
}
