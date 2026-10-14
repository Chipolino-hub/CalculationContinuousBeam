package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private TextField numSpansField;
    @FXML private VBox spansVBox;
    @FXML private LineChart<Number, Number> momentChart;
    @FXML private TableView<SupportData> supportsTable;
    @FXML private TableColumn<SupportData, Integer> supportNumColumn;
    @FXML private TableColumn<SupportData, Double> momentColumn;
    @FXML private TableColumn<SupportData, Double> reactionColumn;
    @FXML private TableColumn<SupportData, Double> distanceColumn;
    @FXML private TabPane tabPane;
    @FXML private ScrollPane inputScrollPane;
    @FXML private TextArea instructionText;
    @FXML private TextArea reportArea;

    private List<BeamSpan> spans = new ArrayList<>();
    private CalculationService calculationService = new CalculationService();

    @FXML
    private void initialize() {
        instructionText.setText(
                "ИНСТРУКЦИЯ И ПОЯСНЕНИЯ\n" +
                "\n" +
                "1. Ввод данных:\n" +
                "   - Количество пролётов – целое положительное число.\n" +
                "   - Длина пролёта (м) – положительное число.\n"   +
                "   - Момент инерции (м⁴) – положительное число. Эта характеристика показывает, насколько сечение балки сопротивляется изгибу. Для прямоугольного сечения шириной b и высотой h: I = b*h³/12. Если точное значение неизвестно, можно оставить 1.0 – это означает, что все пролёты считаются одинаковой жёсткости, результат будет пропорционален реальному.\n\n" +
                "2. Типы нагрузок:\n" +
                "   - Равномерная (q) – нагрузка, распределённая по всей длине пролёта (например, собственный вес), единица измерения кН/м.\n"   +
                "   - Сосредоточенная сила (F) – сила, приложенная в конкретной точке (например, колонна), единица кН.\n"     +
                "   - Момент (M) – сосредоточенный изгибающий момент, приложенный в точке (например, защемление), единица кН·м.\n\n" +
                "3. Результаты:\n" +
                "   - Опорный момент – внутренний изгибающий момент, возникающий в опоре. На крайних опорах он равен 0 (шарниры).\n" +
                "   - Опорная реакция – вертикальная сила, с которой опора действует на балку.\n" +
                "   - Расстояние от начала – координата опоры вдоль балки (сумма длин предыдущих пролётов).\n" +
                "   - Эпюра моментов – график изменения изгибающего момента вдоль балки. Положительные значения соответствуют растяжению нижних волокон (балка прогибается вниз), отрицательные – растяжению верхних волокон (балка выгибается вверх).\n\n" +
                "4. Примечание: все поля должны быть заполнены корректными числами. Некорректные поля подсвечиваются красным.");

        supportNumColumn.setCellValueFactory(cellData -> cellData.getValue().numProperty().asObject());
        momentColumn.setCellValueFactory(cellData -> cellData.getValue().momentProperty().asObject());
        reactionColumn.setCellValueFactory(cellData -> cellData.getValue().reactionProperty().asObject());
        distanceColumn.setCellValueFactory(cellData -> cellData.getValue().distanceProperty().asObject());

        momentChart.setCreateSymbols(false);
        ((NumberAxis) momentChart.getXAxis()).setLabel("Длина балки (м)");
        ((NumberAxis) momentChart.getYAxis()).setLabel("Изгибающий момент (кН·м)");

        reportArea.setEditable(false);
        reportArea.setWrapText(true);
    }

    @FXML
    private void applyNumSpans() {
        if (!validateNumSpans()) return;
        int num = Integer.parseInt(numSpansField.getText());
        spansVBox.getChildren().clear();
        spans.clear();
        for (int i = 0; i < num; i++) {
            BeamSpan span = new BeamSpan();
            spans.add(span);
            spansVBox.getChildren().add(createSpanInputPane(i + 1, span));
        }
    }

    private boolean validateNumSpans() {
        try {
            int n = Integer.parseInt(numSpansField.getText());
            if (n <= 0) {
                showAlert("Количество пролётов должно быть положительным целым.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showAlert("Неверный формат количества пролётов.");
            return false;
        }
    }

    private GridPane createSpanInputPane(int num, BeamSpan span) {
        GridPane pane = new GridPane();
        pane.getStyleClass().add("span-pane");
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(5);

        Label title = new Label("Пролёт " + num + ":");
        GridPane.setConstraints(title, 0, 0, 2, 1);

        Label lLabel = new Label("Длина (м):");
        TextField lField = new TextField();
        lField.setTooltip(new Tooltip("Положительное число – длина пролёта в метрах"));
        span.setLengthField(lField);
        GridPane.setConstraints(lLabel, 0, 1);
        GridPane.setConstraints(lField, 1, 1);

        Label iLabel = new Label("Момент инерции (м⁴):");
        TextField iField = new TextField("1.0");
        iField.setTooltip(new Tooltip("Положительное число. Если точное значение неизвестно, оставьте 1.0 (единичная жёсткость)."));
        span.setInertiaField(iField);
        GridPane.setConstraints(iLabel, 0, 2);
        GridPane.setConstraints(iField, 1, 2);

        Label loadsTitle = new Label("Нагрузки:");
        GridPane.setConstraints(loadsTitle, 0, 3, 2, 1);

        VBox loadsBox = new VBox(5);
        span.setLoadsVBox(loadsBox);
        GridPane.setConstraints(loadsBox, 0, 4, 2, 1);

        Button addBtn = new Button("Добавить нагрузку");
        addBtn.setOnAction(e -> addLoadInput(span));
        GridPane.setConstraints(addBtn, 0, 5, 2, 1);

        pane.getChildren().addAll(title, lLabel, lField, iLabel, iField, loadsTitle, loadsBox, addBtn);

        lField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateLengthField(lField, num);
        });
        iField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateInertiaField(iField, num);
        });

        return pane;
    }

    private void validateLengthField(TextField field, int spanNum) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ": длина не может быть пустой.");
        } else {
            try {
                double val = Double.parseDouble(text);
                if (val <= 0) {
                    field.setStyle("-fx-border-color: red;");
                    showAlert("Пролёт " + spanNum + ": длина должна быть положительной.");
                } else {
                    field.setStyle("");
                }
            } catch (NumberFormatException e) {
                field.setStyle("-fx-border-color: red;");
                showAlert("Пролёт " + spanNum + ": длина должна быть числом.");
            }
        }
    }

    private void validateInertiaField(TextField field, int spanNum) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ": момент инерции не может быть пустым.");
        } else {
            try {
                double val = Double.parseDouble(text);
                if (val <= 0) {
                    field.setStyle("-fx-border-color: red;");
                    showAlert("Пролёт " + spanNum + ": момент инерции должен быть положительным.");
                } else {
                    field.setStyle("");
                }
            } catch (NumberFormatException e) {
                field.setStyle("-fx-border-color: red;");
                showAlert("Пролёт " + spanNum + ": момент инерции должен быть числом.");
            }
        }
    }

    private void addLoadInput(BeamSpan span) {
        GridPane loadPane = new GridPane();
        loadPane.setHgap(10);
        loadPane.setVgap(5);

        ChoiceBox<String> type = new ChoiceBox<>(FXCollections.observableArrayList(
                "Равномерная (q)", "Сосредоточенная сила (F)", "Момент (M)"));
        type.getSelectionModel().selectFirst();
        type.setTooltip(new Tooltip("Выберите тип нагрузки"));
        GridPane.setConstraints(type, 0, 0);

        TextField value = new TextField();
        value.setPromptText("Значение");
        value.setTooltip(new Tooltip("Число: для равномерной – кН/м, для силы – кН, для момента – кН·м"));
        GridPane.setConstraints(value, 1, 0);

        TextField pos = new TextField();
        pos.setPromptText("Позиция (м)");
        pos.setTooltip(new Tooltip("Число от 0 до длины пролёта (только для силы и момента)"));
        pos.setVisible(false);
        GridPane.setConstraints(pos, 2, 0);

        type.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) -> {
            boolean visible = !nv.equals("Равномерная (q)");
            pos.setVisible(visible);
            if (visible) {
                value.setPromptText("Значение (кН или кН·м)");
            } else {
                value.setPromptText("Значение (кН/м)");
            }
        });

        Button remove = new Button("Удалить");
        remove.setTooltip(new Tooltip("Удалить эту нагрузку"));
        remove.setOnAction(e -> {
            span.getLoadsVBox().getChildren().remove(loadPane);
            span.getLoadPanes().remove(loadPane);
        });
        GridPane.setConstraints(remove, 3, 0);

        loadPane.getChildren().addAll(type, value, pos, remove);
        span.getLoadsVBox().getChildren().add(loadPane);
        span.getLoadPanes().add(loadPane);

        int spanIndex = spans.indexOf(span) + 1;
        int loadIndex = span.getLoadPanes().size();

        value.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateValueField(value, spanIndex, loadIndex);
        });
        pos.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && pos.isVisible()) validatePositionField(pos, spanIndex, loadIndex, span);
        });
    }

    private void validateValueField(TextField field, int spanNum, int loadNum) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ", нагрузка " + loadNum + ": значение не может быть пустым.");
        } else {
            try {
                Double.parseDouble(text);
                field.setStyle("");
            } catch (NumberFormatException e) {
                field.setStyle("-fx-border-color: red;");
                showAlert("Пролёт " + spanNum + ", нагрузка " + loadNum + ": значение должно быть числом.");
            }
        }
    }

    private void validatePositionField(TextField field, int spanNum, int loadNum, BeamSpan span) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ", нагрузка " + loadNum + ": позиция не может быть пустой.");
            return;
        }
        double pos;
        try {
            pos = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ", нагрузка " + loadNum + ": позиция должна быть числом.");
            return;
        }
        String lenText = span.getLengthField().getText();
        if (lenText.isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ": сначала введите длину пролёта.");
            return;
        }
        double length;
        try {
            length = Double.parseDouble(lenText);
        } catch (NumberFormatException e) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ": длина должна быть числом.");
            return;
        }
        if (pos < 0 || pos > length) {
            field.setStyle("-fx-border-color: red;");
            showAlert("Пролёт " + spanNum + ", нагрузка " + loadNum + ": позиция должна быть в пределах [0, " + length + "].");
        } else {
            field.setStyle("");
        }
    }

    @FXML
    private void calculate() {
        if (!validateInput()) return;
        parseSpans();
        List<Double> moments = calculationService.calculateMoments(spans);
        List<Double> reactions = calculationService.calculateReactions(spans, moments);
        List<Double> distances = calculateSupportDistances();
        displayTable(moments, reactions, distances);
        displayMomentDiagram(moments, distances);
        generateReport(moments, reactions);
        tabPane.getSelectionModel().select(1); 
    }

    private List<Double> calculateSupportDistances() {
        List<Double> distances = new ArrayList<>();
        double x = 0;
        distances.add(x);
        for (BeamSpan span : spans) {
            x += span.getLength();
            distances.add(x);
        }
        return distances;
    }

    private boolean validateInput() {
        if (spans.isEmpty()) {
            showAlert("Сначала создайте пролёты, нажав 'Применить'.");
            return false;
        }
        for (int i = 0; i < spans.size(); i++) {
            BeamSpan s = spans.get(i);
            TextField lf = s.getLengthField();
            TextField inf = s.getInertiaField();

            if (lf.getText().trim().isEmpty()) {
                showAlert("Пролёт " + (i+1) + ": длина не может быть пустой.");
                lf.requestFocus();
                return false;
            }
            if (inf.getText().trim().isEmpty()) {
                showAlert("Пролёт " + (i+1) + ": момент инерции не может быть пустым.");
                inf.requestFocus();
                return false;
            }

            double l, iner;
            try {
                l = Double.parseDouble(lf.getText());
            } catch (NumberFormatException e) {
                showAlert("Пролёт " + (i+1) + ": длина должна быть числом.");
                lf.requestFocus();
                return false;
            }
            try {
                iner = Double.parseDouble(inf.getText());
            } catch (NumberFormatException e) {
                showAlert("Пролёт " + (i+1) + ": момент инерции должен быть числом.");
                inf.requestFocus();
                return false;
            }
            if (l <= 0) {
                showAlert("Пролёт " + (i+1) + ": длина должна быть положительной.");
                lf.requestFocus();
                return false;
            }
            if (iner <= 0) {
                showAlert("Пролёт " + (i+1) + ": момент инерции должен быть положительным.");
                inf.requestFocus();
                return false;
            }

            List<GridPane> loadPanes = s.getLoadPanes();
            for (int j = 0; j < loadPanes.size(); j++) {
                GridPane p = loadPanes.get(j);
                ChoiceBox<String> typeBox = (ChoiceBox<String>) p.getChildren().get(0);
                TextField valField = (TextField) p.getChildren().get(1);
                TextField posField = (TextField) p.getChildren().get(2);

                if (valField.getText().trim().isEmpty()) {
                    showAlert("Пролёт " + (i+1) + ", нагрузка " + (j+1) + ": значение не может быть пустым.");
                    valField.requestFocus();
                    return false;
                }
                try {
                    Double.parseDouble(valField.getText());
                } catch (NumberFormatException e) {
                    showAlert("Пролёт " + (i+1) + ", нагрузка " + (j+1) + ": значение должно быть числом.");
                    valField.requestFocus();
                    return false;
                }

                if (!typeBox.getValue().equals("Равномерная (q)")) {
                    if (posField.getText().trim().isEmpty()) {
                        showAlert("Пролёт " + (i+1) + ", нагрузка " + (j+1) + ": позиция не может быть пустой.");
                        posField.requestFocus();
                        return false;
                    }
                    double pos;
                    try {
                        pos = Double.parseDouble(posField.getText());
                    } catch (NumberFormatException e) {
                        showAlert("Пролёт " + (i+1) + ", нагрузка " + (j+1) + ": позиция должна быть числом.");
                        posField.requestFocus();
                        return false;
                    }
                    if (pos < 0 || pos > l) {
                        showAlert("Пролёт " + (i+1) + ", нагрузка " + (j+1) + ": позиция должна быть в пределах [0, " + l + "].");
                        posField.requestFocus();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void parseSpans() {
        for (BeamSpan s : spans) {
            s.setLength(Double.parseDouble(s.getLengthField().getText()));
            s.setInertia(Double.parseDouble(s.getInertiaField().getText()));
            List<Load> loads = new ArrayList<>();
            for (GridPane p : s.getLoadPanes()) {
                ChoiceBox<String> t = (ChoiceBox<String>) p.getChildren().get(0);
                TextField v = (TextField) p.getChildren().get(1);
                TextField pos = (TextField) p.getChildren().get(2);
                double val = Double.parseDouble(v.getText());
                double position = 0;
                Load.LoadType type;
                String selected = t.getValue();
                if (selected.equals("Равномерная (q)")) {
                    type = Load.LoadType.UNIFORM;
                } else if (selected.equals("Сосредоточенная сила (F)")) {
                    type = Load.LoadType.FORCE;
                    position = Double.parseDouble(pos.getText());
                } else {
                    type = Load.LoadType.MOMENT;
                    position = Double.parseDouble(pos.getText());
                }
                loads.add(new Load(type, val, position));
            }
            s.setLoads(loads);
        }
    }

    private void displayTable(List<Double> moments, List<Double> reactions, List<Double> distances) {
        ObservableList<SupportData> data = FXCollections.observableArrayList();
        for (int i = 0; i < moments.size(); i++) {
            data.add(new SupportData(i, moments.get(i), reactions.get(i), distances.get(i)));
        }
        supportsTable.setItems(data);
    }

    private void displayMomentDiagram(List<Double> moments, List<Double> distances) {
        momentChart.getData().clear();
        
        // Основная серия – эпюра моментов
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Эпюра моментов");
        double x = 0;
        int pointsPerSpan = 50;
        for (int i = 0; i < spans.size(); i++) {
            BeamSpan s = spans.get(i);
            double ml = (i == 0) ? 0 : moments.get(i);
            double mr = (i == spans.size() - 1) ? 0 : moments.get(i + 1);
            double dx = s.getLength() / pointsPerSpan;
            for (int j = 0; j <= pointsPerSpan; j++) {
                double xl = j * dx;
                double mfree = calculationService.calculateFreeMoment(s, xl);
                double m = ml * (1 - xl / s.getLength()) + mr * (xl / s.getLength()) + mfree;
                series.getData().add(new XYChart.Data<>(x + xl, m));
            }
            x += s.getLength();
        }
        momentChart.getData().add(series);
    }

    private void generateReport(List<Double> moments, List<Double> reactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("РЕЗУЛЬТАТЫ РАСЧЁТА\n\n");
        sb.append("Опорные моменты (кН·м):\n");
        for (int i = 0; i < moments.size(); i++) {
            sb.append(String.format("Опора %d: %.3f\n", i, moments.get(i)));
        }
        sb.append("\nОпорные реакции (кН):\n");
        for (int i = 0; i < reactions.size(); i++) {
            sb.append(String.format("Опора %d: %.3f\n", i, reactions.get(i)));
        }
        reportArea.setText(sb.toString());
    }

    @FXML
    private void showAbout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/about.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tabPane.getScene().getWindow());
            stage.setTitle("О программе");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}