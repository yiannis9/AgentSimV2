package sample;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;


public class EngineAgent extends Agent {

    //for generating unique CIDs
    protected static int cidCnt = 0;
    String cidBase ;
    //
    private Random rand = new Random();
    private String abc = "AB";
    private Logger logger=null;
    ACLMessage msg;
    public ArrayList<Rule> ruleList;
    private ArrayList<AgentPointCalculator> pointsAndRates;
    private Integer Agents=0;

    protected void setup() {
        //setting up important variables which will be used often
        char curChoice;
        String curTarget;
        Integer Turns=0;
        ArrayList<Rule> ruleList = null;

        String agName = getAID().getName().split("@")[0];

        //getting variables from parsed agent arguments
        Object[] args = getArguments();
        if (args != null) {
            Turns = (Integer) args[0];
            //managed to get logger as argument
            logger = (Logger) args[1];
            Agents= (Integer) args[2];
            ruleList = (ArrayList<Rule>) args[3];
            logger.info("GAME ENGINE AGENT INITIALISED. Agents:"+Agents+" /Turns:"+Turns);
        }

        //initialise list with points and rates
        genPointsAndRateList(Agents);

        ArrayList<Rule> finalRuleList = ruleList;
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                for(int turnsTaken=0;turnsTaken<=50;turnsTaken++){

                    //every turn we roll for a random number of threats exposed to
                    Integer ThreatsThisTurn = rand.nextInt(Agents);
                    //distribute threats to random agents according to random number of threats per turn
                    for(int threat=1;threat<=ThreatsThisTurn;threat++){
                        Random rnd = new Random();
                        //get random threat from ruleList
                        Integer randomThreatIndex = rnd.nextInt(finalRuleList.size());
                        Integer compromisedAgentNo = rnd.nextInt(Agents);
                        String compromisedAgentName = "agent-"+compromisedAgentNo;
                        Rule selectedThreat = finalRuleList.get(randomThreatIndex);
                        logger.info(compromisedAgentName+" has been compromised due to the following threat: "+ selectedThreat.getDesc());

                    }

                    logger.info("Turn: "+turnsTaken+" ended.");
                }
            }
        });



        //receiving ACl messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){

                    String sender = msg.getSender().getName().split("@")[0];
                    logger.info("Response from " + sender + " : " + msg.getContent());

                    //gui option for messages
//                    JOptionPane.showMessageDialog(null,
//                            "Message received : " + msg.getContent());
                } else {
                    block();
                }
            }
        });


        //sending ACL messages
//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);
//                msgChoice.setContent("manasoulawl");
//                msgChoice.addReceiver(new AID("agent-engine", AID.ISLOCALNAME));
//                send(msgChoice);
//
//            }
//        });

    }

    //for generating unique CIDs
    public String genCID(String cidBase, int cidCnt) {

        if (cidBase==null) {
            cidBase = getLocalName() + hashCode() +
                    System.currentTimeMillis()%10000 + "_";
        }
        return  cidBase + (cidCnt++);
    }

    //generate list with points and threat rate
    public void genPointsAndRateList (Integer Agents){
        pointsAndRates = new ArrayList<AgentPointCalculator>();
        for(int agent=0;agent<Agents;agent++){
            String agName = "agent-"+agent;
            pointsAndRates.add(new AgentPointCalculator(500,0.5,agName));
        }
    }

}
