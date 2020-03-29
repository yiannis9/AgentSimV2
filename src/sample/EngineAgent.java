package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

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
    public ArrayList<Rule> finalRuleList;
    private ArrayList<AgentPointCalculator> pointsAndRates = new ArrayList<AgentPointCalculator>();
    private Integer Agents=0;
    private Integer turnsTaken=1;
    private Integer Turns=0;
    private ArrayList<String>  infectedAgents;
    private Groups groups;

    protected void setup() {
        //getting variables from parsed agent arguments
        Object[] args = getArguments();
        if (args != null) {
            Turns = (Integer) args[0];
            //managed to get logger as argument
            logger = (Logger) args[1];
            Agents= (Integer) args[2];
            ruleList = (ArrayList<Rule>) args[3];
            groups = (Groups) args[4];
            logger.info("GAME ENGINE AGENT INITIALISED. Agents:"+Agents+" /Turns:"+Turns);
        }

        //initialise list with points and rates
        ArrayList<AgentPointCalculator> pointsAndRates =genPointsAndRateList(Agents);
        ArrayList<Rule> finalRuleList = ruleList;

        //GENERATE AGENTS IN THE SAME CONTAINER AND PASS ROLES
        createAgents(groups);


        //I do this to give time for other agents to launch their arguments and print in the logger
        doWait(2000);

        //min maxing simulation times with the number of agents...
        int ticks;
        if (Agents==10){
            ticks=1000;
        }else if (Agents>10 && Agents<50){
            ticks=5000;
        }
        else{
            ticks=10000;
        }
        //INITIALISE MAIN LOOP OF ENGINE
        TickerBehaviour mainLoop = new TickerBehaviour(this,ticks) {
            private boolean finished;
            @Override
            public void onTick() {
                //INIT INFECTED AGENT LIST EACH TURNS--EVERY TURN THE INFECTED AGENTS CHANGE!!!
                //PUT HERE ALL NECESSARY VARIABLES THAT NEED TO BE RE-INITIALISED EVERY TURN
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
                        if (!infectedAgents.isEmpty()){
                            logger.info(infectedAgents.size()+ " NEW DECISIONS FOR AGENTS TO TAKE THIS TURN!");
                        }else{
                            logger.info("NO NEW DECISIONS TO TAKE THIS TURN!");
                        }
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
                seq.addSubBehaviour(new TickerBehaviour(this.myAgent,10) {
                    @Override
                    public void onTick() {
                        //IF WE GOT ALL REPLIES FROM INFECTED AGENTS OR OTHERWISE
                        if (replies.size() != infectedAgents.size()) {
                            ACLMessage msg = receive();
                            replies.add(msg);
                            logger.info("NEW MESSAGE IN ENGINE MAIL BOX");
                        } else {
                            logger.info(replies.size()+ " REPLIES RECEIVED");
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
                            String department = descAndChoice[2];
                            logger.info("Response from " + sender + " : " +cDesc +" ==> Choice: " + CID);

                            //HERE I GET THE CHOICE AND APPLY IT TO THE POINTS AND RATES
                            //WORKS BY FIRST MATCHING THE RULE AND FINDING THE CORRECT VARIABLES
                            for (Rule r : ruleList) {
                                    if (Objects.equals(r.getDesc(),cDesc)) {
                                        for (Choice ch : r.getChoiceList()) {
                                            if (Objects.equals(ch.getCID(),CID)) {
                                                //FIND CHOICE REWARD AND THREAT CHANGE
                                                Integer addedBonus = Integer.valueOf(ch.getReward());
                                                Double threatChange = Double.valueOf(ch.getThreatChange());
                                                //AND APPLY THEM TO APC
                                                String dep = "";
                                                Integer ActionDegree = null;
                                                for (AgentPointCalculator apc : pointsAndRates) {
                                                    //APPLY ONLY TO SENDER FIRST
                                                    if (Objects.equals(apc.getName(),sender)) {
                                                        dep = apc.getRole().getDepartment();
                                                        ActionDegree = apc.getRole().getActionDegree();
                                                        apc.setPoints(apc.getPoints() + addedBonus);
                                                        apc.setThreatRate(apc.getThreatRate() * threatChange);
                                                        logger.info(sender+" now has "+apc.getPoints() +", "+ apc.getThreatRate());
                                                    }
                                                }
                                                //THEN TO EVERYONE IN DEPARTMENT IF DEGREE IS 2.
                                                if (Objects.equals(ActionDegree,2)){
                                                    for (AgentPointCalculator apc : pointsAndRates) {
                                                        if (apc.getName() != sender){
                                                            if (Objects.equals(apc.getRole().getDepartment(),dep)){
                                                                apc.setPoints(apc.getPoints() + addedBonus);
                                                                apc.setThreatRate(apc.getThreatRate() * threatChange);
                                                                logger.info(apc.getName()+" now has "+apc.getPoints() +", "+ apc.getThreatRate()+ " because of "+sender+"'s decisions.");
                                                            }
                                                        }
                                                    }
                                                //OR TO ALL AGENTS IF DEGREE IS 3.
                                                } else if (Objects.equals(ActionDegree,3)){
                                                    for (AgentPointCalculator apc : pointsAndRates) {
                                                        if (apc.getName() != sender){
                                                                apc.setPoints(apc.getPoints() + addedBonus);
                                                                apc.setThreatRate(apc.getThreatRate() * threatChange);
                                                                logger.info(apc.getName()+" now has "+apc.getPoints() +", "+ apc.getThreatRate()+ " because of "+sender+"'s decisions.");
                                                        }
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
                        logger.info("TURN "+ turnsTaken + " HAS ENDED.");

                        //STOP MAIN TICKER BEHAVIOUR ENDING THE ENGINE'S TASKS
                        if (Objects.equals(turnsTaken, Turns)){
                            //FINAL LOGGING INFO- AGENT SCORES
                            logger.info("\n------SIMULATION ENDED------");
                            logger.info("\n----------------------------");
                            //--------LOG AGENT SCORES----------
                            logger.info("\n------AGENT SCORES:------");
                            for (AgentPointCalculator ag: pointsAndRates){
                                logger.info(String.format("%s ==> Points: %d, Rate: %s", ag.getName(), ag.getPoints(), ag.getThreatRate()));
                            }
//                            done();
//                            stop();
                            myAgent.doSuspend();
                        }
                        //INCREMENT TURNS
                        turnsTaken++;
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

    public void createAgents(Groups groups) {
        Random randRole = new Random();
        Object args2[] = new Object[3];
        args2[1] = logger;
        args2[2] = groups;
        
        for (int agentcounter = 0; agentcounter < Agents; agentcounter++) {
            //getting random roles and passing to agents
            ArrayList<Role> allRoles = groups.getAllRoles();
            Role role = allRoles.get(randRole.nextInt(allRoles.size()));
            for (AgentPointCalculator ag: pointsAndRates){
                if (Objects.equals(ag.getName(),"agent-"+agentcounter)){
                    ag.setRole(role);
                }
            }
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
