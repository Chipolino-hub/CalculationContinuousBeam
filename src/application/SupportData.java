package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SupportData {
    private final IntegerProperty num = new SimpleIntegerProperty();
    private final DoubleProperty moment = new SimpleDoubleProperty();
    private final DoubleProperty reaction = new SimpleDoubleProperty();
    private final DoubleProperty distance = new SimpleDoubleProperty();

    public SupportData(int num, double moment, double reaction, double distance) {
        this.num.set(num);
        this.moment.set(moment);
        this.reaction.set(reaction);
        this.distance.set(distance);
    }

    public IntegerProperty numProperty() { return num; }
    public DoubleProperty momentProperty() { return moment; }
    public DoubleProperty reactionProperty() { return reaction; }
    public DoubleProperty distanceProperty() { return distance; }
}