package todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TodoList（一覧の管理と保存）のテスト。
 * 一時フォルダの todo.txt を使い、本物のデータファイルには触れない。
 */
class TodoListTest {

    @TempDir
    Path tempDir;

    private Path file;
    private TodoList todoList;

    @BeforeEach
    void setUp() throws IOException {
        file = tempDir.resolve("todo.txt");
        todoList = new TodoList(file);
        todoList.load();
    }

    @Test
    @DisplayName("F6-08：ファイルが無ければ空の一覧で始まる")
    void loadWithoutFile() {
        assertEquals(List.of(), todoList.getItems());
    }

    @Test
    @DisplayName("F6-01：追加した順に末尾へ並ぶ")
    void add() throws IOException {
        todoList.add("買い物");
        todoList.add("掃除");
        assertEquals(List.of(new Todo("買い物", false), new Todo("掃除", false)), todoList.getItems());
    }

    @Test
    @DisplayName("F6-03：完了を2回切り替えると未完了に戻る")
    void toggleTwice() throws IOException {
        todoList.add("買い物");
        todoList.toggle(0);
        assertEquals(true, todoList.getItems().get(0).done());
        todoList.toggle(0);
        assertEquals(false, todoList.getItems().get(0).done());
    }

    @Test
    @DisplayName("F6-04：指定した項目だけが削除される")
    void remove() throws IOException {
        todoList.add("A");
        todoList.add("B");
        todoList.add("C");
        todoList.remove(1);
        assertEquals(List.of(new Todo("A", false), new Todo("C", false)), todoList.getItems());
    }

    @Test
    @DisplayName("F6-06：未完了の件数を数える")
    void countUndone() throws IOException {
        todoList.add("A");
        todoList.add("B");
        todoList.add("C");
        todoList.toggle(1);
        assertEquals(2, todoList.countUndone());
    }

    @Test
    @DisplayName("F6-07・F6-08：保存した内容と完了状態を、別のインスタンスで読み込める（再起動の代わり）")
    void persistence() throws IOException {
        todoList.add("買い物");
        todoList.add("掃除");
        todoList.toggle(0);

        TodoList reopened = new TodoList(file);
        reopened.load();
        assertEquals(List.of(new Todo("買い物", true), new Todo("掃除", false)), reopened.getItems());
    }
}
