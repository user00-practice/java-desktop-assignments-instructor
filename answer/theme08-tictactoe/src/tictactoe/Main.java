package tictactoe;

import javax.swing.SwingUtilities;

/**
 * 三目並べを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
