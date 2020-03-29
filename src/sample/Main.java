package sample;

import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileReader;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class Main extends Application {
    public Integer agents;
    public Integer turns;
    public ArrayList<Rule> finalRuleList;
    public Groups groups;
    public ArrayList<String> departments = new ArrayList<String>();

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

        //create list to store jsonRules from jason file
        finalRuleList = new ArrayList<Rule>();

        //call func to load group specification rules
        groupsLoad(grid);
        //call func to load game specification rules
        gameSpecLoad(grid);


        //Sliders UI - quite a lot of work for sliders to work
        Label turnsLabel = new Label("Turns:");
        grid.add(turnsLabel, 0, 3);
        Slider turnSlider = new Slider(10,100,50);
        grid.add(turnSlider, 1, 3);
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

        grid.add(valSlider,2,3);

        //Agent Slider
        Label agLabel = new Label("Agents:");
        grid.add(agLabel, 0, 4);
        Slider agSlider = new Slider(10,100,50);
        grid.add(agSlider, 1, 4);
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
        grid.add(valAgSlider,2,4);
        //some bug with the slider not being able to parse default value (50) so had to get around it
        if (agents == null) {
            agents = 50;
        }


        //creating start button and its handling events.
        Button startBtn = new Button("Generate");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(startBtn);
        grid.add(hbBtn, 10, 10);

        startBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Game game = null;
                try {
                    game = new Game(agents,turns,finalRuleList,groups);
                } catch (StaleProxyException ex) {
                    ex.printStackTrace();
                }
                assert game != null;
                primaryStage.setScene(game.gameScene);
            }
        });

        //creating root node and adding the button
        StackPane root = new StackPane();
        root.getChildren().add(grid);

        //creating a scene to pass to stage
        Scene menu = new Scene(root, 1240, 720);

        //pass scene to the stage
        primaryStage.setScene(menu);
    }

    //no need to use this. Everything must be specified in the group's JSON file
    public  void gencheckboxes(GridPane grid){
        //create checkboxes and add to grid
        Label groupLabel = new Label("Groups:");
        grid.add(groupLabel, 0, 6);
        int gRowIndx = 7;
        int gColIndx = 0;
        for (int i = 0; i < departments.size(); i++) {
            String s = departments.get(i);
            if (i  % 2 == 0){
                gRowIndx++;
                gColIndx = 0;
            }
            //checkbox
            CheckBox javaCheckBox = new CheckBox(s);
            javaCheckBox.setIndeterminate(false);
            grid.add(javaCheckBox, gColIndx, gRowIndx);
            gColIndx++;
        }
    }

    public void gameSpecLoad (GridPane grid){


        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/sample/gameSpec.json"));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports return 0;java.util.List interface.
            JSONArray jsonRules = (JSONArray) jsonObject.get("Rules");

            // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
            for (int i = 0; i < jsonRules.size(); i++) {

                // get json object at index i
                JSONObject jsonRule = (JSONObject) jsonRules.get(i);
                //extract type and description from json object
                String type = (String) jsonRule.get("Type");
                String desc = (String) jsonRule.get("Desc");
                //create new rule and parse info
                Rule rule = new Rule(type,desc);

                //testing
//                System.out.println(type);
//                System.out.println(desc);


                //now we get the choice list of the rule to iterate over
                JSONArray choices = (JSONArray) jsonRule.get("ChoiceList");
                for (int x = 0; x < choices.size(); x++) {
                    //need to parse it as a new json object at index x
                    JSONObject chObj =  (JSONObject) choices.get(x);
                    //now we add
                    String cid= (String) chObj.get("CID");
                    String reward= (String) chObj.get("Reward");
                    String threatChange= (String) chObj.get("ThreatChange");
                    String cDesc= (String) chObj.get("cDesc");
                    Choice ch = new Choice(cid,reward,threatChange,cDesc);
                    rule.getChoiceList().add(ch);

                    //testing
//                    System.out.println(cid);
//                    System.out.println(reward);
//                    System.out.println(threatChange);
//                    System.out.println(cDesc);

                }
                System.out.println();
                finalRuleList.add(rule);

                //testing
//                for (Rule r: finalRuleList){
//                    System.out.println(r.getDesc());
//                    System.out.println(r.getType());
//                    System.out.println(r.getChoiceList().get(0).getReward());
//                    for (Choice cho: r.getChoiceList()){
//                        System.out.println(cho.getCID());
//                        System.out.println(cho.getcDesc());
//                        System.out.println(cho.getReward());
//                        System.out.println(cho.getThreatChange());
//                    }
//
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //loaded game spec text
        Text gameLoadedTxt = new Text("Game Specification Loaded");
        gameLoadedTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
        grid.add(gameLoadedTxt,0, 1);
    }

    public void groupsLoad (GridPane grid){

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("src/sample/Groups.json"));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports return 0;java.util.List interface.
            JSONArray jsonGroups = (JSONArray) jsonObject.get("Department");

            ArrayList<Role> allRoles = new ArrayList<Role>();
            // An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
            for (int i = 0; i < jsonGroups.size(); i++) {

                // get json object at index i
                JSONObject jsonGroup = (JSONObject) jsonGroups.get(i);
                //extract fields from json object
                String departmentName = (String) jsonGroup.get("Name");
                //testing
//                System.out.println(departmentName);
                JSONArray positions  = (JSONArray) jsonGroup.get("RoleList");


                for (int x = 0; x < positions.size(); x++) {
                    //need to parse it as a new json object at index x
                    JSONObject chObj =  (JSONObject) positions.get(x);
                    //now we add
                    String position= (String) chObj.get("Position");
                    String aDegree = (String) chObj.get("ActionDegree").toString();
                    Integer actionDegree = Integer.valueOf(aDegree);
                    String lmt= (String) chObj.get("Limit").toString();
                    Integer limit= Integer.valueOf(lmt);
                    Role role = new Role(position,departmentName,actionDegree,limit);
                    allRoles.add(role);
//                    System.out.println(position);
//                    System.out.println(actionDegree);
//                    System.out.println(limit);


                }

                departments.add(departmentName);
            }

            JSONArray attributesJSON = (JSONArray) jsonObject.get("Attributes");
            String[] attributes = new String[attributesJSON.size()];
            for (int i1 = 0; i1 < attributesJSON.size(); i1++) {
                Object att = attributesJSON.get(i1);
                String attS = (String) att;
                attributes[i1]=attS;
            }
//                System.out.println(Arrays.toString(attributes));
            groups = new Groups(attributes, allRoles);
//            System.out.println(groups.getAllRoles().size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //loaded game spec text
        Text gameLoadedTxt = new Text("Group Specification Loaded");
        gameLoadedTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
        grid.add(gameLoadedTxt,0, 2);
    }
}

