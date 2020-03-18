package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.util.Random;


public class SimAgent extends Agent {

    public char curChoice;

    public String curPartner;

    public String state = "active";

    public String role;

    protected void setup() {
        //this is a gimmick to get random choices A or B
        String agName = getAID().getName().split("@")[0];
        String abc = "AB";
        Random rand = new Random();
        curChoice = abc.charAt(rand.nextInt(abc.length()));

        //getting roles from arguments
        Object[] args = getArguments();
        if (args != null) {
                role = (String) args[0];
            }

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
