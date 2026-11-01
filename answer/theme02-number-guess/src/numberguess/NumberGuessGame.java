package numberguess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 正解の決定・大小の判定・残り回数と履歴の管理を担当するクラス。
 */
public class NumberGuessGame {

    public static final int MIN = 1;
    public static final int MAX = 100;
    public static final int MAX_TRIES = 10;

    private final Random random = new Random();
    private final List<Integer> history = new ArrayList<>();
    private int answer;
    private GameState state;

    public NumberGuessGame() {
        reset();
    }

    /** F2-01・F2-07：新しい正解を決めて、最初からやり直す。 */
    public void reset() {
        answer = random.nextInt(MAX - MIN + 1) + MIN;
        history.clear();
        state = GameState.PLAYING;
    }

    /** 入力された数が範囲内（1〜100）かどうかを調べる。（F2-05） */
    public boolean isInRange(int number) {
        return number >= MIN && number <= MAX;
    }

    /**
     * 入力された数を判定する。（F2-02〜F2-04）
     * 範囲のチェックは、呼び出す前に isInRange で行っておくこと。
     */
    public Hint guess(int number) {
        history.add(number);

        Hint hint;
        if (number < answer) {
            hint = Hint.TOO_SMALL;
        } else if (number > answer) {
            hint = Hint.TOO_LARGE;
        } else {
            hint = Hint.CORRECT;
        }

        if (hint == Hint.CORRECT) {
            state = GameState.CLEARED;
        } else if (getRemainingTries() == 0) {
            state = GameState.FAILED;
        }
        return hint;
    }

    public int getRemainingTries() {
        return MAX_TRIES - history.size();
    }

    public int getTryCount() {
        return history.size();
    }

    public List<Integer> getHistory() {
        return List.copyOf(history);
    }

    public GameState getState() {
        return state;
    }

    public int getAnswer() {
        return answer;
    }
}
