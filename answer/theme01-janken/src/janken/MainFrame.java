package janken;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * じゃんけんゲームの画面を作るクラス。
 * 画面の部品の配置と、ボタンが押されたときに JankenGame を呼び出す処理を担当する。
 */
public class MainFrame extends JFrame {

    private static final Font BIG_FONT = new Font(Font.DIALOG, Font.BOLD, 24);
    private static final Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

    private final JankenGame game = new JankenGame();

    private final JLabel handLabel = new JLabel("手を選んでください", SwingConstants.CENTER);
    private final JLabel resultLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);

    public MainFrame() {
        setTitle("じゃんけんゲーム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);

        // 上部：手と結果の表示
        handLabel.setFont(NORMAL_FONT);
        resultLabel.setFont(BIG_FONT);
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(handLabel);
        topPanel.add(resultLabel);

        // 中央：グー・チョキ・パーのボタン（enum の値の数だけループで作る）
        JPanel handPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        handPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        for (Hand hand : Hand.values()) {
            JButton button = new JButton(hand.getLabel());
            button.setFont(BIG_FONT);
            button.addActionListener(e -> onHandSelected(hand));
            handPanel.add(button);
        }

        // 下部：通算成績とリセットボタン
        scoreLabel.setFont(NORMAL_FONT);
        JButton resetButton = new JButton("リセット");
        resetButton.setFont(NORMAL_FONT);
        resetButton.addActionListener(e -> onReset());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomPanel.add(scoreLabel, BorderLayout.CENTER);
        bottomPanel.add(resetButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(handPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateScore();
    }

    /** 手のボタンが押されたときの処理。 */
    private void onHandSelected(Hand hand) {
        Round round = game.play(hand);
        handLabel.setText("あなた：" + round.player().getLabel()
                + "　コンピュータ：" + round.computer().getLabel());
        resultLabel.setText(round.result().getLabel());
        updateScore();
    }

    /** リセットボタンが押されたときの処理。 */
    private void onReset() {
        game.reset();
        handLabel.setText("手を選んでください");
        resultLabel.setText(" ");
        updateScore();
    }

    /** 通算成績の表示を更新する。 */
    private void updateScore() {
        scoreLabel.setText("勝ち " + game.getWinCount()
                + "　負け " + game.getLoseCount()
                + "　あいこ " + game.getDrawCount());
    }
}
