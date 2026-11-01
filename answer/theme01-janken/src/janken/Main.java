package janken;

import javax.swing.SwingUtilities;

/**
 * じゃんけんゲームを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
