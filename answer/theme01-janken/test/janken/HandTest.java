package janken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Hand（じゃんけんの手）のテスト。
 */
class HandTest {

    @Test
    @DisplayName("表示名がグー・チョキ・パーになっている")
    void labels() {
        assertEquals("グー", Hand.ROCK.getLabel());
        assertEquals("チョキ", Hand.SCISSORS.getLabel());
        assertEquals("パー", Hand.PAPER.getLabel());
    }

    @Test
    @DisplayName("random を何度も呼ぶと、3つの手がすべて出る")
    void randomReturnsAllHands() {
        Random random = new Random(0);
        Set<Hand> appeared = EnumSet.noneOf(Hand.class);
        for (int i = 0; i < 100; i++) {
            appeared.add(Hand.random(random));
        }
        assertEquals(EnumSet.allOf(Hand.class), appeared);
    }

    @Test
    @DisplayName("同じシードの Random を渡すと、同じ順番で手が出る")
    void sameSeedSameSequence() {
        Random a = new Random(123);
        Random b = new Random(123);
        for (int i = 0; i < 10; i++) {
            assertEquals(Hand.random(a), Hand.random(b));
        }
    }
}
