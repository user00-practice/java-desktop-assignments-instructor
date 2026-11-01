package paint;

import java.awt.Color;

/**
 * 描いた図形を表すインターフェース。
 * sealed（封印）にして、図形の種類を Line・Rect・Oval の3つだけに限定している。
 * そのため、switch で図形の種類ごとに分けるときに default が要らない。
 */
public sealed interface Shape permits Line, Rect, Oval {

    /** 図形の色。レコードの color 項目が、このメソッドの中身として自動的に使われる。 */
    Color color();
}
