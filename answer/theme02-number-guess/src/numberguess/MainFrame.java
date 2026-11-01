package numberguess;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * 数当てゲームの画面を作るクラス。
 * 入力欄の文字を数に変換してチェックし、判定は NumberGuessGame に任せる。
 */
public class MainFrame extends JFrame {

    private static final Font BIG_FONT = new Font(Font.DIALOG, Font.BOLD, 20);
    private static final Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

    private final NumberGuessGame game = new NumberGuessGame();

    private final JTextField inputField = new JTextField(6);
    private final JButton judgeButton = new JButton("判定");
    private final JLabel hintLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel remainingLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel historyLabel = new JLabel("", SwingConstants.CENTER);

    public MainFrame() {
        setTitle("数当てゲーム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 400);
        setLocationRelativeTo(null);

        // 上部：説明文
        JLabel guideLabel = new JLabel(
                NumberGuessGame.MIN + "〜" + NumberGuessGame.MAX + "の数を当ててください",
                SwingConstants.CENTER);
        guideLabel.setFont(BIG_FONT);
        guideLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // 中央：入力欄と判定ボタン
        inputField.setFont(BIG_FONT);
        judgeButton.setFont(BIG_FONT);
        judgeButton.addActionListener(e -> onJudge());
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(inputField);
        inputPanel.add(judgeButton);

        // 下部：ヒント・残り回数・履歴・もう一度ボタン
        hintLabel.setFont(BIG_FONT);
        remainingLabel.setFont(NORMAL_FONT);
        historyLabel.setFont(NORMAL_FONT);
        JButton retryButton = new JButton("もう一度");
        retryButton.setFont(NORMAL_FONT);
        retryButton.addActionListener(e -> onRetry());
        JPanel retryPanel = new JPanel();
        retryPanel.add(retryButton);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.add(hintLabel);
        infoPanel.add(remainingLabel);
        infoPanel.add(historyLabel);
        infoPanel.add(retryPanel);

        add(guideLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        updateInfo();
    }

    /** 判定ボタンが押されたときの処理。 */
    private void onJudge() {
        // F2-05：数字以外が入力されたら、エラーメッセージを出して終わる（回数は減らさない）
        int number;
        try {
            number = Integer.parseInt(inputField.getText().trim());
        } catch (NumberFormatException e) {
            showError("数字を入力してください");
            return;
        }
        if (!game.isInRange(number)) {
            showError(NumberGuessGame.MIN + "〜" + NumberGuessGame.MAX + "の数を入力してください");
            return;
        }

        Hint hint = game.guess(number);
        switch (game.getState()) {
            case CLEARED -> hintLabel.setText("正解！ " + game.getTryCount() + "回で当たりました");
            case FAILED -> hintLabel.setText("残念！ 正解は " + game.getAnswer() + " でした");
            case PLAYING -> hintLabel.setText(hint.getMessage());
        }
        inputField.setText("");
        updateInfo();
    }

    /** もう一度ボタンが押されたときの処理。（F2-07） */
    private void onRetry() {
        game.reset();
        hintLabel.setText(" ");
        inputField.setText("");
        updateInfo();
    }

    /** 残り回数・履歴の表示と、判定ボタンが押せるかどうかを更新する。 */
    private void updateInfo() {
        remainingLabel.setText("残り " + game.getRemainingTries() + " 回");
        historyLabel.setText("履歴：" + game.getHistory());
        // ゲームが終わったら判定ボタンを押せなくする
        boolean playing = game.getState() == GameState.PLAYING;
        judgeButton.setEnabled(playing);
        inputField.setEnabled(playing);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "入力エラー", JOptionPane.WARNING_MESSAGE);
    }
}
