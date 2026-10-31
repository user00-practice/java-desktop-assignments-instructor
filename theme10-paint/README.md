# テーマ10 簡易お絵描き

マウスのドラッグで直線・四角形・円を描けるお絵描きアプリです。Graphics2Dによる描画、マウスイベント、sealed interface・record・パターンマッチングを組み合わせた設計を学びます。Java SE 25の新しい文法を最も活かせるテーマです。

## 実行方法

1. IntelliJ IDEA の右上にある実行構成の一覧から「テーマ10 簡易お絵描き」を選ぶ
2. 緑の実行ボタン（▶）を押す

スターターコードの状態では、タイトルだけが入った空のウィンドウが表示されます。ここに画面と機能を作っていきます。

## このフォルダの構成

```
theme10-paint/
├── README.md         … この要求仕様書
└── src/paint/
    ├── Main.java         … 起動用のクラス（基本的に変更不要）
    ├── MainFrame.java    … 画面を作るクラス（ここから作り始める）
    └── DrawingPanel.java … 処理を担当するクラス（中身は自分で書く）
```

必要に応じて、クラス（enum・record を含む）を `src/paint/` に追加してください。

## 画面構成

- 上部：ツールバー（図形の種類「直線」「四角形」「円」、色の選択、「元に戻す」「全消去」ボタン）
- 中央：描画領域（白い背景）

## 必須要件

| ID | 機能 | 内容 |
| --- | --- | --- |
| F10-01 | 図形の選択 | ツールバーで描く図形（直線・四角形・円）を選べる |
| F10-02 | 描画 | マウスを押した位置から離した位置までの範囲に、選んだ図形を描く |
| F10-03 | プレビュー | ドラッグ中は、描かれる予定の図形を表示し続ける |
| F10-04 | 色の選択 | 黒・赤・青・緑から描く色を選べる |
| F10-05 | 図形の保持 | 描いた図形はすべて残り、ウィンドウの大きさを変えても消えない |
| F10-06 | 元に戻す | 最後に描いた図形を1つ消す（何度でも押せる） |
| F10-07 | 全消去 | 確認後、すべての図形を消す |

## 実装上の条件

- 図形は sealed interface と record で表す（下の例）
- 描画処理は、パターンマッチングの switch で図形の種類ごとに分ける
- 描いた図形は `List<Shape>` に保存し、`paintComponent` で毎回すべて描き直す
- 描画領域は `JPanel` を継承したクラス（`DrawingPanel`）で作り、`MouseListener` と `MouseMotionListener` を使う
- 右から左、下から上へドラッグしても正しく描けるようにする（`Math.min`、`Math.abs` を使う）

```java
sealed interface Shape permits Line, Rect, Oval {}
record Line(int x1, int y1, int x2, int y2, Color color) implements Shape {}
record Rect(int x, int y, int w, int h, Color color) implements Shape {}
record Oval(int x, int y, int w, int h, Color color) implements Shape {}

// 描画処理の例
switch (shape) {
    case Line l -> g.drawLine(l.x1(), l.y1(), l.x2(), l.y2());
    case Rect r -> g.drawRect(r.x(), r.y(), r.w(), r.h());
    case Oval o -> g.drawOval(o.x(), o.y(), o.w(), o.h());
}
```

sealed interface で図形の種類が限定されているため、switch に default が不要になります。図形を追加して case を書き忘れると、コンパイルエラーで教えてくれる点が学習のポイントです。

`Shape` という名前は `java.awt.Shape` と同じです。`import java.awt.*;` のようにまとめてインポートすると名前がぶつかるため、`java.awt.Color` のように使うクラスだけを個別にインポートしてください。

## 拡張要件

- フリーハンド（ペン）ツールを追加する（新しい record を追加し、switch に case を足す）
- 線の太さを選べるようにする（`BasicStroke`）
- 描いた絵をPNG画像として保存する（`BufferedImage` と `ImageIO`）

## 受け入れ基準

- [ ] 直線・四角形・円がドラッグで描ける
- [ ] どの方向にドラッグしても図形が正しく描ける
- [ ] ウィンドウの大きさを変えても図形が消えない
- [ ] 「元に戻す」を押すたびに、新しい順に図形が1つずつ消える

## 全テーマ共通の要件

リポジトリ直下の [README.md](../README.md) の「全テーマ共通の要件」も満たしてください。
