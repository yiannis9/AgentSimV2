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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;


public class SimAgent extends Agent {

    public char curChoice;
    public String curPartner;
    public String state = "active";
    public String role;
    private String abc = "AB";
    private Random rand = new Random();
    private Logger logger = null;
    private double threatRate = 0.5;
    private double points= 500;

    protected void setup() {
        //cleaning name of agent
        String agName = getAID().getName().split("@")[0];
        //getting roles from arguments
        Object[] args = getArguments();
        if (args != null) {
            role = (String) args[0];

            //managed to get logger as argument
            logger = (Logger) args[1];
            logger.info(agName + "("+ role + ") has been initialised");
            }

        //this is a gimmick to get random choices A or B
        curChoice = abc.charAt(rand.nextInt(abc.length()));


        //receiving ACl messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){

                    String sender = msg.getSender().getName().split("@")[0];
                    String concatenatedChoices = msg.getContent();
                    String[] stripedChoices = concatenatedChoices.split("[||]");
                    ArrayList<Choice> currentChoices = new ArrayList<Choice>();
                    for (String s: stripedChoices){
                        if (!s.isEmpty()){
                            //stripped choice broken into its fields
                            String[] sc = s.split("[:]");
                            Choice choice = new Choice(sc[0],sc[1],sc[2],sc[3]);
                            currentChoices.add(choice);
                        }
                    }
//                    JOptionPane.showMessageDialog(null,
//                            "Message received : "+ msg.getContent());
                } else {
                    block();
                }
            }
        });

//        //sending ACL messages
//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);
//                msgChoice.setContent(String.valueOf(curChoice));
//                msgChoice.addReceiver(new AID("agent-engine", AID.ISLOCALNAME));
//                send(msgChoice);
//            }
//        });



    }

}
