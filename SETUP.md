# セットアップ手順（講師用リポジトリ → 受講者用リポジトリ 自動同期）

このリポジトリには GitHub Actions のワークフロー（`.github/workflows/sync-to-student-repo.yml`）が
すでに用意されています。以下の手順で、実際に動くように設定してください。

## 全体像

```
講師用リポジトリ（このリポジトリ）
      │ main へ push
      ▼
GitHub Actions
      │ answer/ を除外
      ▼
受講者用リポジトリへ自動反映
```

## 手順1：2つのリポジトリをGitHub上に作成する

1. GitHubで新規リポジトリを2つ作成します。
   - 講師用：例）`java-desktop-assignments-instructor`（**Private** 推奨）
   - 受講者用：例）`java-desktop-assignments-student`（Public / Private は運用に合わせて選択）
2. 受講者用リポジトリは、**必ず初回コミットを1つ入れた状態で作成する**
   （作成時に **Add a README file** にチェックを入れるのが一番簡単）。
   完全に空（コミット0件）のまま作成すると `main` ブランチ自体が存在せず、
   後述のワークフローが `couldn't find remote ref refs/heads/main` というエラーで確実に失敗する
   （実際に起きた事例あり）。初回コミットさえあれば中身はREADME.mdだけでよく、
   最初の同期実行時にGitHub Actionsが内容を丸ごと上書き・反映する。
3. 講師用リポジトリに、このフォルダの中身一式を push します。

```bash
cd java-desktop-assignments-instructor
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<あなたのアカウント>/java-desktop-assignments-instructor.git
git push -u origin main
```

IntelliJ IDEA を使う場合は、このフォルダを「ファイル」→「開く...」で開き、メニューの「Git」→「GitHub」→「GitHub でプロジェクトを共有」からも push できます（その場合はリポジトリ名を `java-desktop-assignments-instructor`、Private にする）。

## 手順2：受講者用リポジトリへの書き込み権限を持つトークンを作る

1. GitHubの **Settings → Developer settings → Personal access tokens → Fine-grained tokens** を開く
2. **Generate new token** をクリック
3. 以下を設定する
   - Token name：`java-desktop-assignments-student-sync` など分かりやすい名前
   - Expiration：運用に合わせて設定（学期ごとに更新するなら90日など）
   - Repository access：**Only select repositories** → 受講者用リポジトリだけを選択
   - **Permissions** の設定方法（2025年8月のUI変更後、実機で確認済みの手順）：
     Permissionsセクション右上の **+ Add permissions** ボタンを押し、「Repositories」タブから
     **Contents** を選んで追加する。追加されたカードの右側に **Access: Read-only** という
     ドロップダウンが出るので、これを **Read and write** に変更する（初期値のままだと書き込めない）。
     `Metadata`（Required）というカードも自動で追加されるが、これはRead-onlyのままでよい。
     ※GitHubの画面デザインは今後も変わりうるので、固定のボタン名を鵜呑みにせず、
     「Contents権限をRead and writeにする」という目的で探すこと。見つからない場合は、
     利用者が実際に見ている画面のスクリーンショットをもらって案内し直す。
4. **Generate token** を押し、表示されたトークンをコピーしておく

## 手順3：講師用リポジトリにシークレットとして登録する

1. 講師用リポジトリの **Settings → Secrets and variables → Actions** を開く
2. **New repository secret** をクリック
3. Name：`STUDENT_REPO_TOKEN`、Secret：手順2でコピーしたトークンを貼り付け
4. **Add secret** を押す

## 手順4：ワークフローの宛先リポジトリ名を書き換える

`.github/workflows/sync-to-student-repo.yml` の中の次の行を、実際のアカウント名・リポジトリ名に書き換える。

```yaml
repository: YOUR_ACCOUNT/java-desktop-assignments-student # ← ここを書き換える
```

## 手順5：動作確認

