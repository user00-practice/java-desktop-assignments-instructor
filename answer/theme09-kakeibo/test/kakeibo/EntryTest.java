package kakeibo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Entry（記録1件）と EntryType のテスト。
 */
class EntryTest {

    @Test
    @DisplayName("CSVの1行に変換して、元に戻せる")
    void csvRoundTrip() {
        Entry entry = new Entry(LocalDate.of(2026, 11, 1), EntryType.EXPENSE, "食費", 850, "昼ごはん");
        assertEquals("2026-11-01,EXPENSE,食費,850,昼ごはん", entry.toCsv());
        assertEquals(entry, Entry.fromCsv(entry.toCsv()));
    }

    @Test
    @DisplayName("メモが空でも読み込める")
    void emptyMemo() {
        Entry entry = Entry.fromCsv("2026-11-01,INCOME,給料,200000,");
        assertEquals("", entry.memo());
        assertEquals(EntryType.INCOME, entry.type());
    }

    @Test
    @DisplayName("形式が違う行は例外になる")
    void invalidLine() {
        assertThrows(RuntimeException.class, () -> Entry.fromCsv("2026/11/01,EXPENSE,食費,850,"));
        assertThrows(RuntimeException.class, () -> Entry.fromCsv("2026-11-01,EXPENSE,食費,abc,"));
    }

    @Test
    @DisplayName("F9-03：区分ごとに選べる費目")
    void categories() {
        assertEquals(java.util.List.of("給料", "その他"), EntryType.INCOME.getCategories());
        assertEquals(java.util.List.of("食費", "日用品", "交通費", "娯楽", "その他"),
                EntryType.EXPENSE.getCategories());
        assertEquals("支出", EntryType.EXPENSE.toString());
    }
}
