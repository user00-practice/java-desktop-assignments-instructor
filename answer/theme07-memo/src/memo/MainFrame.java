package memo;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * 簡易メモ帳の画面を作るクラス。
 * メニューの操作を受け付け、ファイルの読み書きは MemoFile に任せる。
 */
public class MainFrame extends JFrame {

    private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

    private final MemoFile memoFile = new MemoFile();
    private final JTextArea textArea = new JTextArea();
    private final JLabel statusLabel = new JLabel();
    private final JFileChooser chooser = new JFileChooser();

    public MainFrame() {
        // F7-07：×ボタンですぐに終了せず、windowClosing で確認してから終了する
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
        setSize(640, 480);
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());

        // 中央：テキスト領域（JScrollPane に入れてスクロールできるようにする）
        textArea.setFont(FONT);
        textArea.setLineWrap(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // 下部：ステータスバー
        statusLabel.setFont(FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(statusLabel, BorderLayout.SOUTH);

        // F7-05・F7-06：入力されるたびに、未保存の印と文字数・行数を更新する
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // 文字の装飾が変わったときに呼ばれる（JTextArea では使わない）
            }
        });

        updateTitle();
        updateStatus();
    }

    /** メニューバーを作る。 */
    private JMenuBar createMenuBar() {
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.setFont(FONT);
        addMenuItem(fileMenu, "新規", this::onNew);
        addMenuItem(fileMenu, "開く...", this::onOpen);
        addMenuItem(fileMenu, "保存", this::save);
        addMenuItem(fileMenu, "名前を付けて保存...", this::saveAs);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "終了", this::onExit);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        return menuBar;
    }

    private void addMenuItem(JMenu menu, String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(FONT);
        item.addActionListener(e -> action.run());
        menu.add(item);
    }

    /** F7-01：新規 */
    private void onNew() {
        if (!confirmDiscard()) {
            return;
        }
        textArea.setText("");
        memoFile.newDocument();     // setText で「変更あり」になるので、そのあとで状態を戻す
        updateTitle();
    }

    /** F7-02：開く */
    private void onOpen() {
        if (!confirmDiscard()) {
            return;
        }
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        try {
            String text = memoFile.open(file.toPath());
            textArea.setText(text);
            memoFile.setModified(false);
            textArea.setCaretPosition(0);
        } catch (IOException e) {
            showError("ファイルを開けませんでした。\n" + file);   // F7-08
        }
        updateTitle();
    }

    /**
     * F7-03：保存。新規の文書なら「名前を付けて保存」と同じ動作にする。
     * 保存できたら true、キャンセルや失敗なら false を返す。
     */
    private boolean save() {
        if (memoFile.isNew()) {
            return saveAs();
        }
        try {
            memoFile.save(textArea.getText());
            updateTitle();
            return true;
        } catch (IOException e) {
            showError("保存に失敗しました。");                     // F7-08
            return false;
        }
    }

    /** F7-04：名前を付けて保存。保存できたら true を返す。 */
    private boolean saveAs() {
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File file = chooser.getSelectedFile();
        try {
            memoFile.saveAs(file.toPath(), textArea.getText());
            updateTitle();
            return true;
        } catch (IOException e) {
            showError("保存に失敗しました。\n" + file);           // F7-08
            return false;
        }
    }

    /** 終了（メニューと×ボタンの両方から呼ばれる） */
    private void onExit() {
        if (confirmDiscard()) {
            System.exit(0);
        }
    }

    /**
     * F7-07：未保存の変更があれば保存するか確認する。
     * 続けてよいなら true、キャンセルされたら false を返す。
     */
    private boolean confirmDiscard() {
        if (!memoFile.isModified()) {
            return true;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                memoFile.getFileName() + " への変更を保存しますか？",
                "確認", JOptionPane.YES_NO_CANCEL_OPTION);
        return switch (answer) {
            case JOptionPane.YES_OPTION -> save();
            case JOptionPane.NO_OPTION -> true;
            default -> false;      // キャンセル、またはダイアログを閉じた
        };
    }

    /** 文章が変更されたときの処理。 */
    private void onTextChanged() {
        memoFile.setModified(true);
        updateTitle();
        updateStatus();
    }

    /** F7-05：タイトルバーを「ファイル名 - 簡易メモ帳」にする。未保存なら先頭に「*」。 */
    private void updateTitle() {
        String mark = memoFile.isModified() ? "*" : "";
        setTitle(mark + memoFile.getFileName() + " - 簡易メモ帳");
    }

    /** F7-06：文字数（改行を除く）と行数を表示する。 */
    private void updateStatus() {
        String text = textArea.getText();
        int chars = text.replace("\n", "").length();
        statusLabel.setText(chars + " 文字　" + textArea.getLineCount() + " 行");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
