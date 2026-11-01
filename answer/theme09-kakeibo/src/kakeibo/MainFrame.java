package kakeibo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 家計簿の画面を作るクラス。
 * 入力のチェックと表示を担当し、記録の管理と集計は Kakeibo に任せる。
 */
public class MainFrame extends JFrame {

    private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);
    private static final String[] COLUMNS = {"日付", "区分", "費目", "金額", "メモ"};

    private final Kakeibo kakeibo = new Kakeibo(Path.of("kakeibo.csv"));

    // 入力欄
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 9); // F9-02
    private final JComboBox<EntryType> typeBox = new JComboBox<>(EntryType.values());
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JTextField amountField = new JTextField(6);
    private final JTextField memoField = new JTextField(10);

    // 一覧表（セルを直接編集できないようにする）
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    /** 一覧表に今表示している記録（表の行番号と対応している） */
    private List<Entry> shownEntries = new ArrayList<>();

    // 集計
    private final JComboBox<YearMonth> monthBox = new JComboBox<>();
    private final JLabel totalLabel = new JLabel();
    private final JLabel categoryLabel = new JLabel();

    public MainFrame() {
        setTitle("家計簿");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);

        try {
            kakeibo.load();
        } catch (IOException | RuntimeException e) {
            showError("kakeibo.csv を読み込めませんでした。");
        }
        refreshMonths(YearMonth.now());
    }

    /** 上部：入力欄と登録ボタン */
    private JPanel createInputPanel() {
        typeBox.setSelectedItem(EntryType.EXPENSE);
        updateCategories();
        typeBox.addActionListener(e -> updateCategories());   // 区分を変えたら費目の候補を変える

        JButton addButton = new JButton("登録");
        addButton.addActionListener(e -> onAdd());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panel.add(new JLabel("日付"));
        panel.add(dateField);
        panel.add(typeBox);
        panel.add(categoryBox);
        panel.add(new JLabel("金額"));
        panel.add(amountField);
        panel.add(new JLabel("メモ"));
        panel.add(memoField);
        panel.add(addButton);
        setFontAll(panel);
        return panel;
    }

    /** 中央：一覧表 */
    private JScrollPane createTablePanel() {
        table.setFont(FONT);
        table.setRowHeight(26);
        table.getTableHeader().setFont(FONT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10), scrollPane.getBorder()));
        return scrollPane;
    }

    /** 下部：月の選択・集計・削除ボタン */
    private JPanel createSummaryPanel() {
        monthBox.addActionListener(e -> refreshTable());
        JButton deleteButton = new JButton("削除");
        deleteButton.addActionListener(e -> onDelete());

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row1.add(new JLabel("表示する月"));
        row1.add(monthBox);
        row1.add(totalLabel);
        row1.add(deleteButton);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        row2.add(categoryLabel);
        setFontAll(row1);
        setFontAll(row2);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        panel.add(row1);
        panel.add(row2);
        return panel;
    }

    /** 登録ボタンが押されたときの処理。 */
    private void onAdd() {
        // F9-04：日付のチェック（「2026-11-01」の形式）
        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            showError("日付は「2026-11-01」の形式で入力してください。");
            return;
        }
        // F9-04：金額のチェック（正の整数）
        int amount;
        try {
            amount = Integer.parseInt(amountField.getText().trim());
        } catch (NumberFormatException e) {
            showError("金額は数字で入力してください。");
            return;
        }
        if (amount <= 0) {
            showError("金額は1以上の数を入力してください。");
            return;
        }

        EntryType type = (EntryType) typeBox.getSelectedItem();
        String category = (String) categoryBox.getSelectedItem();
        try {
            kakeibo.add(date, type, category, amount, memoField.getText().trim());
        } catch (IOException e) {
            showError("保存に失敗しました。");
        }
        amountField.setText("");
        memoField.setText("");
        refreshMonths(YearMonth.from(date));      // 登録した記録の月を表示する
    }

    /** 削除ボタンが押されたときの処理。 */
    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("削除する記録を一覧から選んでください。");
            return;
        }
        Entry entry = shownEntries.get(row);
        int answer = JOptionPane.showConfirmDialog(this,
                entry.date() + " " + entry.category() + " " + entry.amount() + "円 を削除しますか？",
                "削除の確認", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            kakeibo.remove(entry);
        } catch (IOException e) {
            showError("保存に失敗しました。");
        }
        refreshTable();
    }

    /** 区分に合わせて、費目のプルダウンの中身を入れ替える。 */
    private void updateCategories() {
        categoryBox.removeAllItems();
        EntryType type = (EntryType) typeBox.getSelectedItem();
        for (String category : type.getCategories()) {
            categoryBox.addItem(category);
        }
    }

    /** 月のプルダウンを作り直し、指定した月を選ぶ。 */
    private void refreshMonths(YearMonth select) {
        monthBox.removeAllItems();        // ここで refreshTable が何度か呼ばれるが問題ない
        for (YearMonth month : kakeibo.months()) {
            monthBox.addItem(month);
        }
        monthBox.setSelectedItem(select);
        refreshTable();
    }

    /** F9-05〜F9-07：選ばれている月の一覧表と集計を表示する。 */
    private void refreshTable() {
        YearMonth month = (YearMonth) monthBox.getSelectedItem();
        if (month == null) {
            return;
        }
        shownEntries = kakeibo.entriesOf(month);
        tableModel.setRowCount(0);
        for (Entry e : shownEntries) {
            tableModel.addRow(new Object[] {e.date(), e.type(), e.category(), e.amount() + "円", e.memo()});
        }

        int income = kakeibo.totalOf(month, EntryType.INCOME);
        int expense = kakeibo.totalOf(month, EntryType.EXPENSE);
        totalLabel.setText("収入 " + income + "円　支出 " + expense + "円　差額 " + (income - expense) + "円");

        StringBuilder sb = new StringBuilder("費目別の支出：");
        for (Map.Entry<String, Integer> item : kakeibo.expenseByCategory(month).entrySet()) {
            sb.append(item.getKey()).append(" ").append(item.getValue()).append("円　");
        }
        categoryLabel.setText(sb.toString());
    }

    /** パネルの中の部品の文字をすべて同じ大きさにする。 */
    private void setFontAll(JPanel panel) {
        for (Component component : panel.getComponents()) {
            component.setFont(FONT);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "お知らせ", JOptionPane.WARNING_MESSAGE);
    }
}
