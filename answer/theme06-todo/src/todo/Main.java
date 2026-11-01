package todo;

import javax.swing.SwingUtilities;

/**
 * ToDoリストを起動するクラス。
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
