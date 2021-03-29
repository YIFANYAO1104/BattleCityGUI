package com.bham.bc.view.menu;

import com.bham.bc.components.environment.MapType;
import com.bham.bc.components.mode.MODE;
import com.bham.bc.view.MenuSession;
import com.bham.bc.view.model.MenuButton;
import com.bham.bc.view.model.MenuSlider;
import com.bham.bc.view.model.NewGameEvent;
import com.bham.bc.view.model.SubMenu;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    private NewGameEvent newGameEvent;

    private Scene scene;
    private TableView tableView;
    private static ArrayList<Records> records=new ArrayList<>();

    /**
     * Constructs an AnchorPane layout as the Main Menu
     *
     * @param width  menu window's length
     * @param height menu window's height
     */
    public MainMenu(double width, double height) throws IOException {
        newGameEvent = new NewGameEvent(NewGameEvent.START_GAME);
        setWidth(width);
        setHeight(height);

        initBgDim();
        initTitle();

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
        Rectangle bg = new Rectangle(getWidth(), getHeight());
        bg.setFill(Color.GRAY);
        bg.setOpacity(0.2);

        getChildren().add(bg);
    }

    /**
     * Adds title to the menu
     */
    private void initTitle() {
        Title title = new Title("T A N K 1 G A M E");
        title.setTranslateY(100);
        title.setTranslateX(getWidth()/2 - title.getWidth()/2);

        getChildren().add(title);
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

        btnSolo.setOnMouseClicked(e -> { newGameEvent.setNumPlayers(1); subMenuMain.hide(); subMenuMode.show(); });
        btnCoop.setOnMouseClicked(e -> { newGameEvent.setNumPlayers(2); subMenuMain.hide(); subMenuMode.show(); });
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
        MenuButton btnBack = new MenuButton("Back");
        btnBack.setOnMouseClicked(e->{subMenuMode.hide();subMenuMain.show();});
        btnSurvival.setOnMouseClicked(e -> { newGameEvent.setMode(MODE.SURVIVAL); newGameEvent.setMapType(MapType.Map1); btnSurvival.fireEvent(newGameEvent); });
        btnChallenge.setOnMouseClicked(e -> { newGameEvent.setMode(MODE.CHALLENGE); newGameEvent.setMapType(MapType.EmptyMap); btnChallenge.fireEvent(newGameEvent);});

        subMenuMode = new SubMenu(this);
        subMenuMode.getChildren().addAll(btnSurvival, btnChallenge,btnBack);
    }

    /**
     * Creates a sub-menu to view high-scores of both modes. This menu is observed whenever
     * "HIGH-SCORES" is clicked and shows top 10 scores.
     */
    private void createSubMenuScores() throws IOException {
        subMenuScores=new SubMenu(this);
        subMenuScores.setMinHeight(430);
        subMenuScores.setMinWidth(550);
        BackgroundImage image=new BackgroundImage(new Image("file:src/main/resources/GUIResources/img_3.png",550,430,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,null);
        subMenuScores.setBackground(new Background(image));
        subMenuScores.setTranslateY(200);
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
        text2.setTranslateX(210);
        text2.setTranslateY(40);


        // Stylesheet for menu table
        getStylesheets().add(MenuSlider.class.getResource("../../../../../GUIResources/table.css").toExternalForm());

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
        parseJsonFile("src\\main\\java\\com\\bham\\bc\\view\\menu\\test.json");
        ObservableList data = FXCollections.observableArrayList(records);
        tableView.setItems(data);


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
    }

    public static void parseJsonFile(String fileName) throws IOException {
        FileInputStream fileInputStream=new FileInputStream(fileName);
        byte[] array=new byte[1024*1024];
        int num=fileInputStream.read(array);
        String s=new String(array);
        System.out.println(s);
        parse(s);
    }

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
        date.setCellValueFactory(
                new PropertyValueFactory<>("date"));
        tableView=new TableView();
        tableView.getColumns().addAll(rank,name,score,date);
//        ObservableList<Records> dataSet = FXCollections.observableArrayList(new Records("First","Fan","999","25/3"));
//        tableView.setItems(dataSet);
        tableView.setId("table");
        tableView.setMaxSize(395,300);
        tableView.setTranslateX(80);
        tableView.setTranslateY(30);
    }


    /**
     * Creates a sub-menu for settings. This menu is observed whenever "SETTINGS" is clicked
     * and allows the user to configure UI parameters, such as SFX or MUSIC volume
     */
    private void createSubMenuSettings() {
        MenuButton btnBack = new MenuButton("Back");
        MenuSlider bg=new MenuSlider("Volume:");

        DoubleProperty doubleProperty1=bg.getValueProperty();
        doubleProperty1.addListener((obsVal, oldVal, newVal) -> {
            audioManager.setMusicVolume(newVal.doubleValue()/100);
            bg.getNumOfVolume().setText(newVal.intValue()+"%");
            bg.setSliderStyle();


        });
        MenuSlider sfx=new MenuSlider("SFX Volume:");

        DoubleProperty doubleProperty2=sfx.getValueProperty();
        doubleProperty2.addListener((obsVal, oldVal, newVal) -> {
            audioManager.setEffectVolume(newVal.doubleValue()/100);
            sfx.getNumOfVolume().setText(newVal.intValue()+"%");
            sfx.setSliderStyle();

        });


        btnBack.setOnMouseClicked(e->{subMenuSettings.hide();subMenuMain.show();});
        subMenuSettings = new SubMenu(this);
        subMenuSettings.getChildren().addAll(bg,sfx,btnBack);
    }


    /**
     *  <h1>Title of the game</h1>
     *
     *  <p>This is a unique component provided by a parent {@link com.bham.bc.view.menu.MainMenu}.
     *  This is because the Title should always be part of the Main Menu node.
     */
    private static class Title extends StackPane {

        private static final double WIDTH = 475;
        private static final double HEIGHT = 60;

        /**
         * Constructs a title used in the Main Menu layout
         * @param name the title of the game
         * TODO: look for fancier/more game-like fonts
         */
        public Title(String name) {
            Rectangle bg = new Rectangle(WIDTH, HEIGHT);
            setWidth(WIDTH);
            setHeight(HEIGHT);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(3);
            bg.setFill(null);


            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.TOP_CENTER);
            getChildren().addAll(bg,text);
        }
    }

}
