package janken;

/**
 * 1回分のじゃんけんの内容（自分の手・コンピュータの手・結果）をまとめたレコード。
 */
public record Round(Hand player, Hand computer, Result result) {
}
