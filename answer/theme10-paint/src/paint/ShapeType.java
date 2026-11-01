package paint;

import java.awt.Color;

/**
 * ツールバーで選ぶ図形の種類を表す列挙型。
 * マウスを押した位置と離した位置から、その種類の図形を作る。
 */
public enum ShapeType {
    LINE("直線"),
    RECT("四角形"),
    OVAL("円");

    private final String label;

    ShapeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 2つの点から図形を作る。
     * 右から左・下から上へドラッグしても正しく描けるように、
     * 四角形と円は「小さいほうの座標」を左上にし、幅と高さは絶対値にする。
     */
    public Shape create(int x1, int y1, int x2, int y2, Color color) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int w = Math.abs(x2 - x1);
        int h = Math.abs(y2 - y1);
        return switch (this) {
            case LINE -> new Line(x1, y1, x2, y2, color);
            case RECT -> new Rect(x, y, w, h, color);
            case OVAL -> new Oval(x, y, w, h, color);
        };
    }
}
