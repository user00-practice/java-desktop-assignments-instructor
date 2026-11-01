package quiz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 問題ファイルの読み込み・出題順の管理・正誤判定と採点を担当するクラス。
 */
public class QuizManager {

    private final List<Question> questions = new ArrayList<>();
    private final List<Question> mistakes = new ArrayList<>();
    private int index;
    private int correctCount;

    /**
     * F5-01：問題ファイルを読み込む。空行は読み飛ばす。
     * ファイルが無いときは IOException、形式が違うときは IllegalArgumentException を投げる。
     */
    public void load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        questions.clear();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                questions.add(Question.fromCsv(line));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException((i + 1) + "行目の形式が正しくありません", e);
            }
        }
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("問題が1問もありません");
        }
    }

    /** F5-02・F5-07：出題順をシャッフルして、最初から始める。 */
    public void start() {
        Collections.shuffle(questions);
        mistakes.clear();
        index = 0;
        correctCount = 0;
    }

    /** 今の問題を返す。 */
    public Question current() {
        return questions.get(index);
    }

    /** F5-04：選んだ答えが正解かどうかを判定し、採点する。 */
    public boolean answer(String choice) {
        Question q = current();
        boolean correct = q.answer().equals(choice);
        if (correct) {
            correctCount++;
        } else {
            mistakes.add(q);
        }
        return correct;
    }

    /** 次の問題に進む。 */
    public void next() {
        index++;
    }

    /** 全問終わったかどうか。 */
    public boolean isFinished() {
        return index >= questions.size();
    }

    /** 今が何問目か（1から数える）。 */
    public int getNumber() {
        return index + 1;
    }

    public int getTotal() {
        return questions.size();
    }

    public int getCorrectCount() {
        return correctCount;
    }

    /** F5-05：正答率（%）を返す。 */
    public double getCorrectRate() {
        return correctCount * 100.0 / questions.size();
    }

    public List<Question> getMistakes() {
        return List.copyOf(mistakes);
    }
}
