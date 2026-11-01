package calculator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * 電卓の画面を作るクラス。
 * ボタンが押されたら、そのボタンの文字に応じて Calculator のメソッドを呼び、表示を更新する。
 */
public class MainFrame extends JFrame {

    /** ボタンの並び（4列×5行）。空文字の位置には何も置かない */
    private static final String[] BUTTON_LABELS = {
            "C", "", "", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "", "0", ".", "="
    };

    private final Calculator calculator = new Calculator();
    private final JTextField display = new JTextField("0");

    public MainFrame() {
        setTitle("電卓");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 480);
        setLocationRelativeTo(null);

        // 上部：表示欄（右寄せ・編集不可）
        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 下部：ボタン（配列をループして作る）
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Font buttonFont = new Font(Font.DIALOG, Font.BOLD, 24);
        for (String label : BUTTON_LABELS) {
            if (label.isEmpty()) {
                buttonPanel.add(new JLabel());   // 空いている場所を埋める
                continue;
            }
            JButton button = new JButton(label);
            button.setFont(buttonFont);
            button.addActionListener(e -> onButton(label));
            buttonPanel.add(button);
        }

        add(display, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    /** ボタンが押されたときの処理。 */
    private void onButton(String label) {
        switch (label) {
            case "C" -> calculator.clear();
            case "=" -> calculator.equal();
            case "." -> calculator.inputDot();
            case "+", "−", "×", "÷" -> calculator.inputOperator(Operator.fromSymbol(label));
            default -> calculator.inputDigit(label);
        }
        display.setText(calculator.getDisplay());
    }
}
