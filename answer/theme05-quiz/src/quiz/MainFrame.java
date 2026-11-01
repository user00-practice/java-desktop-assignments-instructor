package quiz;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * クイズアプリの画面を作るクラス。
 * 出題画面と結果画面を CardLayout で切り替える。
 */
public class MainFrame extends JFrame {

    private static final Font BIG_FONT = new Font(Font.DIALOG, Font.BOLD, 22);
    private static final Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
    private static final String QUIZ_CARD = "quiz";
    private static final String RESULT_CARD = "result";

    private final QuizManager manager = new QuizManager();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // 出題画面の部品
    private final JLabel numberLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel questionLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton[] choiceButtons = new JButton[4];
    private final JLabel judgeLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton nextButton = new JButton("次へ");

    // 結果画面の部品
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private final JTextArea mistakeArea = new JTextArea();

    public MainFrame() {
        setTitle("クイズアプリ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);

        cards.add(createQuizPanel(), QUIZ_CARD);
        cards.add(createResultPanel(), RESULT_CARD);
        add(cards);

        // F5-01・F5-06：問題ファイルを読み込む。失敗したらメッセージを出して、出題しない
        try {
            manager.load(Path.of("quiz.csv"));
        } catch (IOException e) {
            showLoadError("quiz.csv を読み込めませんでした。ファイルがあるか確認してください。");
            return;
        } catch (IllegalArgumentException e) {
            showLoadError("quiz.csv の形式が正しくありません。\n" + e.getMessage());
            return;
        }
        startQuiz();
    }

    /** 出題画面を作る。 */
    private JPanel createQuizPanel() {
        numberLabel.setFont(NORMAL_FONT);
        questionLabel.setFont(BIG_FONT);
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        top.add(numberLabel);
        top.add(questionLabel);

        JPanel choicePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        choicePanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        for (int i = 0; i < choiceButtons.length; i++) {
            JButton button = new JButton();
            button.setFont(NORMAL_FONT);
            button.addActionListener(e -> onChoose(button.getText()));
            choiceButtons[i] = button;
            choicePanel.add(button);
        }

        judgeLabel.setFont(BIG_FONT);
        nextButton.setFont(NORMAL_FONT);
        nextButton.addActionListener(e -> onNext());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        bottom.add(judgeLabel, BorderLayout.CENTER);
        bottom.add(nextButton, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(choicePanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    /** 結果画面を作る。 */
    private JPanel createResultPanel() {
        scoreLabel.setFont(BIG_FONT);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        mistakeArea.setFont(NORMAL_FONT);
        mistakeArea.setEditable(false);

        JButton retryButton = new JButton("もう一度挑戦する");
        retryButton.setFont(NORMAL_FONT);
        retryButton.addActionListener(e -> startQuiz());
        JPanel bottom = new JPanel();
        bottom.add(retryButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        panel.add(scoreLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(mistakeArea), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    /** 最初から出題を始める。 */
    private void startQuiz() {
        manager.start();
        showQuestion();
        cardLayout.show(cards, QUIZ_CARD);
    }

    /** 今の問題を画面に表示する。 */
    private void showQuestion() {
        Question q = manager.current();
        numberLabel.setText(manager.getNumber() + " / " + manager.getTotal() + "問");
        questionLabel.setText(q.text());
        List<String> choices = q.shuffledChoices();
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i].setText(choices.get(i));
        }
        judgeLabel.setText(" ");
        setChoicesEnabled(true);
        nextButton.setEnabled(false);
    }

    /** 選択肢が押されたときの処理。 */
    private void onChoose(String choice) {
        boolean correct = manager.answer(choice);
        if (correct) {
            judgeLabel.setText("正解！");
        } else {
            judgeLabel.setText("不正解… 正解は「" + manager.current().answer() + "」");
        }
        // 「次へ」を押すまで、選択肢を押せなくする
        setChoicesEnabled(false);
        nextButton.setEnabled(true);
    }

    /** 次へボタンが押されたときの処理。 */
    private void onNext() {
        manager.next();
        if (manager.isFinished()) {
            showResult();
        } else {
            showQuestion();
        }
    }

    /** F5-05：結果画面を表示する。 */
    private void showResult() {
        scoreLabel.setText(String.format("%d問中 %d問正解（正答率 %.1f%%）",
                manager.getTotal(), manager.getCorrectCount(), manager.getCorrectRate()));

        StringBuilder sb = new StringBuilder();
        List<Question> mistakes = manager.getMistakes();
        if (mistakes.isEmpty()) {
            sb.append("全問正解です！");
        } else {
            sb.append("間違えた問題\n");
            for (Question q : mistakes) {
                sb.append("・").append(q.text()).append("　→　").append(q.answer()).append("\n");
            }
        }
        mistakeArea.setText(sb.toString());
        cardLayout.show(cards, RESULT_CARD);
    }

    private void setChoicesEnabled(boolean enabled) {
        for (JButton button : choiceButtons) {
            button.setEnabled(enabled);
        }
    }

    /** 読み込みに失敗したときの表示。 */
    private void showLoadError(String message) {
        JOptionPane.showMessageDialog(null, message, "ファイルエラー", JOptionPane.ERROR_MESSAGE);
        questionLabel.setText("問題を読み込めませんでした");
        setChoicesEnabled(false);
        nextButton.setEnabled(false);
    }
}
