package hash.table;

import hash.table.StyledDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.OptionalInt;

public class HashTableController {

    private HashTable<String> table;

    private BorderPane root;
    private TextArea logArea;
    private Label diagLabel;
    private TableView<CellRow> tableView;
    private ObservableList<CellRow> cellData;
    private Label statsLabel;

    private VBox inputPane;
    private TextField keyField;
    private TextField dataField;
    private Label inputTitle;
    private Runnable confirmAction;
    private CheckBox showAllCells;

    public HashTableController(Stage stage) {
        int n = askTableSize();
        table = new HashTable<>(n, 0.5);
        buildUI();
        log("Создана таблица размером m = " + table.size());
        refreshTable();
    }

    public BorderPane getRoot() {
        return root;
    }

    // ── Init dialog ────────────────────────────────────────────────────────────

    private int askTableSize() {
        Optional<String> r = StyledDialog.showInputDialog(null, "Hash Table",
            "Ёмкость таблицы (желаемое кол-во элементов):", "20");
        try {
            return Integer.parseInt(r.orElse("20").trim());
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    // ── Build UI ───────────────────────────────────────────────────────────────

    private void buildUI() {
        root = new BorderPane();
        root.setId("app-root");

        HBox header = buildHeader();
        BorderPane.setMargin(header, new Insets(0, 0, 14, 0));
        root.setTop(header);

        VBox center = buildTableArea();
        BorderPane.setMargin(center, new Insets(0, 14, 0, 0));
        root.setCenter(center);

        root.setRight(buildButtonPanel());

        VBox bottom = buildBottomPanel();
        BorderPane.setMargin(bottom, new Insets(14, 0, 0, 0));
        root.setBottom(bottom);
    }

    private HBox buildHeader() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("header-bar");

        Label title = new Label("Hash Table");
        title.getStyleClass().add("app-title");

        Label sub = new Label("Linear Probing");
        sub.getStyleClass().add("subtitle-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statsLabel = new Label("m = — n = — α = —");
        statsLabel.getStyleClass().add("stats-label");

        bar.getChildren().addAll(title, sub, spacer, statsLabel);
        return bar;
    }

    private VBox buildTableArea() {
        VBox area = new VBox(10);
        VBox.setVgrow(area, Priority.ALWAYS);

        tableView = new TableView<>();
        tableView.getStyleClass().add("glass-table");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        cellData = FXCollections.observableArrayList();
        tableView.setItems(cellData);

        TableColumn<CellRow, String> idxCol = col("#", CellRow::indexProperty, 55);
        TableColumn<CellRow, String> statusCol = col("Статус", CellRow::statusProperty, 88);
        TableColumn<CellRow, String> keyCol = col("Key (k)", CellRow::keyProperty, 90);
        TableColumn<CellRow, String> kpCol = col("k'", CellRow::keyPrimeProperty, 80);
        TableColumn<CellRow, String> hCol = col("h(k')", CellRow::hashIndexProperty, 70);
        TableColumn<CellRow, String> dataCol = col("Data", CellRow::dataProperty, 0);

        statusCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("cell-busy", "cell-deleted", "cell-free");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                switch (item) {
                    case "BUSY"    -> getStyleClass().add("cell-busy");
                    case "DELETED" -> getStyleClass().add("cell-deleted");
                    default        -> getStyleClass().add("cell-free");
                }
            }
        });

        tableView.getColumns().addAll(idxCol, statusCol, keyCol, kpCol, hCol, dataCol);
        tableView.setPlaceholder(new Label("Таблица пуста"));

