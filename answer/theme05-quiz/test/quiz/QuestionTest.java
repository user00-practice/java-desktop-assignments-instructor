package quiz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Question（1問分のデータ）のテスト。
 */
class QuestionTest {

    @Test
    @DisplayName("CSVの1行から問題文・正解・不正解を取り出せる")
    void fromCsv() {
        Question q = Question.fromCsv("apple の意味は？,りんご,みかん,ぶどう,もも");
        assertEquals("apple の意味は？", q.text());
        assertEquals("りんご", q.answer());
        assertEquals(List.of("みかん", "ぶどう", "もも"), q.wrongs());
    }

    @Test
    @DisplayName("前後の空白は取り除かれる")
    void fromCsvTrims() {
        Question q = Question.fromCsv(" 問題 , 正解 , a , b , c ");
        assertEquals("問題", q.text());
        assertEquals("正解", q.answer());
    }

    @Test
    @DisplayName("列が5つでなければ IllegalArgumentException")
    void fromCsvInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Question.fromCsv("問題,正解,a"));
        assertThrows(IllegalArgumentException.class, () -> Question.fromCsv("1,2,3,4,5,6"));
    }

    @Test
    @DisplayName("F5-03：選択肢は正解と不正解の4つで、何度か作ると並び順が変わる")
    void shuffledChoices() {
        Question q = Question.fromCsv("問題,正解,a,b,c");
        Set<List<String>> orders = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            List<String> choices = q.shuffledChoices();
            assertEquals(Set.of("正解", "a", "b", "c"), new HashSet<>(choices));
            assertEquals(4, choices.size());
            orders.add(choices);
        }
        // 50回作って、すべて同じ並びになることはまず無い
        assertEquals(true, orders.size() > 1);
    }
}
