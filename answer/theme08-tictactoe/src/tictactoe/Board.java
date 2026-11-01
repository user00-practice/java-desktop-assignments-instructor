package tictactoe;

/**
 * 盤面（3×3）の管理・手番の交代・勝敗と引き分けの判定を担当するクラス。
 * 何回ゲームをしても続けて数える戦績（F8-07）もここで管理する。
 */
public class Board {

    public static final int SIZE = 3;

    private final Mark[][] cells = new Mark[SIZE][SIZE];
    private Mark turn;
    private Mark winner;
    private boolean draw;

    private int circleWins;
    private int crossWins;
    private int drawCount;

    public Board() {
        reset();
    }

    /** F8-06：盤面を空に戻して、○の手番から始める。（戦績はそのまま） */
    public void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = Mark.EMPTY;
            }
        }
        turn = Mark.CIRCLE;
        winner = null;
        draw = false;
    }

    /**
     * F8-01・F8-02：指定したマスに手番の印を置く。
     * 置けたら true、置けなかった（印がある・決着済み）なら false を返す。
     */
    public boolean place(int row, int col) {
        if (isOver() || cells[row][col] != Mark.EMPTY) {
            return false;
        }
        cells[row][col] = turn;

        if (hasLine(turn)) {                 // F8-03：揃ったら勝ち
            winner = turn;
            if (winner == Mark.CIRCLE) {
                circleWins++;
            } else {
                crossWins++;
            }
        } else if (isFull()) {               // F8-04：全部埋まったら引き分け
            draw = true;
            drawCount++;
        } else {
            turn = turn.opponent();          // 手番を交代する
        }
        return true;
    }

    /** 指定した印が、縦・横・斜めのどれかで3つ揃っているかを調べる。 */
    private boolean hasLine(Mark mark) {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] == mark && cells[i][1] == mark && cells[i][2] == mark) {
                return true;                 // 横の行
            }
            if (cells[0][i] == mark && cells[1][i] == mark && cells[2][i] == mark) {
                return true;                 // 縦の列
            }
        }
        if (cells[0][0] == mark && cells[1][1] == mark && cells[2][2] == mark) {
            return true;                     // 左上→右下の斜め
        }
        return cells[0][2] == mark && cells[1][1] == mark && cells[2][0] == mark; // 右上→左下
    }

    /** 9マスすべて埋まっているかを調べる。 */
    private boolean isFull() {
        for (Mark[] row : cells) {
            for (Mark mark : row) {
                if (mark == Mark.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /** 勝敗が決まった（勝ちまたは引き分け）かどうか。 */
    public boolean isOver() {
        return winner != null || draw;
    }

    public Mark getMark(int row, int col) {
        return cells[row][col];
    }

    public Mark getTurn() {
        return turn;
    }

    /** 勝者を返す。まだ決まっていなければ null。 */
    public Mark getWinner() {
        return winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public int getCircleWins() {
        return circleWins;
    }

    public int getCrossWins() {
        return crossWins;
    }

    public int getDrawCount() {
        return drawCount;
    }
}
