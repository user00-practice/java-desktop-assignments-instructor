package paint;

import java.awt.Color;

/**
 * 円（楕円）を表すレコード。位置と大きさは、円がぴったり入る四角形で表す。
 */
public record Oval(int x, int y, int w, int h, Color color) implements Shape {
}
