package quiz;

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
 * QuizManager（読み込み・出題・採点）のテスト。
 * @TempDir で作られる一時フォルダにテスト用のCSVを書き、本物の quiz.csv は使わない。
 */
class QuizManagerTest {

    @TempDir
    Path tempDir;

    private Path writeCsv(String content) throws IOException {
        return Files.writeString(tempDir.resolve("quiz.csv"), content);
    }

    private QuizManager loadThreeQuestions() throws IOException {
        QuizManager manager = new QuizManager();
        manager.load(writeCsv("""
                Q1,A1,x,y,z
                Q2,A2,x,y,z

                Q3,A3,x,y,z
                """));
        return manager;
    }

    @Test
    @DisplayName("F5-01：空行を飛ばして全問を読み込む")
    void load() throws IOException {
        QuizManager manager = loadThreeQuestions();
        assertEquals(3, manager.getTotal());
    }

    @Test
    @DisplayName("F5-06：ファイルが無ければ IOException")
    void loadMissingFile() {
        QuizManager manager = new QuizManager();
        assertThrows(IOException.class, () -> manager.load(tempDir.resolve("none.csv")));
    }

    @Test
    @DisplayName("F5-06：形式が違う行があれば、行番号つきの IllegalArgumentException")
    void loadInvalidLine() throws IOException {
        QuizManager manager = new QuizManager();
        Path csv = writeCsv("Q1,A1,x,y,z\nQ2,A2\n");
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> manager.load(csv));
        assertEquals("2行目の形式が正しくありません", e.getMessage());
    }

    @Test
    @DisplayName("F5-06：問題が1問も無ければ IllegalArgumentException")
    void loadEmpty() throws IOException {
        QuizManager manager = new QuizManager();
        Path csv = writeCsv("\n\n");
        assertThrows(IllegalArgumentException.class, () -> manager.load(csv));
    }

    @Test
    @DisplayName("F5-04・F5-05：正解数・正答率・間違えた問題が正しく記録される")
    void scoring() throws IOException {
        QuizManager manager = loadThreeQuestions();
        manager.start();

        assertTrue(manager.answer(manager.current().answer()));   // 1問目：正解
        manager.next();
        String missed = manager.current().text();
        assertFalse(manager.answer("x"));                          // 2問目：不正解
        manager.next();
        assertTrue(manager.answer(manager.current().answer()));   // 3問目：正解
        manager.next();

        assertTrue(manager.isFinished());
        assertEquals(2, manager.getCorrectCount());
        assertEquals(200.0 / 3, manager.getCorrectRate(), 0.001);
        assertEquals(1, manager.getMistakes().size());
        assertEquals(missed, manager.getMistakes().get(0).text());
    }

    @Test
    @DisplayName("問題番号は1から数える")
    void number() throws IOException {
        QuizManager manager = loadThreeQuestions();
        manager.start();
        assertEquals(1, manager.getNumber());
        manager.next();
        assertEquals(2, manager.getNumber());
    }

    @Test
    @DisplayName("F5-07：start でやり直すと、採点がリセットされる")
    void restart() throws IOException {
        QuizManager manager = loadThreeQuestions();
        manager.start();
        manager.answer("x");
        manager.next();

        manager.start();
        assertEquals(1, manager.getNumber());
        assertEquals(0, manager.getCorrectCount());
        assertEquals(0, manager.getMistakes().size());
    }

    @Test
    @DisplayName("配布している quiz.csv（10問）を正しく読み込める")
    void loadBundledFile() throws IOException {
        // テストの作業フォルダは theme05-quiz フォルダ
        QuizManager manager = new QuizManager();
        manager.load(Path.of("quiz.csv"));
        assertEquals(10, manager.getTotal());
    }
}
