package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Calculator（電卓の計算と状態管理）のテスト。
 * ボタンを押す順番を文字列で書き、最後の表示を確かめる。
 */
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    /** 「1 2 + 3 4 =」のような空白区切りのキー操作を、順番に押す。 */
    private String press(String keys) {
        for (String key : keys.split(" ")) {
            switch (key) {
                case "C" -> calculator.clear();
                case "=" -> calculator.equal();
                case "." -> calculator.inputDot();
                case "+", "−", "×", "÷" -> calculator.inputOperator(Operator.fromSymbol(key));
                default -> calculator.inputDigit(key);
            }
        }
        return calculator.getDisplay();
    }

    @Test
    @DisplayName("起動直後の表示は 0")
    void initialDisplay() {
        assertEquals("0", calculator.getDisplay());
    }

    @ParameterizedTest(name = "{0}  →  {1}")
    @DisplayName("受け入れ基準と、四則演算の基本")
    @CsvSource(delimiter = '|', value = {
            "1 2 + 3 4 =   | 46",
            "1 + 2 × 3 =   | 9",
            "1 . 5 × 2 =   | 3",
            "9 − 1 2 =     | -3",
            "7 ÷ 2 =       | 3.5",
            "1 + 2 + 3 =   | 6",
            "2 × 3 × 4 =   | 24"
    })
    void calculations(String keys, String expected) {
        assertEquals(expected, press(keys.trim()));
    }

    @Test
    @DisplayName("F3-03：演算子を押した時点で、それまでの計算結果が表示される")
    void showsIntermediateResult() {
        assertEquals("3", press("1 + 2 +"));
    }

    @Test
    @DisplayName("演算子を続けて押したら、後から押したものに置き換わる")
    void replaceOperator() {
        assertEquals("2", press("5 + − 3 ="));
    }

    @Test
    @DisplayName("F3-04：1つの数に小数点は1つまで")
    void onlyOneDot() {
        assertEquals("1.5", press("1 . . 5"));
    }

    @Test
    @DisplayName("F3-04：数の最初に小数点を押すと「0.」になる")
    void dotFirst() {
        assertEquals("0.5", press(". 5"));
    }

    @Test
    @DisplayName("先頭の0は数字で置き換わる（「05」にならない）")
    void leadingZero() {
        assertEquals("5", press("0 5"));
    }

    @Test
    @DisplayName("＝のあとに数字を押すと、新しい数の入力になる")
    void newInputAfterEqual() {
        assertEquals("7", press("1 + 2 = 7"));
    }

    @Test
    @DisplayName("＝の結果に続けて演算できる")
    void continueAfterEqual() {
        assertEquals("30", press("1 + 2 = × 1 0 ="));
    }

    @Test
    @DisplayName("F3-06：0で割るとエラー表示になり、Cを押すまで入力を受け付けない")
    void divideByZero() {
        assertEquals("エラー", press("5 ÷ 0 ="));
        assertEquals("エラー", press("3 + 1 ="));
        assertEquals("0", press("C"));
        assertEquals("4", press("3 + 1 ="));
    }

    @Test
    @DisplayName("F3-05：Cで計算途中の状態もすべて消える")
    void clearResetsPendingOperator() {
        assertEquals("8", press("9 + C 8 ="));
    }

    @ParameterizedTest(name = "{0} は「{1}」と表示")
    @DisplayName("F3-07：整数になる結果は小数点なしで表示する")
    @CsvSource({
            "5.0,  5",
            "-3.0, -3",
            "3.5,  3.5",
            "0.0,  0"
    })
    void format(double value, String expected) {
        assertEquals(expected, Calculator.format(value));
    }
}
