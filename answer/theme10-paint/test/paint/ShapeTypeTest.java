package paint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ShapeType（2点から図形を作る処理）のテスト。
 */
class ShapeTypeTest {

    @Test
    @DisplayName("直線は、押した点と離した点をそのまま使う")
    void line() {
        Shape shape = ShapeType.LINE.create(100, 80, 20, 10, Color.RED);
        assertEquals(new Line(100, 80, 20, 10, Color.RED), shape);
    }

    @Test
    @DisplayName("四角形：左上から右下へドラッグ")
    void rectNormal() {
        assertEquals(new Rect(10, 20, 90, 60, Color.BLACK),
                ShapeType.RECT.create(10, 20, 100, 80, Color.BLACK));
    }

    @Test
    @DisplayName("四角形：右下から左上へドラッグしても、左上の位置と正の幅・高さになる")
    void rectReverse() {
        assertEquals(new Rect(10, 20, 90, 60, Color.BLACK),
                ShapeType.RECT.create(100, 80, 10, 20, Color.BLACK));
    }

    @Test
    @DisplayName("円：右上から左下へドラッグしても正しく作られる")
    void ovalReverse() {
        assertEquals(new Oval(10, 20, 90, 60, Color.BLUE),
                ShapeType.OVAL.create(100, 20, 10, 80, Color.BLUE));
    }

    @Test
    @DisplayName("どの図形も color() で色を取り出せる（sealed interface の共通メソッド）")
    void color() {
        for (ShapeType type : ShapeType.values()) {
            assertEquals(Color.GREEN, type.create(0, 0, 10, 10, Color.GREEN).color());
        }
    }
}