        area.getChildren().add(tableView);
        return area;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<T, String> col(String title,
            java.util.function.Function<T, javafx.beans.value.ObservableValue<String>> fn,
            double prefW) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> fn.apply(d.getValue()));
        if (prefW > 0) c.setPrefWidth(prefW);
        return c;
    }

    private Node buildButtonPanel() {
        VBox panel = new VBox(7);
        panel.getStyleClass().add("button-panel");
        panel.setPrefWidth(235);
        panel.setMinWidth(220);
        panel.setMaxWidth(260);

        inputPane = buildInputPane();
        inputPane.setVisible(false);
        inputPane.setManaged(false);

        showAllCells = new CheckBox("Все ячейки");
        showAllCells.getStyleClass().add("glass-checkbox");
        showAllCells.setMaxWidth(Double.MAX_VALUE);
        showAllCells.setOnAction(e -> refreshTable());

        panel.getChildren().add(inputPane);
        panel.getChildren().add(sectionLabel("ОПЕРАЦИИ"));
        panel.getChildren().add(btn("Вставить",      this::onInsert));
        panel.getChildren().add(btn("Найти",         this::onSearch));
        panel.getChildren().add(btn("Удалить",       this::onDelete));
        panel.getChildren().add(sep());
        panel.getChildren().add(sectionLabel("СОСТОЯНИЕ"));
        panel.getChildren().add(showAllCells);
        panel.getChildren().add(btn("Структура",     this::onPrint));
        panel.getChildren().add(btn("Размер (m)",    this::onSize));
        panel.getChildren().add(btn("Элементов (n)", this::onCount));
        panel.getChildren().add(btn("Пустая?",       this::onEmpty));
        panel.getChildren().add(btn("Нагрузка (α)",  this::onLoad));
        panel.getChildren().add(btn("Итератор",      this::onIterate));
        panel.getChildren().add(btn("Диагностика",   this::onDiag));
        panel.getChildren().add(btn("Очистить",      this::onClear));
        panel.getChildren().add(sep());
        panel.getChildren().add(sectionLabel("ТЕСТЫ"));
        panel.getChildren().add(btn("Тест χ²",       this::onChiSquare));
        panel.getChildren().add(btn("Трудоёмкость",  this::onComplexity));

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("btn-scroll");
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefWidth(235);
        scroll.setMinWidth(220);
        scroll.setMaxWidth(260);
        return scroll;
    }

    private VBox buildInputPane() {
        VBox pane = new VBox(8);
        pane.getStyleClass().add("input-pane");

        inputTitle = new Label();
        inputTitle.getStyleClass().add("input-title");

        keyField = new TextField();
        keyField.setPromptText("Ключ");
        keyField.getStyleClass().add("glass-input");

        dataField = new TextField();
        dataField.setPromptText("Данные");
        dataField.getStyleClass().add("glass-input");

        Button ok = new Button("OK");
        ok.getStyleClass().addAll("action-btn", "action-btn-primary");
        ok.setOnAction(e -> { if (confirmAction != null) confirmAction.run(); });

        Button cancel = new Button("✕");
        cancel.getStyleClass().add("action-btn");
        cancel.setOnAction(e -> hideInput());

        keyField.setOnAction(e -> {
            if (dataField.isManaged()) dataField.requestFocus();
            else if (confirmAction != null) confirmAction.run();
        });
        dataField.setOnAction(e -> { if (confirmAction != null) confirmAction.run(); });

        HBox btns = new HBox(8, ok, cancel);
        pane.getChildren().addAll(inputTitle, keyField, dataField, btns);
        return pane;
    }

    private VBox buildBottomPanel() {
        VBox bottom = new VBox(8);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.getStyleClass().add("log-area");
        logArea.setPrefRowCount(14);
        logArea.setPrefHeight(260);
        logArea.setWrapText(true);

        diagLabel = new Label("Диагностика: —");
        diagLabel.getStyleClass().add("diag-label");

        bottom.getChildren().addAll(logArea, diagLabel);
        return bottom;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-label");
        return l;
    }

    private Button btn(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("action-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    private Separator sep() {
        return new Separator();
    }

    private void showInput(String title, boolean withData, Runnable onConfirm) {
        inputTitle.setText(title);
        keyField.clear();
        dataField.clear();
        dataField.setVisible(withData);
        dataField.setManaged(withData);
        confirmAction = onConfirm;
        inputPane.setVisible(true);
        inputPane.setManaged(true);
        keyField.requestFocus();
    }

    private void hideInput() {
        inputPane.setVisible(false);
        inputPane.setManaged(false);
        confirmAction = null;
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
        logArea.positionCaret(logArea.getText().length());
    }

    private void updateDiag() {
        diagLabel.setText(String.format(
            "k = %d k' = %d h(k') = %d Зондирований = %d",
            table.getLastKeyOriginal(), table.getLastKeyPrime(),
            table.getLastHashIndex(), table.getLastProbeCount()
        ));
    }

    private void updateStats() {
        statsLabel.setText(String.format(
            "m = %d n = %d α = %.3f",
            table.size(), table.count(), table.loadFactor()
        ));
    }

    @SuppressWarnings("unchecked")
    private void refreshTable() {
        cellData.clear();
        Cell<Integer, String>[] cells = (Cell<Integer, String>[]) table.getCells();
        for (int i = 0; i < cells.length; i++) {
            Cell<Integer, String> c = cells[i];
            CellStatus st = c.getStatus();
            if (!showAllCells.isSelected() && st == CellStatus.FREE) continue;
            String keyStr = "—", kp = "—", hi = "—", dataStr = "—";
            if (st == CellStatus.BUSY) {
                int k  = c.getKey();
                int kpv = KeyConverter.toNatural(k);
                keyStr  = String.valueOf(k);
                kp      = String.valueOf(kpv);
                hi      = String.valueOf(kpv % table.size());
                dataStr = String.valueOf(c.getData());
            }
            cellData.add(new CellRow(i, st.name(), keyStr, kp, hi, dataStr));
        }
        updateStats();
    }

    private String capture(Runnable action) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf, true, StandardCharsets.UTF_8);
        PrintStream old = System.out;
        System.setOut(ps);
        try { action.run(); } finally { System.setOut(old); }
        return buf.toString(StandardCharsets.UTF_8).trim();
    }

    // ── Actions ────────────────────────────────────────────────────────────────

    private void onInsert() {
        showInput("Вставить элемент", true, () -> {
            String ks = keyField.getText().trim();
            String ds = dataField.getText().trim();
            if (ks.isEmpty()) { log("Ошибка: введите ключ."); return; }
            if (ds.isEmpty()) { log("Ошибка: введите данные."); return; }
            try {
                int key = Integer.parseInt(ks);
                boolean ok = table.insert(key, ds);
                log(ok ? "✓ Вставлено: key=" + key + "  data=\"" + ds + "\""
                       : "✗ Не вставлено — дубликат или таблица полна.");
                updateDiag();
                refreshTable();
                hideInput();
            } catch (NumberFormatException e) {
                log("Ошибка: ключ должен быть целым числом.");
            }
        });
    }

    private void onSearch() {
        showInput("Найти элемент", false, () -> {
            String ks = keyField.getText().trim();
            if (ks.isEmpty()) return;
            try {
                int key = Integer.parseInt(ks);
                String r = table.search(key);
                log(r != null ? "✓ Найдено: key=" + key + " → \"" + r + "\""
                              : "✗ Не найдено: key=" + key);
                updateDiag();
                refreshTable();
                hideInput();
            } catch (NumberFormatException e) {
                log("Ошибка: ключ должен быть целым числом.");
            }
        });
    }

    private void onDelete() {
        showInput("Удалить элемент", false, () -> {
            String ks = keyField.getText().trim();
            if (ks.isEmpty()) return;
            try {
                int key = Integer.parseInt(ks);
                boolean ok = table.delete(key);
                log(ok ? "✓ Удалено: key=" + key
                       : "✗ Не найдено: key=" + key);
                updateDiag();
                refreshTable();
                hideInput();
            } catch (NumberFormatException e) {
                log("Ошибка: ключ должен быть целым числом.");
            }
        });
    }

    private void onPrint() {
        log("─── Структура таблицы ───\n" + capture(() -> table.print()));
    }

    private void onSize() {
        log("Размер таблицы  m = " + table.size());
    }

    private void onCount() {
        log("Количество элементов  n = " + table.count());
    }

    private void onEmpty() {
        log(table.isEmpty() ? "Таблица пуста." : "Таблица не пуста.");
    }

    private void onLoad() {
        log(String.format("Коэффициент заполнения  α = %.4f", table.loadFactor()));
    }

    private void onIterate() {
        StringBuilder sb = new StringBuilder("─── Обход итератором ───\n");
        int idx = 0;
        HashTableIterator<Integer, String> it  = table.begin();
        HashTableIterator<Integer, String> end = table.end();
        while (it.notEquals(end)) {
            sb.append(String.format("  [%d] key=%d  →  \"%s\"%n", idx, it.getKey(), it.getValue()));
            idx++;
            it = it.next();
        }
        if (idx == 0) sb.append("  (пусто)");
        log(sb.toString().trim());
    }

    private void onDiag() {
        updateDiag();
        log(String.format("Диагностика: k=%d  k'=%d  h(k')=%d  зондирований=%d",
            table.getLastKeyOriginal(), table.getLastKeyPrime(),
            table.getLastHashIndex(), table.getLastProbeCount()));
    }

    private void onClear() {
        if (StyledDialog.showConfirmDialog(root.getScene().getWindow(), "Подтверждение", "Очистить таблицу?")) {
            table.clear();
            log("Таблица очищена.");
            refreshTable();
        }
    }

    private void onChiSquare() {
        log("─── Тест χ² ───\n" + capture(() -> HashTableTests.chiSquareTest(table.size())));
    }

    private void onComplexity() {
        log("─── Тест трудоёмкости ───\n" + capture(HashTableTests::complexityTest));
    }
}
