package kakeibo;

import javax.swing.SwingUtilities;

/**
 * 家計簿を起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
