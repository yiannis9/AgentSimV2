package sample;

import java.util.ArrayList;

public class Rule {
    private String type;
    private String desc;
    private ArrayList<Choice> choiceList;

    public Rule (String type, String Desc) {
        this.type = type;
        this.desc = Desc;
        setChoiceList(new ArrayList<Choice>());
    }

    public ArrayList<Choice> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(ArrayList<Choice> choiceList) {
        this.choiceList = choiceList;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
