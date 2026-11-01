package memo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MemoFile（ファイルの読み書きと未保存状態）のテスト。
 */
class MemoFileTest {

    @TempDir
    Path tempDir;

    private MemoFile memoFile;

    @BeforeEach
    void setUp() {
        memoFile = new MemoFile();
    }

    @Test
    @DisplayName("最初は新規の文書で、名前は「無題」、変更なし")
    void initialState() {
        assertTrue(memoFile.isNew());
        assertEquals("無題", memoFile.getFileName());
        assertFalse(memoFile.isModified());
    }

    @Test
    @DisplayName("F7-04：名前を付けて保存すると、日本語を含む内容がUTF-8で保存される")
    void saveAs() throws IOException {
        Path file = tempDir.resolve("メモ.txt");
        memoFile.setModified(true);
        memoFile.saveAs(file, "こんにちは\n2行目");

        assertEquals("こんにちは\n2行目", Files.readString(file));
        assertFalse(memoFile.isNew());
        assertFalse(memoFile.isModified());
        assertEquals("メモ.txt", memoFile.getFileName());
    }

    @Test
    @DisplayName("F7-03：一度保存したあとは、同じファイルに上書き保存される")
    void save() throws IOException {
        Path file = tempDir.resolve("a.txt");
        memoFile.saveAs(file, "1回目");
        memoFile.setModified(true);
        memoFile.save("2回目");

        assertEquals("2回目", Files.readString(file));
        assertFalse(memoFile.isModified());
    }

    @Test
    @DisplayName("F7-02：開くと内容が返り、変更なしの状態になる")
    void open() throws IOException {
        Path file = Files.writeString(tempDir.resolve("b.txt"), "中身");
        memoFile.setModified(true);

        assertEquals("中身", memoFile.open(file));
        assertEquals("b.txt", memoFile.getFileName());
        assertFalse(memoFile.isModified());
    }

    @Test
    @DisplayName("F7-08：存在しないファイルを開くと IOException")
    void openMissingFile() {
        assertThrows(IOException.class, () -> memoFile.open(tempDir.resolve("none.txt")));
    }

    @Test
    @DisplayName("F7-01：新規にすると、ファイル名と変更状態が最初に戻る")
    void newDocument() throws IOException {
        memoFile.saveAs(tempDir.resolve("c.txt"), "x");
        memoFile.setModified(true);
        memoFile.newDocument();

        assertTrue(memoFile.isNew());
        assertEquals("無題", memoFile.getFileName());
        assertFalse(memoFile.isModified());
    }
}
