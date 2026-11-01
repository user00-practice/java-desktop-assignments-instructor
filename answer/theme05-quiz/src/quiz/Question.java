package quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 1問分のデータ（問題文・正解・不正解の選択肢）を表すレコード。
 */
public record Question(String text, String answer, List<String> wrongs) {

    /**
     * CSVの1行（問題文,正解,不正解1,不正解2,不正解3）から Question を作る。
     * 列の数が5つでなければ IllegalArgumentException を投げる。
     */
    public static Question fromCsv(String line) {
        String[] cols = line.split(",");
        if (cols.length != 5) {
            throw new IllegalArgumentException("列の数が5つではありません：" + line);
        }
        return new Question(cols[0].trim(), cols[1].trim(),
                List.of(cols[2].trim(), cols[3].trim(), cols[4].trim()));
    }

    /** 正解と不正解を混ぜて、ランダムな順番に並べた4つの選択肢を返す。（F5-03） */
    public List<String> shuffledChoices() {
        List<String> choices = new ArrayList<>();
        choices.add(answer);
        choices.addAll(wrongs);
        Collections.shuffle(choices);
        return choices;
    }
}
