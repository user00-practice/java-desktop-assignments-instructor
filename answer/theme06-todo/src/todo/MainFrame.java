package todo;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Path;

/**
 * ToDoリストの画面を作るクラス。
 * 一覧のデータは TodoList が持ち、画面の JList は DefaultListModel を通して表示だけを担当する。
 */
public class MainFrame extends JFrame {

    private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

    private final TodoList todoList = new TodoList(Path.of("todo.txt"));

    private final JTextField inputField = new JTextField();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private final JLabel countLabel = new JLabel();

    public MainFrame() {
        setTitle("ToDoリスト");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 480);
        setLocationRelativeTo(null);

        // 上部：入力欄と追加ボタン
        inputField.setFont(FONT);
        JButton addButton = new JButton("追加");
        addButton.setFont(FONT);
        addButton.addActionListener(e -> onAdd());
        JPanel top = new JPanel(new BorderLayout(5, 0));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(inputField, BorderLayout.CENTER);
        top.add(addButton, BorderLayout.EAST);

        // 中央：一覧（1つだけ選べる）
        list.setFont(FONT);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10), scrollPane.getBorder()));

        // 下部：完了・削除ボタンと件数
        JButton doneButton = new JButton("完了にする");
        JButton deleteButton = new JButton("削除");
        doneButton.setFont(FONT);
        deleteButton.setFont(FONT);
        doneButton.addActionListener(e -> onToggle());
        deleteButton.addActionListener(e -> onDelete());
        countLabel.setFont(FONT);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottom.add(doneButton);
        bottom.add(deleteButton);
        bottom.add(countLabel);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // F6-08：起動時に読み込む
        try {
            todoList.load();
        } catch (IOException e) {
            showError("todo.txt を読み込めませんでした。");
        }
        refresh();
    }

    /** 追加ボタンが押されたときの処理。 */
    private void onAdd() {
        String title = inputField.getText().trim();
        // F6-02：空欄や空白だけの場合は追加しない
        if (title.isEmpty()) {
            showError("やることを入力してください。");
            return;
        }
        try {
            todoList.add(title);
        } catch (IOException e) {
            showError("保存に失敗しました。");
        }
        inputField.setText("");
        refresh();
    }

    /** 完了にするボタンが押されたときの処理。 */
    private void onToggle() {
        int index = list.getSelectedIndex();
        if (index < 0) {                                  // F6-05：何も選ばれていない
            showError("完了にする項目を選んでください。");
            return;
        }
        try {
            todoList.toggle(index);
        } catch (IOException e) {
            showError("保存に失敗しました。");
        }
        refresh();
        list.setSelectedIndex(index);                     // 同じ項目を選んだままにする
    }

    /** 削除ボタンが押されたときの処理。 */
    private void onDelete() {
        int index = list.getSelectedIndex();
        if (index < 0) {                                  // F6-05：何も選ばれていない
            showError("削除する項目を選んでください。");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "「" + todoList.getItems().get(index).title() + "」を削除しますか？",
                "削除の確認", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            todoList.remove(index);
        } catch (IOException e) {
            showError("保存に失敗しました。");
        }
        refresh();
    }

    /** 一覧と件数の表示を、TodoList の内容に合わせて作り直す。 */
    private void refresh() {
        listModel.clear();
        for (Todo todo : todoList.getItems()) {
            listModel.addElement(todo.toDisplayText());
        }
        countLabel.setText("未完了：" + todoList.countUndone() + " 件");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "お知らせ", JOptionPane.WARNING_MESSAGE);
    }
}
