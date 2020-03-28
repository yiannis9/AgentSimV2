package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Groups {
    private ArrayList<Role> allRoles;
    private String[] Attributes;

    public Groups(String[] Attributes, ArrayList<Role> allRoles){
        this.Attributes=Attributes;
        this.allRoles = allRoles;

    }

    public ArrayList<Role> getAllRoles() {
        return allRoles;
    }

    public void setDepartments(ArrayList<Role> allRoles) {
        this.allRoles = allRoles;
    }

    public String[] getAttributes() {
        return Attributes;
    }

    public void setAttributes(String[] attributes) {
        Attributes = attributes;
    }
}
