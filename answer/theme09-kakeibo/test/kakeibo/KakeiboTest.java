package kakeibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kakeibo（記録の管理・集計・保存）のテスト。
 */
class KakeiboTest {

    private static final YearMonth NOV = YearMonth.of(2026, 11);

    @TempDir
    Path tempDir;

    private Path file;
    private Kakeibo kakeibo;

    @BeforeEach
    void setUp() throws IOException {
        file = tempDir.resolve("kakeibo.csv");
        kakeibo = new Kakeibo(file);
        kakeibo.load();
        // 11月の記録3件（登録順は日付順ではない）と、10月の記録1件
        kakeibo.add(LocalDate.of(2026, 11, 3), EntryType.EXPENSE, "食費", 800, "昼ごはん");
        kakeibo.add(LocalDate.of(2026, 11, 1), EntryType.INCOME, "給料", 200000, "");
        kakeibo.add(LocalDate.of(2026, 11, 2), EntryType.EXPENSE, "交通費", 400, "");
        kakeibo.add(LocalDate.of(2026, 10, 30), EntryType.EXPENSE, "食費", 999, "");
    }

    @Test
    @DisplayName("F9-05：指定した月の記録だけを、日付の古い順に返す")
    void entriesOf() {
        List<Entry> entries = kakeibo.entriesOf(NOV);
        assertEquals(List.of(1, 2, 3), entries.stream().map(e -> e.date().getDayOfMonth()).toList());
    }

    @Test
    @DisplayName("F9-06：月ごとの収入合計・支出合計")
    void totals() {
        assertEquals(200000, kakeibo.totalOf(NOV, EntryType.INCOME));
        assertEquals(1200, kakeibo.totalOf(NOV, EntryType.EXPENSE));
        assertEquals(999, kakeibo.totalOf(YearMonth.of(2026, 10), EntryType.EXPENSE));
    }

    @Test
    @DisplayName("F9-07：費目別の支出合計（記録の無い費目は0、並びは費目の定義順）")
    void expenseByCategory() {
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("食費", 800);
        expected.put("日用品", 0);
        expected.put("交通費", 400);
        expected.put("娯楽", 0);
        expected.put("その他", 0);

        Map<String, Integer> actual = kakeibo.expenseByCategory(NOV);
        assertEquals(expected, actual);
        assertEquals(List.copyOf(expected.keySet()), List.copyOf(actual.keySet()));
    }

    @Test
    @DisplayName("F9-08：記録を削除すると、一覧と集計から消える")
    void remove() throws IOException {
        Entry food = kakeibo.entriesOf(NOV).get(2);    // 11/3 の食費
        kakeibo.remove(food);
        assertEquals(2, kakeibo.entriesOf(NOV).size());
        assertEquals(400, kakeibo.totalOf(NOV, EntryType.EXPENSE));
    }

    @Test
    @DisplayName("F9-09：保存した記録を、別のインスタンスで読み込める（再起動の代わり）")
    void persistence() throws IOException {
        Kakeibo reopened = new Kakeibo(file);
        reopened.load();
        assertEquals(kakeibo.entriesOf(NOV), reopened.entriesOf(NOV));
        assertEquals(1200, reopened.totalOf(NOV, EntryType.EXPENSE));
    }

    @Test
    @DisplayName("メモの中のカンマは「、」に置き換えて保存する（CSVが崩れないように）")
    void commaInMemo() throws IOException {
        kakeibo.add(LocalDate.of(2026, 11, 5), EntryType.EXPENSE, "娯楽", 1500, "映画,ポップコーン");

        Kakeibo reopened = new Kakeibo(file);
        reopened.load();
        Entry last = reopened.entriesOf(NOV).get(3);
        assertEquals("映画、ポップコーン", last.memo());
    }

    @Test
    @DisplayName("月の選択肢には、記録のある月と今月が新しい順に並ぶ")
    void months() {
        List<YearMonth> months = kakeibo.months();
        assertTrue(months.contains(YearMonth.now()));
        assertTrue(months.contains(NOV));
        assertTrue(months.contains(YearMonth.of(2026, 10)));
        for (int i = 1; i < months.size(); i++) {
            assertTrue(months.get(i - 1).isAfter(months.get(i)));
        }
    }
}
