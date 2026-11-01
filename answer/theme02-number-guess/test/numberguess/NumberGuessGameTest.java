package numberguess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NumberGuessGame（判定・回数・履歴）のテスト。
 */
class NumberGuessGameTest {

    private NumberGuessGame game;

    @BeforeEach
    void setUp() {
        game = new NumberGuessGame();
    }

    /** 正解以外の数を1つ返す（テスト用）。 */
    private int wrongNumber() {
        return game.getAnswer() == 1 ? 2 : 1;
    }

    @Test
    @DisplayName("F2-01：正解は1〜100の範囲にある")
    void answerInRange() {
        for (int i = 0; i < 200; i++) {
            game.reset();
            assertTrue(game.getAnswer() >= 1 && game.getAnswer() <= 100);
        }
    }

    @Test
    @DisplayName("F2-05：範囲チェック（1と100はOK、0と101はNG）")
    void isInRange() {
        assertTrue(game.isInRange(1));
        assertTrue(game.isInRange(100));
        assertFalse(game.isInRange(0));
        assertFalse(game.isInRange(101));
    }

    @Test
    @DisplayName("F2-02：正解より小さい数・大きい数で、それぞれのヒントが返る")
    void hints() {
        int answer = game.getAnswer();
        if (answer > 1) {
            assertEquals(Hint.TOO_SMALL, game.guess(answer - 1));
        }
        if (answer < 100) {
            assertEquals(Hint.TOO_LARGE, game.guess(answer + 1));
        }
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    @DisplayName("F2-03：正解すると CLEARED になり、回数が記録される")
    void correct() {
        game.guess(wrongNumber());
        assertEquals(Hint.CORRECT, game.guess(game.getAnswer()));
        assertEquals(GameState.CLEARED, game.getState());
        assertEquals(2, game.getTryCount());
    }

    @Test
    @DisplayName("F2-04：10回外れると FAILED になる（9回目まではプレイ中）")
    void failAfterTenTries() {
        for (int i = 0; i < 9; i++) {
            game.guess(wrongNumber());
        }
        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(1, game.getRemainingTries());

        game.guess(wrongNumber());
        assertEquals(GameState.FAILED, game.getState());
        assertEquals(0, game.getRemainingTries());
    }

    @Test
    @DisplayName("10回目で正解したら FAILED ではなく CLEARED になる")
    void correctOnLastTry() {
        for (int i = 0; i < 9; i++) {
            game.guess(wrongNumber());
        }
        game.guess(game.getAnswer());
        assertEquals(GameState.CLEARED, game.getState());
    }

    @Test
    @DisplayName("F2-06：入力した数が順番に履歴に残る")
    void history() {
        game.guess(10);
        game.guess(50);
        game.guess(30);
        assertEquals(List.of(10, 50, 30), game.getHistory());
    }

    @Test
    @DisplayName("F2-07：リセットで履歴・回数・状態が最初に戻る")
    void reset() {
        game.guess(wrongNumber());
        game.guess(game.getAnswer());
        game.reset();
        assertEquals(List.of(), game.getHistory());
        assertEquals(10, game.getRemainingTries());
        assertEquals(GameState.PLAYING, game.getState());
    }
}
