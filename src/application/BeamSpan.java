package application;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class BeamSpan {
    private double length;
    private double inertia;
    private List<Load> loads = new ArrayList<>();
    private TextField lengthField;
    private TextField inertiaField;
    private VBox loadsVBox;
    private List<GridPane> loadPanes = new ArrayList<>();

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public double getInertia() { return inertia; }
    public void setInertia(double inertia) { this.inertia = inertia; }
    public List<Load> getLoads() { return loads; }
    public void setLoads(List<Load> loads) { this.loads = loads; }
    public TextField getLengthField() { return lengthField; }
    public void setLengthField(TextField lengthField) { this.lengthField = lengthField; }
    public TextField getInertiaField() { return inertiaField; }
    public void setInertiaField(TextField inertiaField) { this.inertiaField = inertiaField; }
    public VBox getLoadsVBox() { return loadsVBox; }
    public void setLoadsVBox(VBox loadsVBox) { this.loadsVBox = loadsVBox; }
    public List<GridPane> getLoadPanes() { return loadPanes; }
}