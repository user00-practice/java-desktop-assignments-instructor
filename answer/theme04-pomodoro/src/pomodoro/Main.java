package pomodoro;

import javax.swing.SwingUtilities;

/**
 * ポモドーロタイマーを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
