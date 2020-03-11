package sample;

import jade.core.*;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Game {
    public Scene gameScene;
    public StackPane stack;
    public Integer Agents;
    public Integer Turns;
    public BorderPane canvas;

    public Game(Integer Agents, Integer Turns) {
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

        // init agents
        AgentController agent = null;
        AgentController agentLister = null;
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

                    //testing visuals of agents not launching from jade but from class directly
                    VisAgent guiAgent = new VisAgent(agent);
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
//                Object reference = new Object();
//                Object args[] = new Object[1];
//                args[0] = reference;
//                agentLister = container.createNewAgent("agentLister",
//                        AgentLister.class.getName(),
//                        args);
//                agentLister.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



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
}
