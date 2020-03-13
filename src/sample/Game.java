package sample;

import jade.core.*;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
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
    public Logger logger;

    public Game(Integer Agents, Integer Turns, ArrayList<Rule> ruleList) {
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

        initLogger();


        //initiallise agents
        initAgents(grid,Agents);


        //calling method that creates the top menu displaying agents and turns
        createMenuBar(Turns, Agents);
        canvas.setCenter(grid);

    }

    /*
    method to initialise menu bar
    */
    public void createMenuBar(Integer Turns, Integer Agents) {
        MenuBar menuBar = new MenuBar();
        final Menu menu1 = new Menu("Turn: "+ Turns);
        final Menu menu2 = new Menu("Agents: "+ Agents);

        menuBar.getMenus().addAll(menu1, menu2);
        canvas.setTop(menuBar);

    }

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

    public void initAgents (GridPane grid,Integer Agents) {
        // init agents
        AgentController agent = null;
        try {
            //Get the JADE runtime interface (singleton)
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            //Create a Profile, where the launch arguments are stored
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            //create a non-main agent container
            ContainerController container = runtime.createMainContainer(profile);
            try {
                int gridRow= 0;
                int gridCol = 0;
                int circleCol = 1;
                for (int agentcounter = 0; agentcounter < Agents; agentcounter++) {
                    Object reference = new Object();
                    Object args[] = new Object[1];
                    args[0] = reference;
                    agent = container.createNewAgent("agent-" + agentcounter,
                            SimAgent.class.getName(),
                            args);
                    // Fire up the agent
                    agent.start();

                    //class wrapping abstraction
                    //VisAgent uses SimAgent which is the actual agent class connected to jade
                    //VisAgent is only responsible for the gui visuals
                    VisAgent guiAgent = new VisAgent(agent,logger);
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
