package sample;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    public String type;
    public String Desc;
    public ArrayList<Choice> choiceList;

    public Rule (String type, String Desc) {
        choiceList = new ArrayList<Choice>();
    }

}
