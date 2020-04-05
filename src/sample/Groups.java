package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Groups {
    private ArrayList<Role> allRoles;
    private String[] Attitudes;

    public Groups(String[] Attitudes, ArrayList<Role> allRoles){
        this.Attitudes=Attitudes;
        this.allRoles = allRoles;

    }

    public ArrayList<Role> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(ArrayList<Role> allRoles) {
        this.allRoles = allRoles;
    }

    public String[] getAttitudes() {
        return Attitudes;
    }

    public void setAttitudes(String[] Attitudes) {
        this.Attitudes = Attitudes;
    }
}
