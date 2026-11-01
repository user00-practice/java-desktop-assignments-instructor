package tictactoe;

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
 * 三目並べの画面を作るクラス。
 * 9個のボタンを2次元配列で持ち、押されたマスの行・列を Board に伝える。
 */
public class MainFrame extends JFrame {

    private static final Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

    private final Board board = new Board();
    private final JButton[][] buttons = new JButton[Board.SIZE][Board.SIZE];
    private final JLabel messageLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);

    public MainFrame() {
        setTitle("三目並べ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);

        // 上部：手番や結果
        messageLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 中央：3×3のマス（行・列の二重ループで作る）
        JPanel boardPanel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE, 4, 4));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        Font cellFont = new Font(Font.DIALOG, Font.BOLD, 48);
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                JButton button = new JButton();
                button.setFont(cellFont);
                // ラムダ式の中で使う変数は、値が変わらない（実質 final の）変数にする
                int r = row;
                int c = col;
                button.addActionListener(e -> onCellClicked(r, c));
                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        // 下部：新しいゲームボタンと戦績
        JButton newGameButton = new JButton("新しいゲーム");
        newGameButton.setFont(NORMAL_FONT);
        newGameButton.addActionListener(e -> onNewGame());
        scoreLabel.setFont(NORMAL_FONT);
        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottom.add(newGameButton);
        bottom.add(scoreLabel);

        add(messageLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        updateView();
    }

    /** マスが押されたときの処理。 */
    private void onCellClicked(int row, int col) {
        if (board.place(row, col)) {
            updateView();
        }
    }

    /** 新しいゲームボタンが押されたときの処理。 */
    private void onNewGame() {
        board.reset();
        updateView();
    }

    /** 盤面・メッセージ・戦績の表示を Board の状態に合わせる。 */
    private void updateView() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Mark mark = board.getMark(row, col);
                buttons[row][col].setText(mark.getSymbol());
                // F8-05：決着したら、残りの空きマスを押せなくする
                // （印のあるマスまで無効にすると文字が薄い灰色になり見にくいので、有効のままにする。
                //   押されても Board.place が false を返すので、何も起きない）
                buttons[row][col].setEnabled(mark != Mark.EMPTY || !board.isOver());
            }
        }

        if (board.getWinner() != null) {
            messageLabel.setText(board.getWinner().getSymbol() + " の勝ちです！");
        } else if (board.isDraw()) {
            messageLabel.setText("引き分けです");
        } else {
            messageLabel.setText(board.getTurn().getSymbol() + " の番です");
        }

        scoreLabel.setText("○の勝ち " + board.getCircleWins()
                + "　×の勝ち " + board.getCrossWins()
                + "　引き分け " + board.getDrawCount());
    }
}
