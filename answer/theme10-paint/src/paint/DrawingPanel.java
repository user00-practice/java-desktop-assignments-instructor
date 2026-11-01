package paint;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 描画領域。マウス操作の受け付けと、描いた図形の保持・描画を担当するクラス。
 * 注意：java.awt.* とまとめてインポートすると java.awt.Shape と名前がぶつかるので、
 * 使うクラスだけを1つずつインポートしている。
 */
public class DrawingPanel extends JPanel {

    /** F10-05：描いた図形をすべて覚えておくリスト */
    private final List<Shape> shapes = new ArrayList<>();
    /** F10-03：ドラッグ中に表示する図形（ドラッグしていなければ null） */
    private Shape preview;

    private ShapeType shapeType = ShapeType.LINE;
    private Color color = Color.BLACK;
    private int startX;
    private int startY;

    public DrawingPanel() {
        setBackground(Color.WHITE);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                preview = shapeType.create(startX, startY, e.getX(), e.getY(), color);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (preview == null) {
                    return;               // ドラッグせずにクリックしただけなら何も描かない
                }
                // F10-02：押した位置から離した位置までの範囲に図形を描く
                shapes.add(shapeType.create(startX, startY, e.getX(), e.getY(), color));
                preview = null;
                repaint();
            }
        };
        addMouseListener(mouse);          // 押した・離した
        addMouseMotionListener(mouse);    // ドラッグした
    }

    /**
     * 画面を描き直す必要があるとき（repaint したとき・ウィンドウの大きさを変えたときなど）に呼ばれる。
     * 毎回、覚えている図形をすべて描き直す。
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);          // 背景を白で塗りつぶす
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));

        for (Shape shape : shapes) {
            draw(g2, shape);
        }
        if (preview != null) {
            draw(g2, preview);
        }
    }

    /** 図形を1つ描く。図形の種類ごとの分岐はパターンマッチングの switch で書く。 */
    private void draw(Graphics2D g, Shape shape) {
        g.setColor(shape.color());
        switch (shape) {
            case Line l -> g.drawLine(l.x1(), l.y1(), l.x2(), l.y2());
            case Rect r -> g.drawRect(r.x(), r.y(), r.w(), r.h());
            case Oval o -> g.drawOval(o.x(), o.y(), o.w(), o.h());
        }
    }

    /** F10-06：最後に描いた図形を1つ消す。 */
    public void undo() {
        if (!shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
            repaint();
        }
    }

    /** F10-07：すべての図形を消す。 */
    public void clear() {
        shapes.clear();
        repaint();
    }

    public boolean isEmpty() {
        return shapes.isEmpty();
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
