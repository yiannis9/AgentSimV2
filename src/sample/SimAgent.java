package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;
import java.util.logging.Logger;

public class SimAgent extends Agent {

    public String role;
    private String abc = "AB";
    private Random rand = new Random();
    private Logger logger = null;
    private String attribute = "randomiser";
    private ACLMessage msg;

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

        Rule receivedRule = null;
        //receiving ACl messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {

                    String sender = msg.getSender().getName().split("@")[0];
                    // get message content
                    String concatenatedRule = msg.getContent();
                    // strip the string into an array separating Desc,Type,ChoiceList
                    String[] stripedRule = concatenatedRule.split("[/]");
                    // Create new rule with the striped strings
                    final Rule receivedRule = new Rule(stripedRule[1], stripedRule[0]);
                    // now we do the same with choices
                    String concatenatedChoices = stripedRule[2];
                    // iterate over array and add choices into new rule
                    String[] stripedChoices = concatenatedChoices.split("[||]");
                    for (String s : stripedChoices) {
                        if (!s.isEmpty()) {
                            //stripped choice broken into its fields
                            String[] sc = s.split("[:]");
                            //breaking it into ID,Reward,threatChange,Desc
                            Choice choice = new Choice(sc[0], sc[1], sc[2], sc[3]);
                            receivedRule.getChoiceList().add(choice);
                        }
                    }
                    //HERE WE ADD INTELLIGENT DECISION MECHANISMS OF AGENTS
                    //DEPENDING ON ATTRIBUTE

                    //sending ACL messages
                    addBehaviour(new OneShotBehaviour() {
                        @Override
                        public void action() {

                            ACLMessage msgChoice = msg.createReply();
                            String choicesString = "ABCD";
                            //size of choice list of selected rule. This enables us to have rules of varying
                            //no of choices without getting null pointers or out of index entries. I HOPE
                            int chSize = receivedRule.getChoiceList().size();
                            String curChoice = String.valueOf(choicesString.charAt(rand.nextInt(chSize)));
                            msgChoice.setContent(receivedRule.getDesc()+":"+ String.valueOf(curChoice));
                            send(msgChoice);
                        }
                    });

                } else {
                    block();
                }
            }
        });



    }

}
