package janken;

import javax.swing.SwingUtilities;

/**
 * じゃんけんゲームを起動するクラス。
 * 画面（MainFrame）を作って表示するだけの役割なので、基本的に変更しなくてよい。
 */
public class Main {

    public static void main(String[] args) {
        // Swingの画面は、専用のスレッド（イベントディスパッチスレッド）で作るのが決まりごと
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
