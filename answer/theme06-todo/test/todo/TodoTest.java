package todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Todo（ToDo 1件のレコード）のテスト。
 */
class TodoTest {

    @Test
    @DisplayName("toggle は新しい Todo を返し、元の Todo は変わらない")
    void toggleIsImmutable() {
        Todo original = new Todo("買い物", false);
        Todo toggled = original.toggle();
        assertTrue(toggled.done());
        assertFalse(original.done());
        assertEquals("買い物", toggled.title());
    }

    @Test
    @DisplayName("完了したものは先頭に「[済]」が付く")
    void displayText() {
        assertEquals("買い物", new Todo("買い物", false).toDisplayText());
        assertEquals("[済] 買い物", new Todo("買い物", true).toDisplayText());
    }

    @Test
    @DisplayName("保存用の1行に変換して、元に戻せる")
    void lineRoundTrip() {
        Todo todo = new Todo("レポートを書く", true);
        assertEquals("1\tレポートを書く", todo.toLine());
        assertEquals(todo, Todo.fromLine(todo.toLine()));
    }

    @Test
    @DisplayName("形式が違う行は、未完了の項目として読む")
    void fromInvalidLine() {
        assertEquals(new Todo("タブなし", false), Todo.fromLine("タブなし"));
    }
}
