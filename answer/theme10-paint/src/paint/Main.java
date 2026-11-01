package paint;

import javax.swing.SwingUtilities;

/**
 * 簡易お絵描きアプリを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
