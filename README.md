<p align="center"><img src="docs/images/class_logo.png" alt="クラスロゴ" width="200"></p>

# Java デスクトップアプリ課題（講師用リポジトリ）

Java SE 25 と Swing で作るデスクトップアプリ課題（全10テーマ）の教材一式です。このリポジトリは**講師専用**です。`answer/` フォルダには、受講者に見せない解答例が入っています。

## 仕組み：受講者用リポジトリへの自動同期

```
講師用リポジトリ（このリポジトリ）
      │ main へ push
      ▼
GitHub Actions（.github/workflows/sync-to-student-repo.yml）
      │ answer/ を除外してコピー
      │ STUDENT_README.md → README.md にリネーム
      ▼
受講者用リポジトリ（自動で反映される。手動編集しない）
```

`answer/` 以外のファイルを main ブランチに push すると、GitHub Actions が自動的に受講者用リポジトリへミラーリングします。受講者は受講者用リポジトリをクローンします。セットアップ手順は [SETUP.md](./SETUP.md) を参照してください。

## フォルダ構成

| フォルダ・ファイル | 内容 | 受講者用リポジトリへの同期 |
| --- | --- | --- |
| `theme01-janken/` 〜 `theme10-paint/` | 受講者が実装するスターター（要求仕様書の README.md つき） | される |
| `.idea/` | スターター用のプロジェクト構成と実行構成 | される |
| `docs/images/` | クラスロゴ | される |
| `.gitignore` | Gitの除外設定 | される |
| `STUDENT_README.md` | 受講者用リポジトリの README.md として配置される | される（README.md にリネームして） |
| `answer/` | 講師専用。10テーマの解答例（IntelliJで単独のプロジェクトとして開く） | **されない** |
| `README.md`（この文書）・`SETUP.md` | 講師向けの説明 | **されない** |

解答例の開き方と指導のポイントは [answer/README.md](./answer/README.md) にあります。

## 動作確認環境

- Java SE 25 以降、IntelliJ IDEA、Swing（追加ライブラリなし）
