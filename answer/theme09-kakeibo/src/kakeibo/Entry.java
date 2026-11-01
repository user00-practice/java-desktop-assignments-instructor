package kakeibo;

import java.time.LocalDate;

/**
 * 家計簿の記録1件を表すレコード。
 */
public record Entry(LocalDate date, EntryType type, String category, int amount, String memo) {

    /** CSVファイルに保存する1行を返す。（例：2026-11-01,EXPENSE,食費,850,昼ごはん） */
    public String toCsv() {
        return date + "," + type.name() + "," + category + "," + amount + "," + memo;
    }

    /** CSVファイルの1行から Entry を作る。形式が違うときは例外が発生する。 */
    public static Entry fromCsv(String line) {
        String[] cols = line.split(",", -1);   // -1：メモが空でも5列として読む
        return new Entry(
                LocalDate.parse(cols[0]),
                EntryType.valueOf(cols[1]),
                cols[2],
                Integer.parseInt(cols[3]),
                cols[4]);
    }
}
