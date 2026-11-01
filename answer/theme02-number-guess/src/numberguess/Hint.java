package numberguess;

/**
 * 入力した数を判定した結果を表す列挙型。
 */
public enum Hint {
    TOO_SMALL("小さすぎます。正解はもっと大きい数です"),
    TOO_LARGE("大きすぎます。正解はもっと小さい数です"),
    CORRECT("正解！");

    private final String message;

    Hint(String message) {
        this.message = message;
    }

    /** 画面に表示するメッセージを返す。 */
    public String getMessage() {
        return message;
    }
}
