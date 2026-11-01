package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Operator（演算子ごとの計算）のテスト。
 */
class OperatorTest {

    @Test
    @DisplayName("四則演算が正しく計算される")
    void apply() {
        assertEquals(5.0, Operator.PLUS.apply(2, 3));
        assertEquals(-1.0, Operator.MINUS.apply(2, 3));
        assertEquals(6.0, Operator.TIMES.apply(2, 3));
        assertEquals(2.5, Operator.DIVIDE.apply(5, 2));
    }

    @Test
    @DisplayName("0で割ると ArithmeticException が発生する")
    void divideByZero() {
        assertThrows(ArithmeticException.class, () -> Operator.DIVIDE.apply(1, 0));
    }

    @Test
    @DisplayName("記号から演算子を探せる。知らない記号なら null")
    void fromSymbol() {
        assertEquals(Operator.PLUS, Operator.fromSymbol("+"));
        assertEquals(Operator.MINUS, Operator.fromSymbol("−"));
        assertEquals(Operator.TIMES, Operator.fromSymbol("×"));
        assertEquals(Operator.DIVIDE, Operator.fromSymbol("÷"));
        assertNull(Operator.fromSymbol("%"));
    }
}
