package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;


public class EngineAgent extends Agent {

    //for generating unique CIDs
    protected static int cidCnt = 0;
    String cidBase ;
    //
    private Logger logger=null;
    public ArrayList<Rule> ruleList = new ArrayList<Rule>();
    private ArrayList<AgentPointCalculator> pointsAndRates = new ArrayList<AgentPointCalculator>();
    private Integer Agents=0;
    public ArrayList<Rule> finalRuleList;
    private Integer turnsTaken=1;
    private Integer Turns=0;
    private ArrayList<String>  infectedAgents;

    protected void setup() {
        //setting up important variables which will be used often
//        SequentialBehaviour seq = new SequentialBehaviour();


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

        //GENERATE AGENTS IN THE SAME CONTAINER AND PASS ROLES
        ArrayList<String> rolesList = new ArrayList<String>();
        rolesList.add("Supervisor");
        rolesList.add( "CEO");
        rolesList.add("Employee");
        rolesList.add("ITadmin");
        createAgents(rolesList);

        //initialise list with points and rates
        ArrayList<AgentPointCalculator> pointsAndRates =genPointsAndRateList(Agents);
        ArrayList<Rule> finalRuleList = ruleList;

        //I do this to give time for other agents to launch their arguments and print in the logger
        doWait(2000);

        
        TickerBehaviour mainLoop = new TickerBehaviour(this,2000) {
            private boolean finished;
            @Override
            public void onTick() {
                //INIT INFECTED AGENT LIST EACH TURNS--EVERY TURN THE INFECTED AGENTS CHANGE!!!
                //PUT HERE ALL NECESSARY VARIABLES THAT NEED TO BE RE-INITIALLISED EVERY TURN
                ArrayList<String> infectedAgents = new ArrayList<String>();
                Random rnd = new Random();
                SequentialBehaviour seq = new SequentialBehaviour();
                ArrayList<ACLMessage> replies = new ArrayList<ACLMessage>();
                //1
                //FINDING INFECTED AGENTS EACH TURN BEHAVIOUR
                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //POPULATE INFECTED AGENT LIST EVERY TURN
                        for (AgentPointCalculator a : pointsAndRates) {
                            //perform threat check
                            Double threatCheck = rnd.nextDouble();
                            if (a.getThreatRate() < threatCheck) {
                                infectedAgents.add(a.getName());
                            }
                        }
                        logger.info("NEW DECISIONS FOR AGENTS TO TAKE THIS TURN!");
                    }
                });
                //2
                //SENDING ACL MESSAGES WITH DECISIONS
                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //distribute threats to infected agents according to random number of threats per turn
                        for (String infectedAgent : infectedAgents) {
                            if (infectedAgents.size() !=0) {
                                //get random threat from ruleList
                                Integer randomThreatIndex = rnd.nextInt(finalRuleList.size());
                                Rule selectedThreat = finalRuleList.get(randomThreatIndex);
                                //report compromised agents in log
                                logger.warning(infectedAgent + " has to postpone work to make a decision: " + selectedThreat.getDesc());

                                //cast rule and choices in a single string to send as ACL
                                String encodedChoices = selectedThreat.getDesc() + "/" + selectedThreat.getType() + "/";
                                for (Choice c : selectedThreat.getChoiceList()) {
                                    encodedChoices += c.getCID() + ":" + c.getReward() + ":" + c.getThreatChange() + ":" + c.getcDesc() + "||";
                                }
                                //gimmick to remove last 2 characters coz it makes it easier later
                                encodedChoices = encodedChoices.substring(0, encodedChoices.length() - 2);

                                //SEND ACL MSG
                                String finalEncodedChoices = encodedChoices;
                                ACLMessage msgChoice = new ACLMessage(ACLMessage.INFORM);
                                msgChoice.setContent(finalEncodedChoices);
                                msgChoice.addReceiver(new AID(infectedAgent, AID.ISLOCALNAME));
                                send(msgChoice);

                            }else {
                                logger.info("No decisions to be taken this turn");
                            }
                        }
                    }
                });
                //3
                //RECEIVING ACL MESSAGES!
                seq.addSubBehaviour(new TickerBehaviour(this.myAgent,500) {
                    @Override
                    public void onTick() {
                        //IF WE GOT ALL REPLIES FROM INFECTED AGENTS OR OTHERWISE
                        if (replies.size()!=infectedAgents.size()) {
                            ACLMessage msg = receive();
                            replies.add(msg);
                            logger.info("NEW MESSAGE IN ENGINE MAIL BOX");

                        }else{
                            logger.info("ALL REPLIES RECEIVED");
                            done();
                            stop();
                        }
                    }
                });
                //4
                //GET EACH REPLY AND APPLY THE CHANGES TO POINTS AND RATE
                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        for (ACLMessage msg : replies) {
                            //beautifying sender
                            String sender = msg.getSender().getName().split("@")[0];

                            //APPLYING DECISIONS
                            String[] descAndChoice = msg.getContent().split("[:]");
                            String cDesc = descAndChoice[0];
                            String CID = descAndChoice[1];
                            logger.info("Response from " + sender + " : " +cDesc +" ==> Choice: " + CID);

                            //HERE I GET THE CHOICE AND APPLY IT TO THE POINTS AND RATES
                            //WORKS BY FIRST MATCHING THE RULE AND FINDING THE CORRECT VARIABLES
                            for (Rule r : ruleList) {
                                    if (Objects.equals(r.getDesc(),cDesc)) {
                                        for (Choice ch : r.getChoiceList()) {
                                            if (Objects.equals(ch.getCID(),CID)) {
                                                Integer addedBonus = Integer.valueOf(ch.getReward());
                                                Double threatChange = Double.valueOf(ch.getThreatChange());
                                                for (AgentPointCalculator apc : pointsAndRates) {
                                                    if (Objects.equals(apc.getName(),sender)) {
                                                        apc.setPoints(apc.getPoints() + addedBonus);
                                                        apc.setThreatRate(apc.getThreatRate() * threatChange);
                                                        logger.info(sender+" now has "+apc.getPoints() +", "+ apc.getThreatRate());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                        }
                    }
                });
                //5
                //EXECUTE THIS AT THE END OF EACH TURN
                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //end of each turn add points to non infected agents
                        for (AgentPointCalculator ag : pointsAndRates) {
                            if (infectedAgents.contains(ag.getName())) {
                                ag.setPoints(ag.getPoints() + 300);
                            }
                        }
                        logger.info("Turn "+ turnsTaken + " has ended.");
                        turnsTaken++;

                        //STOP MAIN TICKER BEHAVIOUR ENDING THE ENGINE'S TASKS
                        if (turnsTaken == Turns+1){
                            //THIS CAN BE LEFT ENTIRELY AT THE END
                            //FINAL LOGGING INFO- AGENT SCORES
                            logger.info("\n------SIMULATION ENDED------");
                            logger.info("\n----------------------------");
                            //--------LOG AGENT SCORES----------
                            logger.info("\n------AGENT SCORES:------");
                            for (AgentPointCalculator ag: pointsAndRates){
                                logger.info(String.format("%s ==> Points: %d, Rate: %s", ag.getName(), ag.getPoints(), ag.getThreatRate()));
                            }
                            done();
                            stop();
                        }
                    }
                });
                //ADD SEQUENTIAL TO TICKER BEHAVIOUR
                addBehaviour(seq);
            }
        };
        //ADD MY MAIN BEHAVIOUR TO SETUP SO THAT IT IS SCHEDULED
        addBehaviour(mainLoop);


    //END OF SETUP
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

    public void createAgents(ArrayList<String> rolesList) {
        Random randRole = new Random();
        Object args2[] = new Object[2];
        args2[1] = logger;
        for (int agentcounter = 0; agentcounter < Agents; agentcounter++) {
            //getting random roles and passing to agents
            String role = rolesList.get(randRole.nextInt(rolesList.size()));
            args2[0] = role;
            AgentController agent = null;
            try {
                agent = this.getContainerController().createNewAgent("agent-" + agentcounter,
                        SimAgent.class.getName(), args2);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }

            // Fire up the agent
            try {
                agent.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

    }
}
