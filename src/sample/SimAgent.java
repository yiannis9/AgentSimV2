package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentController;
import java.util.Random;

public class SimAgent extends Agent {

    protected void setup() {

        addBehaviour(new TickerBehaviour(this, 30000) {
            public void onTick() {
                String abc = "AB";
                Random rand = new Random();
                char letter = abc.charAt(rand.nextInt(abc.length()));
                System.out.println(letter);
            }
        });
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Hello World. My name is "+ getLocalName());
            }
        });


    }
}
