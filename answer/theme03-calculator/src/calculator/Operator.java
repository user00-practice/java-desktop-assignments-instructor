package calculator;

/**
 * 四則演算の演算子を表す列挙型。演算子ごとの計算方法もここに書く。
 */
public enum Operator {
    PLUS("+"),
    MINUS("−"),
    TIMES("×"),
    DIVIDE("÷");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    /** ボタンに表示する記号を返す。 */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 2つの数を計算する。0で割ろうとしたときは ArithmeticException を投げる。
     */
    public double apply(double left, double right) {
        return switch (this) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case TIMES -> left * right;
            case DIVIDE -> {
                if (right == 0) {
                    throw new ArithmeticException("0で割ることはできません");
                }
                yield left / right;
            }
        };
    }

    /** 記号から演算子を探す。見つからなければ null を返す。 */
    public static Operator fromSymbol(String symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        return null;
    }
}
