package tictactoe;

/**
 * マスの状態（空・○・×）を表す列挙型。
 */
public enum Mark {
    EMPTY(""),
    CIRCLE("○"),
    CROSS("×");

    private final String symbol;

    Mark(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /** 相手の印を返す（○なら×、×なら○）。 */
    public Mark opponent() {
        return switch (this) {
            case CIRCLE -> CROSS;
            case CROSS -> CIRCLE;
            case EMPTY -> EMPTY;
        };
    }
}
