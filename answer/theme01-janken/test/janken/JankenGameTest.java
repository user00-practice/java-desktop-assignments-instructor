package janken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JankenGame（勝敗判定と通算成績）のテスト。
 */
class JankenGameTest {

    @ParameterizedTest(name = "自分が{0}、コンピュータが{1}なら{2}")
    @DisplayName("F1-03：9通りすべての組み合わせで勝敗が正しい")
    @CsvSource({
            "ROCK,     ROCK,     DRAW",
            "ROCK,     SCISSORS, WIN",
            "ROCK,     PAPER,    LOSE",
            "SCISSORS, ROCK,     LOSE",
            "SCISSORS, SCISSORS, DRAW",
            "SCISSORS, PAPER,    WIN",
            "PAPER,    ROCK,     WIN",
            "PAPER,    SCISSORS, LOSE",
            "PAPER,    PAPER,    DRAW"
    })
    void judge(Hand player, Hand computer, Result expected) {
        assertEquals(expected, JankenGame.judge(player, computer));
    }

    @Test
    @DisplayName("F1-02・F1-04：play の結果に、自分の手とコンピュータの手と勝敗が入っている")
    void playReturnsRound() {
        // 同じシードの Random を2つ用意し、片方でコンピュータの手を予想する
        Hand expectedComputer = Hand.random(new Random(42));
        JankenGame game = new JankenGame(new Random(42));

        Round round = game.play(Hand.ROCK);

        assertEquals(Hand.ROCK, round.player());
        assertEquals(expectedComputer, round.computer());
        assertEquals(JankenGame.judge(Hand.ROCK, expectedComputer), round.result());
    }

    @Test
    @DisplayName("F1-05：勝ち・負け・あいこの回数が、結果どおりに数えられる")
    void countsResults() {
        JankenGame game = new JankenGame(new Random(1));
        int win = 0;
        int lose = 0;
        int draw = 0;
        for (int i = 0; i < 30; i++) {
            switch (game.play(Hand.PAPER).result()) {
                case WIN -> win++;
                case LOSE -> lose++;
                case DRAW -> draw++;
            }
        }
        assertEquals(win, game.getWinCount());
        assertEquals(lose, game.getLoseCount());
        assertEquals(draw, game.getDrawCount());
        assertEquals(30, win + lose + draw);
    }

    @Test
    @DisplayName("F1-06：リセットで通算成績が0に戻る")
    void reset() {
        JankenGame game = new JankenGame(new Random(1));
        for (int i = 0; i < 5; i++) {
            game.play(Hand.ROCK);
        }
        game.reset();
        assertEquals(0, game.getWinCount());
        assertEquals(0, game.getLoseCount());
        assertEquals(0, game.getDrawCount());
    }
}
