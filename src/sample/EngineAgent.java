package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.util.Random;


public class EngineAgent extends Agent {

    public char curChoice;

    public String curPartner;


    protected void setup() {

        String agName = getAID().getName().split("@")[0];
        String abc = "AB";
        Random rand = new Random();
        curChoice = abc.charAt(rand.nextInt(abc.length()));

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


    }

}
