package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

public class AgentLister extends Agent {
    public AMSAgentDescription[] agentsList;

    protected void setup () {
            try {
                final SearchConstraints c = new SearchConstraints();
                c.setMaxResults((long) -1);
                agentsList = AMSService.search(this, new AMSAgentDescription(), c);
            } catch (Exception e) {
                e.printStackTrace();
            }

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                for (AMSAgentDescription amsAgentDescription : agentsList) {
                    AID agentID = amsAgentDescription.getName();
                    System.out.println(agentID.getLocalName());
                }
            }
        });
    }
}
