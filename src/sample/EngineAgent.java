package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;


public class EngineAgent extends Agent {

    //for generating unique CIDs
    protected static int cidCnt = 0;
    String cidBase ;
    //
    private String abc = "AB";
    private Logger logger=null;
    public ArrayList<Rule> ruleList = new ArrayList<Rule>();
    private ArrayList<AgentPointCalculator> pointsAndRates = new ArrayList<AgentPointCalculator>();
    private Integer Agents=0;
    public ArrayList<Rule> finalRuleList;

    protected void setup() {
        //setting up important variables which will be used often
        char curChoice;
        String curTarget;
        Integer Turns=0;


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


        //testing
//        for (Rule r: ruleList){
//            System.out.println(r.getDesc());
//            System.out.println(r.getType());
//            for (Choice ch: r.getChoiceList()){
//                System.out.println(ch.getCID());
//                System.out.println(ch.getcDesc());
//                System.out.println(ch.getReward());
//                System.out.println(ch.getThreatChange());
//            }
//
//        }

        //initialise list with points and rates
        ArrayList<AgentPointCalculator> pointsAndRates =genPointsAndRateList(Agents);
        ArrayList<Rule> finalRuleList = ruleList;
        Integer finalTurns = Turns;
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {

                //turns loop
                for(int turnsTaken = 0; turnsTaken<= finalTurns; turnsTaken++){
                    Random rnd = new Random();

                    //fill list of infected agents each turn
                    ArrayList<String> infectedAgents = new ArrayList<String>();
                    for (AgentPointCalculator a:pointsAndRates){
                        //perform threat check
                        Double threatCheck = rnd.nextDouble();
                        if (a.getThreatRate()<threatCheck){
                            infectedAgents.add(a.getName());
                        }
                    }

                    //distribute threats to infected agents according to random number of threats per turn
                    for(String infectedAgent:infectedAgents){

                        //get random threat from ruleList
                        Integer randomThreatIndex = rnd.nextInt(finalRuleList.size());
                        Rule selectedThreat = finalRuleList.get(randomThreatIndex);
                        //report compromised agents in log
                        logger.warning(infectedAgent+" has been compromised due to the following threat: "+ selectedThreat.getDesc());

                        //cast choices in a single string to send as ACL
                        String encodedChoices = "";
                        for (Choice c: selectedThreat.getChoiceList()){
                            encodedChoices += c.getCID()+"//"+c.getReward()+"//"+c.getThreatChange()+"//"+c.getcDesc()+"//";
                        }
                        System.out.println(encodedChoices);
                        //sending ACL messages
                        String finalEncodedChoices = encodedChoices;
                        addBehaviour(new OneShotBehaviour() {
                            @Override
                            public void action() {
                                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);

                                msgChoice.setContent(finalEncodedChoices);
                                msgChoice.addReceiver(new AID(infectedAgent, AID.ISLOCALNAME));
                                send(msgChoice);

                            }
                        });

                    }
                    for (AgentPointCalculator ag: pointsAndRates){
                        if (infectedAgents.contains(ag.getName())){
                            ag.setPoints(ag.getPoints()+300);
                        }
                    }

                    logger.info("Turn: "+turnsTaken+" ended.");
                }
                for (AgentPointCalculator ag: pointsAndRates){
                    System.out.println(ag.getName()+"// "+ ag.getPoints());
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
    public ArrayList<AgentPointCalculator> genPointsAndRateList (Integer Agents){
        ArrayList<AgentPointCalculator> pointsAndRates = this.pointsAndRates;
        for(int agent = 0; agent<Agents; agent++){
            String agName = "agent-"+agent;
            Integer points = 500;
            Double rate = 0.5;
            pointsAndRates.add(new AgentPointCalculator(points,rate,agName));
        }
        return pointsAndRates;
    }

}
