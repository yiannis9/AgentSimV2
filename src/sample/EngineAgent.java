package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.util.Random;
import java.util.logging.Logger;


public class EngineAgent extends Agent {

    protected void setup() {
        //setting up variables
        char curChoice;
        String curTarget;
        Integer Turns=0;

        String agName = getAID().getName().split("@")[0];
        String abc = "AB";
        Random rand = new Random();
        curChoice = abc.charAt(rand.nextInt(abc.length()));

        //getting roles from arguments
        Object[] args = getArguments();
        if (args != null) {
            Turns = (Integer) args[0];

            //managed to get logger as argument
            Logger logger = (Logger) args[1];
            logger.info("GAME ENGINE AGENT INITIALISED");
        }


        //receiving ACl messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){

                    JOptionPane.showMessageDialog(null,
                            "Message received : "
                                    + msg.getContent()
                    );
                } else {
                    block();
                }
            }
        });



        //sending ACL messages
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);
                msgChoice.setContent(String.valueOf(curChoice));
                msgChoice.addReceiver(new AID("agent-engine", AID.ISLOCALNAME));
                send(msgChoice);

            }
        });


    }

    public void getTurns(Integer turns) {

    }

}
