package paint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DrawingPanel（マウス操作・元に戻す・全消去）のテスト。
 * 画面は表示せず、マウスの操作をプログラムから送って確かめる。
 */
class DrawingPanelTest {

    private DrawingPanel panel;

    @BeforeEach
    void setUp() {
        panel = new DrawingPanel();
    }

    /** マウスのイベントを1つ送る。 */
    private void send(int id, int x, int y) {
        panel.dispatchEvent(new MouseEvent(panel, id, System.currentTimeMillis(), 0, x, y, 1, false,
                MouseEvent.BUTTON1));
    }

    /** (x1, y1) から (x2, y2) までドラッグする。 */
    private void drag(int x1, int y1, int x2, int y2) {
        send(MouseEvent.MOUSE_PRESSED, x1, y1);
        send(MouseEvent.MOUSE_DRAGGED, x2, y2);
        send(MouseEvent.MOUSE_RELEASED, x2, y2);
    }

    @Test
    @DisplayName("最初は何も描かれていない")
    void initiallyEmpty() {
        assertTrue(panel.isEmpty());
    }

    @Test
    @DisplayName("F10-02：ドラッグすると図形が1つ増える")
    void dragAddsShape() {
        drag(10, 10, 50, 50);
        assertFalse(panel.isEmpty());
    }

    @Test
    @DisplayName("ドラッグせずにクリックしただけでは、図形は増えない")
    void clickDoesNotAddShape() {
        send(MouseEvent.MOUSE_PRESSED, 10, 10);
        send(MouseEvent.MOUSE_RELEASED, 10, 10);
        assertTrue(panel.isEmpty());
    }

    @Test
    @DisplayName("F10-06：元に戻すと、新しい順に1つずつ消える")
    void undo() {
        drag(10, 10, 50, 50);
        drag(60, 60, 90, 90);
        panel.undo();
        assertFalse(panel.isEmpty());
        panel.undo();
        assertTrue(panel.isEmpty());
        panel.undo();                       // 何も無いときに押しても例外にならない
        assertTrue(panel.isEmpty());
    }

    @Test
    @DisplayName("F10-07：全消去ですべての図形が消える")
    void clear() {
        drag(10, 10, 50, 50);
        drag(60, 60, 90, 90);
        panel.clear();
        assertTrue(panel.isEmpty());
    }
}
