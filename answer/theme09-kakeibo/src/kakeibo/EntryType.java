package kakeibo;

import java.util.List;

/**
 * 記録の区分（収入・支出）と、区分ごとに選べる費目を表す列挙型。
 * 名前を Type にしないこと：JFrame を継承したクラスの中では、Type が
 * java.awt.Window.Type（ウィンドウの種類）のことだと解釈されてしまい、コンパイルエラーになる。
 */
public enum EntryType {
    INCOME("収入", List.of("給料", "その他")),
    EXPENSE("支出", List.of("食費", "日用品", "交通費", "娯楽", "その他"));

    private final String label;
    private final List<String> categories;

    EntryType(String label, List<String> categories) {
        this.label = label;
        this.categories = categories;
    }

    /** F9-03：この区分で選べる費目の一覧を返す。 */
    public List<String> getCategories() {
        return categories;
    }

    /** プルダウンや表に表示される文字列。 */
    @Override
    public String toString() {
        return label;
    }
}
