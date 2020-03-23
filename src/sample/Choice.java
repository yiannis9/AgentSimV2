package sample;

public class Choice {
    private String CID;
    private String reward;
    private String ThreatChange;
    private String cDesc;

    public Choice (String CID, String reward, String ThreatChange, String cDesc) {
        this.CID=CID;
        this.reward=reward;
        this.ThreatChange=ThreatChange;
        this.cDesc=cDesc;
    }

    public String getcDesc() {
        return cDesc;
    }

    public String getReward() {
        return reward;
    }

    public String getThreatChange() {
        return ThreatChange;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }
}
