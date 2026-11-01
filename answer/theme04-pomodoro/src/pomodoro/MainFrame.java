package pomodoro;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

/**
 * ポモドーロタイマーの画面を作るクラス。
 * javax.swing.Timer で1秒ごとに PomodoroTimer.tick() を呼び、表示を更新する。
 */
public class MainFrame extends JFrame {

    private static final Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

    private final PomodoroTimer pomodoro = new PomodoroTimer();
    /** 1000ミリ秒（1秒）ごとに onTick を呼ぶタイマー。start() するまで動かない */
    private final Timer swingTimer = new Timer(1000, e -> onTick());

    private final JLabel modeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel countLabel = new JLabel("", SwingConstants.CENTER);

    public MainFrame() {
        setTitle("ポモドーロタイマー");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(null);

        // 上部：モード
        modeLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
        modeLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // 中央：残り時間
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 72));

        // 下部：ボタンと完了回数
        JButton startButton = new JButton("開始");
        JButton pauseButton = new JButton("一時停止");
        JButton resetButton = new JButton("リセット");
        startButton.addActionListener(e -> swingTimer.start());
        pauseButton.addActionListener(e -> swingTimer.stop());
        resetButton.addActionListener(e -> onReset());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        for (JButton button : new JButton[] {startButton, pauseButton, resetButton}) {
            button.setFont(NORMAL_FONT);
            buttonPanel.add(button);
        }
        countLabel.setFont(NORMAL_FONT);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));
        bottomPanel.add(buttonPanel);
        bottomPanel.add(countLabel);

        add(modeLabel, BorderLayout.NORTH);
        add(timeLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateDisplay();
    }

    /** 1秒ごとに呼ばれる処理。 */
    private void onTick() {
        Mode finished = pomodoro.tick();
        updateDisplay();
        if (finished != null) {
            // F4-05：ダイアログを出している間はタイマーを止め、閉じたら次のモードを自動で始める
            swingTimer.stop();
            Toolkit.getDefaultToolkit().beep();
            String message = (finished == Mode.WORK)
                    ? "作業時間が終わりました。休憩しましょう。"
                    : "休憩時間が終わりました。作業を始めましょう。";
            JOptionPane.showMessageDialog(this, message);
            swingTimer.start();
        }
    }

    /** リセットボタンが押されたときの処理。 */
    private void onReset() {
        swingTimer.stop();
        pomodoro.reset();
        updateDisplay();
    }

    /** モード・残り時間・完了回数の表示を更新する。 */
    private void updateDisplay() {
        Mode mode = pomodoro.getMode();
        modeLabel.setText(mode.getLabel());
        timeLabel.setText(pomodoro.getRemainingText());
        // F4-07：モードによって文字色を変える
        Color color = switch (mode) {
            case WORK -> new Color(0xC0, 0x00, 0x00);
            case BREAK -> new Color(0x00, 0x70, 0x30);
        };
        timeLabel.setForeground(color);
        countLabel.setText("完了した作業：" + pomodoro.getCompletedCount() + " 回");
    }
}
