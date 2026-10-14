package application;

public class Load {
    public enum LoadType { UNIFORM, FORCE, MOMENT }
    private LoadType type;
    private double value;
    private double position;

    public Load(LoadType type, double value, double position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public LoadType getType() { return type; }
    public double getValue() { return value; }
    public double getPosition() { return position; }
}
