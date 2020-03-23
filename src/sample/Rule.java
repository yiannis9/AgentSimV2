package sample;

import java.util.ArrayList;

public class Rule {
    private String type;
    private String desc;
    public ArrayList<Choice> choiceList;

    public Rule (String type, String Desc) {
        this.type = type;
        this.desc = Desc;
        this.choiceList = new ArrayList<Choice>();
    }

    public ArrayList<Choice> getChoiceList() {
        return choiceList;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
