package quiz;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * クイズアプリの画面を作るクラス。
 * 画面の部品（ボタン・ラベルなど）の配置と、操作されたときの処理の呼び出しを担当する。
 * 計算や判定などの処理そのものは QuizManager クラスに書く（共通要件 C-03）。
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        // C-01：タイトルバーにアプリ名を表示する
        setTitle("クイズアプリ");
        // C-02：ウィンドウの×ボタンでアプリを終了する
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ウィンドウの大きさ（幅, 高さ）。作りながら自由に変えてよい
        setSize(560, 420);
        // ウィンドウを画面の中央に表示する
        setLocationRelativeTo(null);

        // TODO: ここから画面を作る（README.md の「画面構成」「必須要件」を参照）
        //       作り始めたら、下の仮のラベルは削除してよい
        add(new JLabel("ここに画面を作ります", SwingConstants.CENTER));
    }
}
