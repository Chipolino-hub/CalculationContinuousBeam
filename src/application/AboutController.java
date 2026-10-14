package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutController {

    @FXML private Label aboutLabel;

    @FXML
    private void initialize() {
        aboutLabel.setText(
                "Программа предназначена для расчёта неразрезных балок методом трёх моментов.\n\n" +
                "Возможности:\n" +
                "- Произвольное количество пролётов;\n" +
                "- Задание длины и момента инерции каждого пролёта;\n" +
                "- Добавление равномерных, сосредоточенных сил и моментов;\n" +
                "- Расчёт опорных моментов и реакций;\n" +
                "- Построение эпюры изгибающих моментов.\n\n" +
                "Версия: 1.0\n" +
                "Год: 2026");
    }

    @FXML
    private void close() {
        ((Stage) aboutLabel.getScene().getWindow()).close();
    }
}