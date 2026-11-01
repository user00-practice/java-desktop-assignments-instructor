package memo;

import javax.swing.SwingUtilities;

/**
 * 簡易メモ帳を起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
