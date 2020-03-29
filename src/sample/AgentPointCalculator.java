package sample;

public class AgentPointCalculator {
    private Integer points;
    private Double threatRate;
    private String name;
    private Role role;

    public AgentPointCalculator(Integer points, Double threatRate, String name){
        this.threatRate =threatRate;
        this.points = points;
        this.name = name;

    }

    public Double getThreatRate() {
        return threatRate;
    }

    public void setThreatRate(Double threatRate) {
        this.threatRate = threatRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
