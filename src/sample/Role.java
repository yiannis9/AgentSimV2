package sample;

public class Role {
    private String positionName;
    private String department;
    private Integer ActionDegree;
    private Integer limit;

    public Role(String positionName,String department,Integer ActionDegree,Integer limit){
        this.positionName=positionName;
        this.department=department;
        this.ActionDegree=ActionDegree;
        this.limit=limit;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getActionDegree() {
        return ActionDegree;
    }

    public Integer getLimit() {
        return limit;
    }
}
