package calculator;

import javax.swing.SwingUtilities;

/**
 * 電卓を起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
