package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.css.*;

import java.io.FileReader;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class Main extends Application {
    public Integer agents;
    public Integer turns;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //set the title for our stage
        primaryStage.setTitle("AgentSim");

        createMainMenu(primaryStage);

        //have to call .show to display content of scene in stage.
        primaryStage.show();
    }


    public void createMainMenu (Stage primaryStage){
        //creating a grid for the menu
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //menu title
        Text menuTitle = new Text("Welcome To AgentSim");
        menuTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        grid.add(menuTitle, 0, 0, 2, 1);

        gameSpecLoad(grid);

        //Name field for company name --not really necessary...
//        Label compName = new Label("Company Name:");
//        grid.add(compName, 0, 1);
//
//        TextField compTextField = new TextField();
//        grid.add(compTextField, 1, 1);

        //Sliders UI - quite a lot of work for sliders to work
        Label turnsLabel = new Label("Turns:");
        grid.add(turnsLabel, 0, 2);
        Slider turnSlider = new Slider(10,100,50);
        grid.add(turnSlider, 1, 2);
        turnSlider.setShowTickMarks(true);
        turnSlider.setShowTickLabels(true);
        turnSlider.setSnapToTicks(true);
        turnSlider.setMajorTickUnit(10f);
        turnSlider.setMinorTickCount(0);
        turnSlider.setBlockIncrement(5f);
        Label valSlider = new Label(String.valueOf(turnSlider.getValue()));

        turnSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                valSlider.textProperty().setValue(
                        String.valueOf(newValue.intValue()));

                //get value of slider and pass to instance variable for game creation
                turns = (int)(turnSlider.getValue());
            }
        });
        //some bug with the slider not being able to parse default value (50) so had to get around it
        if (turns == null) {
            turns = 50;
        }


        grid.add(valSlider,2,2);

        //Agent Slider
        Label agLabel = new Label("Agents:");
        grid.add(agLabel, 0, 3);
        Slider agSlider = new Slider(10,100,50);
        grid.add(agSlider, 1, 3);
        agSlider.setShowTickMarks(true);
        agSlider.setShowTickLabels(true);
        agSlider.setSnapToTicks(true);
        agSlider.setMajorTickUnit(10f);
        agSlider.setMinorTickCount(0);
        agSlider.setBlockIncrement(5f);
        Label valAgSlider = new Label(String.valueOf(agSlider.getValue()));

        agSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                valAgSlider.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
                //get value of slider and pass to instance variable for game creation
                agents = (int)(agSlider.getValue());
            }
        });
        grid.add(valAgSlider,2,3);
        //some bug with the slider not being able to parse default value (50) so had to get around it
        if (agents == null) {
            agents = 50;
        }


        //creating start button and its handling events.
        Button startBtn = new Button("Start");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(startBtn);
        grid.add(hbBtn, 1, 4);

        startBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Game game = new Game(agents,turns);
                primaryStage.setScene(game.gameScene);
            }
        });

        //creating root node and adding the button
        StackPane root = new StackPane();
        root.getChildren().add(grid);

        //creating a scene to pass to stage
        Scene menu = new Scene(root, 1240, 720);
        menu.setFill(Color.LIGHTBLUE);

        //pass scene to the stage
        primaryStage.setScene(menu);

    }

    public void gameSpecLoad (GridPane grid){

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/sample/gameSpec.json"));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports java.util.List interface.
            JSONArray companyList = (JSONArray) jsonObject.get("Company List");

            // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
            // Iterators differ from enumerations in two ways:
            // 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
            // 2. Method names have been improved.
            Iterator<JSONObject> iterator = companyList.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }





        //loaded game spec text
        Text gameLoadedTxt = new Text("Game Specification Loaded");
        gameLoadedTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
        grid.add(gameLoadedTxt,0, 1);
    }
}

