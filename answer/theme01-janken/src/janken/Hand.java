package janken;

import java.util.Random;

/**
 * じゃんけんの手（グー・チョキ・パー）を表す列挙型。
 */
public enum Hand {
    ROCK("グー"),
    SCISSORS("チョキ"),
    PAPER("パー");

    private final String label;

    Hand(String label) {
        this.label = label;
    }

    /** 画面に表示する名前を返す。 */
    public String getLabel() {
        return label;
    }

    /** 3つの手の中から1つをランダムに選ぶ。 */
    public static Hand random(Random random) {
        Hand[] hands = values();
        return hands[random.nextInt(hands.length)];
    }
}
