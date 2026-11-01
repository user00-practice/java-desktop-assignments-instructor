package calculator;

/**
 * 四則演算と、計算途中の状態（直前の数・演算子）の管理を担当するクラス。
 * 画面のことは何も知らず、「表示すべき文字列」を getDisplay() で返すだけにしている。
 */
public class Calculator {

    /** 入力中の数、または計算結果（表示している文字列） */
    private String input = "0";
    /** 演算子を押す前に入力されていた数 */
    private double stored;
    /** 押された演算子（まだ押されていなければ null） */
    private Operator pending;
    /** 次に数字を押したとき、入力を新しく始めるかどうか */
    private boolean startNew = true;
    /** 0で割ったなどのエラー状態かどうか */
    private boolean error;

    /** 表示欄に出す文字列を返す。 */
    public String getDisplay() {
        return error ? "エラー" : input;
    }

    /** F3-01：数字ボタンが押されたとき。 */
    public void inputDigit(String digit) {
        if (error) {
            return;
        }
        if (startNew) {
            input = digit;
            startNew = false;
        } else if (input.equals("0")) {
            input = digit;           // 「0」のあとに数字を押したら置き換える（「05」にしない）
        } else {
            input = input + digit;
        }
    }

    /** F3-04：小数点ボタンが押されたとき。1つの数に小数点は1つまで。 */
    public void inputDot() {
        if (error) {
            return;
        }
        if (startNew) {
            input = "0.";
            startNew = false;
        } else if (!input.contains(".")) {
            input = input + ".";
        }
    }

    /** F3-02・F3-03：演算子ボタンが押されたとき。 */
    public void inputOperator(Operator operator) {
        if (error) {
            return;
        }
        // 「1 + 2 +」のように続けて押されたら、ここまでの計算を先に済ませる（左から順に計算）
        if (pending != null && !startNew) {
            calculate();
            if (error) {
                return;
            }
        }
        stored = Double.parseDouble(input);
        pending = operator;
        startNew = true;
    }

    /** ＝ボタンが押されたとき。 */
    public void equal() {
        if (error || pending == null) {
            return;
        }
        calculate();
        pending = null;
        startNew = true;
    }

    /** F3-05：Cボタンが押されたとき。すべての状態を最初に戻す。 */
    public void clear() {
        input = "0";
        stored = 0;
        pending = null;
        startNew = true;
        error = false;
    }

    /** stored と input を pending の演算子で計算し、結果を input に入れる。 */
    private void calculate() {
        try {
            double result = pending.apply(stored, Double.parseDouble(input));
            input = format(result);
        } catch (ArithmeticException e) {
            error = true;            // F3-06：0除算はエラー表示にする
        }
    }

    /** F3-07：整数になる結果は「5.0」ではなく「5」と表示する。 */
    static String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
