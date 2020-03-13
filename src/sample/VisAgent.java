package sample;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.logging.Logger;

public class VisAgent extends SimAgent{
    public Text text;
    public Circle circle;
    public Integer infectionRate;
    public AgentController agent;
    public String name;
    public String state;

    public VisAgent (AgentController agent, Logger logger) throws StaleProxyException {
        this.agent = agent;
        //creating agent label
        try {
            text = new Text();
            text.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
            //splitting at @ so that it looks cleaner in simulation gui
            this.name = this.agent.getName().split("@")[0];
            text.setText(this.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //creating agent circle
        circle = new Circle();
        circle.setCenterX(10.0f);
        circle.setCenterY(10.0f);
        circle.setRadius(10.0f);
        circle.setFill(Color.GREEN);

        greetAgent(logger);

    }

    public void greetAgent (Logger logger) {
        // the following statement is used to log any messages
        logger.info("Hello! My name is " + this.name);
    }


}
