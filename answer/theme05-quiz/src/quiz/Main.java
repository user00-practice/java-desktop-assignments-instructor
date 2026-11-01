package quiz;

import javax.swing.SwingUtilities;

/**
 * クイズアプリを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
