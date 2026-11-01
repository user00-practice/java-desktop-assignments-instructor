<p align="center"><img src="docs/images/class_logo.png" alt="クラスロゴ" width="200"></p>

# Java デスクトップアプリ課題（講師用リポジトリ）

Java SE 25 と Swing で作るデスクトップアプリ課題（全10テーマ）の教材一式です。このリポジトリは**講師専用**です。`answer/` フォルダには、受講者に見せない解答例が入っています。

## 仕組み：2つのリポジトリへの反映

```
講師用リポジトリ（このリポジトリ）
   │
   ├─ main へ push するたびに自動実行（sync-to-student-repo.yml）
   │     answer/ を除外してコピー、STUDENT_README.md → README.md
   │     ▼
   │   受講者用リポジトリ：課題のスターター
   │
   └─ Actionsタブから手動実行したときだけ（publish-answer-repo.yml）
         answer/ の中身だけをコピー、answer/PUBLIC_README.md → README.md
         ▼
       解答例リポジトリ：解答例と自動テスト
```

`answer/` 以外のファイルを main ブランチに push すると、受講者用リポジトリへ自動的にミラーリングされます。解答例リポジトリへの公開は、講師が Actions タブで「公開してよい」にチェックを入れて実行したときだけ行われます。どちらも、受講者はクローンして使います（手動編集はしない）。セットアップ手順は [SETUP.md](./SETUP.md) を参照してください。

## フォルダ構成

| フォルダ・ファイル | 内容 | 受講者用リポジトリへの同期 |
| --- | --- | --- |
| `theme01-janken/` 〜 `theme10-paint/` | 受講者が実装するスターター（要求仕様書の README.md つき） | される |
| `.idea/` | スターター用のプロジェクト構成と実行構成 | される |
| `docs/images/` | クラスロゴ | される |
| `.gitignore` | Gitの除外設定 | される |
| `STUDENT_README.md` | 受講者用リポジトリの README.md として配置される | される（README.md にリネームして） |
| `answer/` | 講師専用。10テーマの解答例と自動テスト（IntelliJで単独のプロジェクトとして開く） | **されない**（解答例リポジトリへは手動で公開。`answer/README.md` は送られず、`answer/PUBLIC_README.md` が README.md になる） |
| `README.md`（この文書）・`SETUP.md` | 講師向けの説明 | **されない** |

解答例とテストの開き方、指導のポイントは [answer/README.md](./answer/README.md) にあります。

## 動作確認環境

- Java SE 25 以降、IntelliJ IDEA、Swing（追加ライブラリなし）
