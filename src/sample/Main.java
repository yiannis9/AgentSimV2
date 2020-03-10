package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.css.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


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

        gameLoad(grid);

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

    public void gameLoad (GridPane grid){
        try {

            File gameSpec = new File("src/sample/gameSpec.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(gameSpec);

            //optional, but recommended
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("threat");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //print treat ID
                    System.out.println("Threat id : " + eElement.getAttribute("id"));
                    //print Description
                    System.out.println("Description : " + eElement.getElementsByTagName("description").item(0).getTextContent());
                    //print Choices
                    System.out.println("Choices : ");
                    System.out.println(eElement.getElementsByTagName("choices").item(0).getTextContent());
//                    NodeList choices = (NodeList) eElement.getElementsByTagName("choices").item(0);
//                    for (int x = 0; x < choices.getLength(); x++) {
//
//                        Node nChoice = choices.item(x);
//
//                        if (nChoice.getNodeType() == Node.ELEMENT_NODE) {
//
//                            Element cc = (Element) nChoice;
//                            System.out.println((cc.getElementsByTagName("choice").item(0).getTextContent()));
//                        }
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //load game spec
        Text gameLoadedTxt = new Text("Game Specification Loaded");
        gameLoadedTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
        grid.add(gameLoadedTxt,0, 1);

    }
}