**重要な既知の注意点**：手順1の「初回コミット」（このフォルダの中身一式を最初に1回で push する
コミット）は、ワークフローファイル（`.github/workflows/sync-to-student-repo.yml`）自体を初めて
追加するコミットでもあります。GitHubには、**ワークフローファイルがブランチにまだ存在しない状態で
行われたpush（＝そのワークフローファイル自身を追加する最初のpush）では、Actionsが実行されない
（＝ワークフローファイルが「存在した後」のpushから初めてトリガーされる）**、という広く報告されている
挙動があります。そのため、**手順1のpush直後にActionsタブを見ても、ワークフローが自動的には
実行されていないのが正常**です。慌てず、以下のいずれかの方法で最初の1回だけ手動でキックしてください。

1. push後、Actionsタブの左メニューにワークフロー名（「受講者用リポジトリへ自動同期」）が
   表示されていることを確認する（表示されていれば、GitHubがワークフローファイル自体は認識できている）。
2. ワークフロー名をクリックして専用ページに移動し、右上の **Run workflow** ボタン
   （ブランチ選択つき）から手動実行する（`workflow_dispatch` 設定済みのため）。
   一覧ページ（「All workflows」）のままだとこのボタンは出てこないので注意。
3. これで実行されれば、以降は通常どおり `answer/` 以外のファイルを含むpushで
   自動的にトリガーされるようになる（ワークフローファイルが既にブランチ上に存在するため）。
4. 受講者用リポジトリを開き、演習用フォルダは反映されているのに `answer/` が
   **存在しないこと**、README.mdが受講者向けの内容（`STUDENT_README.md`の内容）になっていることを
   必ず目視で確認する。
5. 実行ログに「Node.js 20 is deprecated ...」という warning が出ることがあるが、
   これはActionsランナー側の情報メッセージで同期の成否とは無関係。無視してよい。

## トラブルシューティング

| 症状 | 主な原因・対処 |
|---|---|
| 初回コミット＆pushしたのにActionsが一切動かない | **既知の挙動**：ワークフローファイル自体を追加した最初のpushではトリガーされないことがある。Actionsタブでワークフロー名が表示されていることを確認し、上記手順5の手動実行（`workflow_dispatch`）で最初の1回をキックする |
| `couldn't find remote ref refs/heads/main`（Checkout student repo の段階） | 受講者用リポジトリにコミットが1つもなく`main`ブランチが存在しない。何かファイルを1つ追加してコミットしてから再実行する |
| Checkout student repo の段階で失敗（上記以外） | `STUDENT_REPO_TOKEN` が未設定、または権限不足。手順2・3を見直す |
| 「除外漏れがないことを確認」で失敗 | `answer/` 以外の場所に、名前に answer や teacher を含むファイルができている |
| Actionsタブにワークフロー自体が表示されない／2回目以降のpushでも動かない | (a) `on: push: branches: [main]` と実際のデフォルトブランチ名が一致しているか確認する。(b) リポジトリの **Settings → Actions → General → Actions permissions** で「Disable actions」になっていないか確認する（Organizationのポリシーで制限されている場合がある） |
| 反映されない（緑チェックだが変化なし） | `answer/` だけを変更して push した可能性が高い（意図通り） |
| 受講者用リポジトリの不要ファイルが消えない／消えすぎる | `rsync --delete` によるミラーリングの仕様。受講者用リポジトリを手動編集しない運用にする |

## 運用上の注意

- 受講者用リポジトリは**手動で直接編集しない**（次回の同期で上書き・削除される）。
- 解答例は教材作成時点のもの（作成環境のJDKは21）。配布前に必ずご自身の環境（JDK 25）で一度動作確認すること。
- 受講者のPCでJDKの名前が「25」以外の場合、IntelliJがJDKの設定を求める。受講者用READMEの「準備：JDKを設定する」で対応できる。

---

