package paint;

import java.awt.Color;

/**
 * 四角形（左上の位置・幅・高さ）を表すレコード。
 */
public record Rect(int x, int y, int w, int h, Color color) implements Shape {
}
