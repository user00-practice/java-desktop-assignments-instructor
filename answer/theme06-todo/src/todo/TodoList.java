package todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ToDoの一覧の管理（追加・完了・削除）と、ファイルへの保存・読み込みを担当するクラス。
 * 一覧を変更するメソッドは、変更のたびにファイルへ保存する。（F6-07）
 */
public class TodoList {

    private final List<Todo> items = new ArrayList<>();
    private final Path file;

    public TodoList(Path file) {
        this.file = file;
    }

    /** F6-08：ファイルから読み込む。ファイルが無ければ空の一覧のまま。 */
    public void load() throws IOException {
        items.clear();
        if (!Files.exists(file)) {
            return;
        }
        for (String line : Files.readAllLines(file)) {
            if (!line.isBlank()) {
                items.add(Todo.fromLine(line));
            }
        }
    }

    /** F6-01：末尾に追加する。 */
    public void add(String title) throws IOException {
        items.add(new Todo(title, false));
        save();
    }

    /** F6-03：指定した番号の項目の完了状態を切り替える。 */
    public void toggle(int index) throws IOException {
        items.set(index, items.get(index).toggle());
        save();
    }

    /** F6-04：指定した番号の項目を削除する。 */
    public void remove(int index) throws IOException {
        items.remove(index);
        save();
    }

    /** F6-06：未完了の件数を返す。 */
    public long countUndone() {
        return items.stream().filter(t -> !t.done()).count();
    }

    public List<Todo> getItems() {
        return List.copyOf(items);
    }

    /** 一覧をファイルに保存する。 */
    private void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Todo todo : items) {
            sb.append(todo.toLine()).append("\n");
        }
        Files.writeString(file, sb.toString());
    }
}
