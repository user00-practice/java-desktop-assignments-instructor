package todo;

/**
 * ToDo 1件（内容と、完了したかどうか）を表すレコード。
 * レコードは値を変更できないので、完了状態を変えるときは新しいレコードを作る。
 */
public record Todo(String title, boolean done) {

    /** 完了状態を反対にした、新しい Todo を返す。 */
    public Todo toggle() {
        return new Todo(title, !done);
    }

    /** 一覧に表示する文字列を返す。完了したものは先頭に「[済]」を付ける。 */
    public String toDisplayText() {
        return done ? "[済] " + title : title;
    }

    /** ファイルに保存する1行（例：「1<タブ>買い物」）を返す。 */
    public String toLine() {
        return (done ? "1" : "0") + "\t" + title;
    }

    /** ファイルの1行から Todo を作る。 */
    public static Todo fromLine(String line) {
        String[] parts = line.split("\t", 2);
        if (parts.length < 2) {
            return new Todo(line, false);     // 形式が違う行は、未完了の項目として読む
        }
        return new Todo(parts[1], parts[0].equals("1"));
    }
}
