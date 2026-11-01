package janken;

/**
 * じゃんけんの結果（勝ち・負け・あいこ）を表す列挙型。
 */
public enum Result {
    WIN("勝ち"),
    LOSE("負け"),
    DRAW("あいこ");

    private final String label;

    Result(String label) {
        this.label = label;
    }

    /** 画面に表示する名前を返す。 */
    public String getLabel() {
        return label;
    }
}
