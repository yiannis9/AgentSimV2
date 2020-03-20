package sample;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;


public class EngineAgent extends Agent {

    //for generating unique CIDs
    protected static int cidCnt = 0;
    String cidBase ;
    private Random rand = new Random();
    private String abc = "AB";
    private Logger logger=null;
    ACLMessage msg;
    private ACLMessage event;
    private ArrayList<AgentPointCalculator> pointsAndRates;

    protected void setup() {
        //setting up important variables which will be used often
        char curChoice;
        String curTarget;
        Integer Turns=0;
        Integer Agents=0;
        String agName = getAID().getName().split("@")[0];


        //getting variables from parsed agent arguments
        Object[] args = getArguments();
        if (args != null) {
            Turns = (Integer) args[0];
            Agents= (Integer) args[2];
            //managed to get logger as argument
            logger = (Logger) args[1];
            logger.info("GAME ENGINE AGENT INITIALISED.");
        }

        //initialise list with points and rates
        genPointsAndRateList(Agents);

        //create new msg
        msg = new ACLMessage( ACLMessage.QUERY_REF );

        for(int turnsTaken=0;turnsTaken<=50;turnsTaken++){


            //every turn we roll for a random number of threats exposed to
            Integer ThreatsThisTurn = rand.nextInt(Agents);
            //distribute threats to random agents according to random number of threats per turn
            for(int threat=1;threat<=ThreatsThisTurn;threat++){
                logger.info("entering agent loop");

            }

            logger.info("Turn: "+turnsTaken+" ended.");
        }


        send ( msg );



        //receiving ACl messages
//        addBehaviour(new CyclicBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msg = receive();
//                if (msg != null){
//
//                    String sender = msg.getSender().getName().split("@")[0];
//                    logger.info("Response from " + sender + " : " + msg.getContent());
//
//                    //gui option for messages
////                    JOptionPane.showMessageDialog(null,
////                            "Message received : " + msg.getContent());
//                } else {
//                    block();
//                }
//            }
//        });



        //sending ACL messages
//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);
//                msgChoice.setContent(String.valueOf(curChoice));
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
