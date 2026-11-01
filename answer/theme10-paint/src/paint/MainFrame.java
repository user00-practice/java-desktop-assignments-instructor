package paint;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * 簡易お絵描きアプリの画面を作るクラス。
 * ツールバーの操作を DrawingPanel に伝える。
 */
public class MainFrame extends JFrame {

    private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);
    /** F10-04：選べる色の名前と色（同じ順番で並べる） */
    private static final String[] COLOR_NAMES = {"黒", "赤", "青", "緑"};
    private static final Color[] COLORS = {
            Color.BLACK, Color.RED, Color.BLUE, new Color(0x00, 0x99, 0x33)};

    private final DrawingPanel drawingPanel = new DrawingPanel();

    public MainFrame() {
        setTitle("簡易お絵描き");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        add(createToolBar(), BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
    }

    /** 上部のツールバーを作る。 */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // F10-01：図形の種類（ButtonGroup に入れて、どれか1つだけ押された状態にする）
        ButtonGroup group = new ButtonGroup();
        for (ShapeType type : ShapeType.values()) {
            JToggleButton button = new JToggleButton(type.getLabel());
            button.setFont(FONT);
            button.addActionListener(e -> drawingPanel.setShapeType(type));
            group.add(button);
            toolBar.add(button);
            if (type == ShapeType.LINE) {
                button.setSelected(true);
            }
        }
        toolBar.addSeparator();

        // F10-04：色の選択
        JLabel colorLabel = new JLabel("色 ");
        colorLabel.setFont(FONT);
        JComboBox<String> colorBox = new JComboBox<>(COLOR_NAMES);
        colorBox.setFont(FONT);
        colorBox.setMaximumSize(colorBox.getPreferredSize());
        colorBox.addActionListener(e -> drawingPanel.setColor(COLORS[colorBox.getSelectedIndex()]));
        toolBar.add(colorLabel);
        toolBar.add(colorBox);
        toolBar.addSeparator();

        // F10-06・F10-07：元に戻す・全消去
        JButton undoButton = new JButton("元に戻す");
        JButton clearButton = new JButton("全消去");
        undoButton.setFont(FONT);
        clearButton.setFont(FONT);
        undoButton.addActionListener(e -> drawingPanel.undo());
        clearButton.addActionListener(e -> onClear());
        toolBar.add(undoButton);
        toolBar.add(clearButton);
        return toolBar;
    }

    /** 全消去ボタンが押されたときの処理。 */
    private void onClear() {
        if (drawingPanel.isEmpty()) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "すべての図形を消しますか？",
                "全消去の確認", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            drawingPanel.clear();
        }
    }
}
