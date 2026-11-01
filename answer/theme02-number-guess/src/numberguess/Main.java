package numberguess;

import javax.swing.SwingUtilities;

/**
 * 数当てゲームを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
