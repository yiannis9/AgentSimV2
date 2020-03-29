package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class SimAgent extends Agent {

    public Role role;
    private String abc = "AB";
    private Random rand = new Random();
    private Logger logger = null;
    private String attribute = "Cautious";
    private ACLMessage msg;
    private Groups groups;

        protected void setup() {
            //cleaning name of agent
            String agName = getAID().getName().split("@")[0];
            //getting roles from arguments
            Object[] args = getArguments();
            if (args != null) {
                role = (Role) args[0];
                groups = (Groups) args[2];
                //managed to get logger as argument
                logger = (Logger) args[1];
                }

            //SET RANDOM ATTRIBUTE AND INITIALISE
            attribute = groups.getAttributes()[rand.nextInt(groups.getAttributes().length)];
            logger.info(agName + "("+ role.getPositionName() +" - "+ attribute + ") has been initialised");


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
                                //check for agent attribute
                                String curChoice = "";
                                if (Objects.equals(attribute, "Randomiser")) {
                                    curChoice = String.valueOf(choicesString.charAt(rand.nextInt(chSize)));
                                }
                                else if (Objects.equals(attribute, "Cautious")) {
                                    //get least dangerous choice in terms of threat change
                                    Choice leastDangerous = receivedRule.getChoiceList().get(0);
                                    for (Choice ch: receivedRule.getChoiceList()){
                                        if (Double.parseDouble(leastDangerous.getThreatChange()) >= Double.parseDouble(ch.getThreatChange())){
                                            leastDangerous =ch;
                                        }
                                    }
                                    curChoice = leastDangerous.getCID();
                                } else if (Objects.equals(attribute, "Profit Maximiser")) {
                                    //get most rewarding choice in terms of reward
                                    Choice moreRewarding = receivedRule.getChoiceList().get(0);
                                    for (Choice ch: receivedRule.getChoiceList()){
                                        if (Integer.parseInt(moreRewarding.getReward()) <= Integer.parseInt(ch.getReward())){
                                            moreRewarding =ch;
                                        }
                                    }
                                    curChoice = moreRewarding.getCID();
                                }
                                msgChoice.setContent(receivedRule.getDesc() + ":" + curChoice + ":"+ role.getDepartment());
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
