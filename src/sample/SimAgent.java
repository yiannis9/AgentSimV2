package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.wrapper.AgentController;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class SimAgent extends Agent {

    public char curChoice;

    protected void setup() {

        try {
            Object[] args = getArguments();
            String s;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    s = (String) args[i];
                    System.out.println("p" + i + ": " + s);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        addBehaviour(new SimpleBehaviour() {

            @Override
            public void action() {

                String abc = "AB";
                Random rand = new Random();
                curChoice = abc.charAt(rand.nextInt(abc.length()));
                System.out.println( getAID() + " has voted " + curChoice);


            }

            @Override
            public boolean done() {
                return true;
            }
        });



    }


    public char getChoice(){
        return curChoice;
    }
}
