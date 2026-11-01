package kakeibo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 記録の管理（登録・削除）・月ごとの集計・CSVへの保存と読み込みを担当するクラス。
 * 記録を変更するメソッドは、変更のたびにファイルへ保存する。（F9-09）
 */
public class Kakeibo {

    private final List<Entry> entries = new ArrayList<>();
    private final Path file;

    public Kakeibo(Path file) {
        this.file = file;
    }

    /** F9-09：ファイルから読み込む。ファイルが無ければ空のまま。 */
    public void load() throws IOException {
        entries.clear();
        if (!Files.exists(file)) {
            return;
        }
        for (String line : Files.readAllLines(file)) {
            if (!line.isBlank()) {
                entries.add(Entry.fromCsv(line));
            }
        }
    }

    /** F9-01：記録を追加する。メモの中のカンマはCSVが崩れるので「、」に置き換える。 */
    public void add(LocalDate date, EntryType type, String category, int amount, String memo)
            throws IOException {
        entries.add(new Entry(date, type, category, amount, memo.replace(",", "、")));
        save();
    }

    /** F9-08：記録を削除する。 */
    public void remove(Entry entry) throws IOException {
        entries.remove(entry);
        save();
    }

    /** F9-05：指定した月の記録を、日付の古い順に返す。 */
    public List<Entry> entriesOf(YearMonth month) {
        return entries.stream()
                .filter(e -> YearMonth.from(e.date()).equals(month))
                .sorted(Comparator.comparing(Entry::date))
                .toList();
    }

    /** F9-06：指定した月・区分の合計金額を返す。 */
    public int totalOf(YearMonth month, EntryType type) {
        return entriesOf(month).stream()
                .filter(e -> e.type() == type)
                .mapToInt(Entry::amount)
                .sum();
    }

    /** F9-07：指定した月の、費目ごとの支出合計を返す（費目の並びは EntryType の定義順）。 */
    public Map<String, Integer> expenseByCategory(YearMonth month) {
        Map<String, Integer> sums = entriesOf(month).stream()
                .filter(e -> e.type() == EntryType.EXPENSE)
                .collect(Collectors.groupingBy(Entry::category,
                        Collectors.summingInt(Entry::amount)));

        Map<String, Integer> ordered = new LinkedHashMap<>();
        for (String category : EntryType.EXPENSE.getCategories()) {
            ordered.put(category, sums.getOrDefault(category, 0));
        }
        return ordered;
    }

    /** 記録がある月と今月を、新しい順に並べて返す（月の選択肢に使う）。 */
    public List<YearMonth> months() {
        TreeSet<YearMonth> set = new TreeSet<>(Comparator.reverseOrder());
        set.add(YearMonth.now());
        for (Entry e : entries) {
            set.add(YearMonth.from(e.date()));
        }
        return new ArrayList<>(set);
    }

    /** 記録をファイルに保存する。 */
    private void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries) {
            sb.append(e.toCsv()).append("\n");
        }
        Files.writeString(file, sb.toString());
    }
}