# 解答例リポジトリへの公開（課題終了後などに使う）

`answer/` の中身だけを、受講者がクローンできる「解答例リポジトリ」へ送る仕組みです。
受講者用リポジトリへの自動同期とは別で、**講師が Actions タブのボタンを押したときだけ**実行されます。
課題期間中に誤って公開されることはありません。

```
講師用リポジトリ（このリポジトリ）
      │ Actionsタブから手動実行（チェックを入れたときだけ）
      ▼
GitHub Actions（.github/workflows/publish-answer-repo.yml）
      │ answer/ の中身だけをコピー
      │ answer/PUBLIC_README.md → README.md（answer/README.md は講師向けなので送らない）
      ▼
解答例リポジトリ（受講者はこれをクローンする。手動編集しない）
```

## 手順A：解答例リポジトリを作成する

1. GitHubで新規リポジトリを作成します。例）`java-desktop-assignments-answer`
   - 公開範囲は受講者用リポジトリと同じ考え方で選んでください。
2. 受講者用リポジトリと同じく、**必ず初回コミットを1つ入れた状態で作成する**
   （作成時に **Add a README file** にチェックを入れる）。空のままだと公開時に
   `couldn't find remote ref refs/heads/main` というエラーで失敗します。

## 手順B：トークンの対象に解答例リポジトリを追加する

どちらか一方を行ってください。

- **方法1（簡単）**：手順2で作ったトークンの設定画面（Settings → Developer settings →
  Personal access tokens → Fine-grained tokens → 作成したトークン）を開き、
  Repository access に解答例リポジトリを追加して保存する。新しいシークレットの登録は不要です。
- **方法2（分けて管理したい場合）**：解答例リポジトリだけを対象にしたトークンを手順2と同じ方法で作り、
  講師用リポジトリのシークレットに `ANSWER_REPO_TOKEN` という名前で登録する。

ワークフローは `ANSWER_REPO_TOKEN` があればそれを使い、無ければ `STUDENT_REPO_TOKEN` を使います。

## 手順C：ワークフローの宛先リポジトリ名を書き換える

`.github/workflows/publish-answer-repo.yml` の次の行を、実際のアカウント名・リポジトリ名に書き換えて push します。

```yaml
repository: YOUR_ACCOUNT/java-desktop-assignments-answer # ← ここを書き換える
```

## 手順D：公開する

1. 講師用リポジトリの **Actions** タブを開く
2. 左のメニューから「解答例リポジトリへ公開」を選ぶ
3. 右上の **Run workflow** を押し、「解答例を受講者に公開してよい場合はチェックする」にチェックを入れて実行する
   （チェックを入れずに実行した場合は、何もせずに終わります）
4. 解答例リポジトリを開き、次の点を目視で確認する
   - `theme01-janken` 〜 `theme10-paint`、`.idea` があること
   - README.md が受講者向けの内容（「解答例」の説明と実行方法）になっていること
   - 「講師専用」「指導のポイント」といった記述が無いこと
5. 受講者に解答例リポジトリのURLを伝える。受講者は、課題のときと同じく「VCS から取得」でクローンします

解答例を修正した場合は、講師用リポジトリに push したあと、もう一度手順Dを行えば反映されます。

## トラブルシューティング（解答例リポジトリ）

| 症状 | 主な原因・対処 |
|---|---|
| 実行したのにすぐ終わり、何も反映されない | 「公開してよい」のチェックを入れずに実行した。チェックを入れて実行し直す |
| `couldn't find remote ref refs/heads/main` | 解答例リポジトリにコミットが1つもない。手順Aの2を行う |
| 解答例リポジトリのチェックアウトで失敗（上記以外） | トークンの対象に解答例リポジトリが入っていない。手順Bを見直す |
| 「講師向けの記述が無いことを確認」で失敗 | `answer/` の中（README.md 以外）に「講師専用」などの語が入っている。表示されたファイルを直す |
