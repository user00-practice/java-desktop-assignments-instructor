package memo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * テキストファイルの読み込み・保存と、開いているファイル・未保存状態の管理を担当するクラス。
 */
public class MemoFile {

    /** 開いているファイル。新規の文書なら null */
    private Path path;
    /** 最後に保存してから内容が変更されたかどうか */
    private boolean modified;

    /** F7-01：新規の文書にする。 */
    public void newDocument() {
        path = null;
        modified = false;
    }

    /** F7-02：ファイルを読み込み、その内容を返す。 */
    public String open(Path file) throws IOException {
        String text = Files.readString(file);
        path = file;
        modified = false;
        return text;
    }

    /** F7-03：開いているファイルに上書き保存する。新規の文書なら呼ばないこと。 */
    public void save(String text) throws IOException {
        Files.writeString(path, text);
        modified = false;
    }

    /** F7-04：指定したファイルに保存し、以後はそのファイルを開いている状態にする。 */
    public void saveAs(Path file, String text) throws IOException {
        Files.writeString(file, text);
        path = file;
        modified = false;
    }

    /** 新規の文書（まだ一度も保存していない）かどうか。 */
    public boolean isNew() {
        return path == null;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /** タイトルバーに出すファイル名を返す。新規なら「無題」。 */
    public String getFileName() {
        return path == null ? "無題" : path.getFileName().toString();
    }
}
