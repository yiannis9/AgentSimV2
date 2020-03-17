package sample;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Game {
    public Scene gameScene;
    public StackPane stack;
    public Integer Agents;
    public Integer Turns;
    public BorderPane canvas;
    public ArrayList<Rule> ruleList;
    public ArrayList<String> rolesList;
    public Logger logger;
    public jade.core.Runtime runtime;
    private boolean gameRunning = true;
    private Integer turnsTaken=0;

    public Game(Integer Agents, Integer Turns, ArrayList<Rule> ruleList) throws StaleProxyException {
        //init stack node and scene
        canvas = new BorderPane();
        gameScene = new Scene(canvas, 1240, 720);

        //grid will stay but testing scene changing
        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //only for testing showing grids
//        grid.setGridLinesVisible(true);

        //run initialise logger method
        initLogger();



        //create exit button
        genExitBtn();

        //calling method that creates the top menu displaying agents and turns
        createMenuBar(Turns, Agents);
        canvas.setCenter(grid);

        //initialise agents
        initAgents(grid,Agents);

    }


    //method to initialise menu bar
    public void createMenuBar(Integer Turns, Integer Agents) {
        MenuBar menuBar = new MenuBar();

        final Menu menu1 = new Menu("Turn: "+ turnsTaken + " / " + Turns);
        final Menu menu2 = new Menu("Agents: "+ Agents);

        menuBar.getMenus().addAll(menu1, menu2);
        canvas.setTop(menuBar);

    }

    public void genExitBtn(){
        // Exit Button
        Button exitBtn = new Button("Exit");
        exitBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Stage stage = (Stage) canvas.getScene().getWindow();

                runtime.shutDown();

                stage.close();
            }
        });
        StackPane bottomStack = new StackPane();

        //Retrieving the observable list of the Stack Pane
        ObservableList<Node> list = bottomStack.getChildren();

        //Adding all the nodes to the pane
        list.addAll(exitBtn);
        canvas.setBottom(bottomStack);

    }

    //initialise logger method. saves log in SimLog.log
    public void initLogger () {
        logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("/home/keravnos/Downloads/AgentSimV2/src/sample/Simlog.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            logger.info("LOGGER INITIALISED");

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

    }

    //initialise all agents method and order them in grid
    public void initAgents (GridPane grid,Integer Agents) {
        // init agents
        AgentController agent = null;
        VisAgent guiAgent = null;
        try {
            //Get the JADE runtime interface (singleton)
            runtime = jade.core.Runtime.instance();
            //Create a Profile, where the launch arguments are stored
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            //create a non-main agent container
            ContainerController container = runtime.createMainContainer(profile);

            //create Engine agent which handles all other agents
            Object reference = new Object();
            Object args[] = new Object[1];
            args[0] = reference;
            AgentController agentEngine = container.createNewAgent("agent-engine", EngineAgent.class.getName(), args);
            agentEngine.start();

            //managing roles
            rolesList = new ArrayList<String>();
            rolesList.add("Supervisor");
            rolesList.add( "CEO");
            rolesList.add("Employee");
            rolesList.add("ITadmin");
            Random randRole = new Random();
            try {
                int gridRow= 0;
                int gridCol = 0;
                int circleCol = 1;
                for (int agentcounter = 0; agentcounter < Agents; agentcounter++) {
                    //getting random roles and passing to agents
                    String role = rolesList.get(randRole.nextInt(rolesList.size()));
                    args[0] = role;
                    agent = container.createNewAgent("agent-" + agentcounter,
                            SimAgent.class.getName(), args
                    );

                    // Fire up the agent
                    agent.start();

                    //class wrapping abstraction
                    //VisAgent uses SimAgent which is the actual agent class connected to jade
                    //VisAgent is only responsible for the gui visuals
                    guiAgent = new VisAgent(agent, logger);
                    int inc=2;
                    if (agentcounter%10==0){
                        gridCol+= 2;
                        circleCol+=2;
                        gridRow=0;
                    }
                    grid.add(guiAgent.text, gridCol, gridRow);
                    grid.add(guiAgent.circle, circleCol, gridRow);
                    gridRow++;

                }
                // --TESTING-- testing printing name of all agents.
//                initAgentLister(container);

            } catch (StaleProxyException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //tester function. creates agentLister which prints all agents in the container.
    public void initAgentLister (ContainerController container) throws StaleProxyException {
        Object reference = new Object();
        Object args[] = new Object[1];
        args[0] = reference;
        AgentController agentLister = container.createNewAgent("agentLister",
                AgentLister.class.getName(),
                args);
        agentLister.start();
    }
}
