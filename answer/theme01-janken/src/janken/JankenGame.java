package janken;

import java.util.Random;

/**
 * じゃんけんの手の決定・勝敗判定・通算成績の記録を担当するクラス。
 */
public class JankenGame {

    private final Random random;
    private int winCount;
    private int loseCount;
    private int drawCount;

    /** 通常はこちらを使う。毎回ちがう手が出る。 */
    public JankenGame() {
        this(new Random());
    }

    /**
     * 使う Random を外から渡す。
     * テストでシード（種）を決めた Random を渡すと、コンピュータの手の順番を固定できる。
     */
    public JankenGame(Random random) {
        this.random = random;
    }

    /**
     * 自分の手を受け取ってじゃんけんを1回行い、その内容を返す。（F1-01〜F1-03）
     */
    public Round play(Hand player) {
        Hand computer = Hand.random(random);
        Result result = judge(player, computer);

        // F1-05：通算成績を数える
        switch (result) {
            case WIN -> winCount++;
            case LOSE -> loseCount++;
            case DRAW -> drawCount++;
        }
        return new Round(player, computer, result);
    }

    /**
     * 自分の手とコンピュータの手から、自分から見た勝敗を判定する。
     */
    public static Result judge(Hand player, Hand computer) {
        if (player == computer) {
            return Result.DRAW;
        }
        // 自分の手ごとに「勝てる相手の手」を決めておき、それと一致すれば勝ち
        Hand canBeat = switch (player) {
            case ROCK -> Hand.SCISSORS;
            case SCISSORS -> Hand.PAPER;
            case PAPER -> Hand.ROCK;
        };
        return computer == canBeat ? Result.WIN : Result.LOSE;
    }

    /** F1-06：通算成績を0に戻す。 */
    public void reset() {
        winCount = 0;
        loseCount = 0;
        drawCount = 0;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getDrawCount() {
        return drawCount;
    }
}
