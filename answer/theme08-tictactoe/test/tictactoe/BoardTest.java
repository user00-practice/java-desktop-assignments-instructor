package tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Board（盤面・手番・勝敗判定）のテスト。
 */
class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * 「行,列」を空白で区切った文字列の順に置く。○と×が交互に置かれる。
     * 例："0,0 1,0 0,1" → ○(0,0) ×(1,0) ○(0,1)
     */
    private void play(String moves) {
        for (String move : moves.trim().split("\\s+")) {
            String[] rc = move.split(",");
            board.place(Integer.parseInt(rc[0]), Integer.parseInt(rc[1]));
        }
    }

    @Test
    @DisplayName("最初は空の盤面で、○の番")
    void initialState() {
        assertEquals(Mark.CIRCLE, board.getTurn());
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(Mark.EMPTY, board.getMark(r, c));
            }
        }
    }

    @Test
    @DisplayName("F8-01：置くと印が入り、手番が交代する")
    void placeAndSwitchTurn() {
        assertTrue(board.place(1, 1));
        assertEquals(Mark.CIRCLE, board.getMark(1, 1));
        assertEquals(Mark.CROSS, board.getTurn());
    }

    @Test
    @DisplayName("F8-02：印のあるマスには置けず、手番も変わらない")
    void cannotPlaceOnOccupied() {
        board.place(1, 1);
        assertFalse(board.place(1, 1));
        assertEquals(Mark.CIRCLE, board.getMark(1, 1));
        assertEquals(Mark.CROSS, board.getTurn());
    }

    // ○が揃う3マスを先に、×はそれ以外の場所に置く手順（○ × ○ × ○ の順）
    @ParameterizedTest(name = "{0}")
    @DisplayName("F8-03：縦・横・斜め、8通りすべての揃い方で○の勝ちになる")
    @CsvSource(delimiter = '|', value = {
            "横 1行目     | 0,0 1,0 0,1 1,1 0,2",
            "横 2行目     | 1,0 0,0 1,1 0,1 1,2",
            "横 3行目     | 2,0 0,0 2,1 0,1 2,2",
            "縦 1列目     | 0,0 0,1 1,0 1,1 2,0",
            "縦 2列目     | 0,1 0,0 1,1 1,0 2,1",
            "縦 3列目     | 0,2 0,0 1,2 1,0 2,2",
            "斜め 左上から | 0,0 0,1 1,1 0,2 2,2",
            "斜め 右上から | 0,2 0,1 1,1 0,0 2,0"
    })
    void winLines(String name, String moves) {
        play(moves);
        assertEquals(Mark.CIRCLE, board.getWinner());
        assertTrue(board.isOver());
    }

    @Test
    @DisplayName("×も勝てる")
    void crossWins() {
        play("0,0 1,0 0,1 1,1 2,2 1,2");
        assertEquals(Mark.CROSS, board.getWinner());
    }

    @Test
    @DisplayName("F8-04：9マス埋まって揃っていなければ引き分け")
    void draw() {
        // ○×○ / ○×× / ×○○
        play("0,0 0,1 0,2 1,1 1,0 1,2 2,1 2,0 2,2");
        assertTrue(board.isDraw());
        assertNull(board.getWinner());
        assertTrue(board.isOver());
    }

    @Test
    @DisplayName("9マス目で揃ったら、引き分けではなく勝ち")
    void winOnLastCell() {
        // 最後の○(2,2)で 3行目が揃う盤面：○×× / ××○ / ○○○
        play("0,0 0,1 1,2 0,2 2,0 1,0 2,1 1,1 2,2");
        assertEquals(Mark.CIRCLE, board.getWinner());
        assertFalse(board.isDraw());
    }

    @Test
    @DisplayName("F8-05：決着したあとは置けない")
    void cannotPlaceAfterGameOver() {
        play("0,0 1,0 0,1 1,1 0,2");
        assertFalse(board.place(2, 2));
        assertEquals(Mark.EMPTY, board.getMark(2, 2));
    }

    @Test
    @DisplayName("F8-06・F8-07：新しいゲームで盤面は空に戻り、戦績は残る")
    void resetKeepsStats() {
        play("0,0 1,0 0,1 1,1 0,2");                       // ○の勝ち
        board.reset();
        play("0,0 0,1 0,2 1,1 1,0 1,2 2,1 2,0 2,2");       // 引き分け
        board.reset();
        play("0,0 1,0 0,1 1,1 2,2 1,2");                   // ×の勝ち
        board.reset();

        assertEquals(Mark.EMPTY, board.getMark(0, 0));
        assertEquals(Mark.CIRCLE, board.getTurn());
        assertEquals(1, board.getCircleWins());
        assertEquals(1, board.getCrossWins());
        assertEquals(1, board.getDrawCount());
    }
}
