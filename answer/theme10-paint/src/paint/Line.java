package paint;

import java.awt.Color;

/**
 * 直線（始点と終点）を表すレコード。
 */
public record Line(int x1, int y1, int x2, int y2, Color color) implements Shape {
}
