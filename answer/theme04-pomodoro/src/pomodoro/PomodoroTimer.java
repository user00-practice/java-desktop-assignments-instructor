package pomodoro;

import java.time.Duration;

/**
 * 残り時間・現在のモード（作業／休憩）・完了回数の管理を担当するクラス。
 * 1秒ごとに tick() を呼んでもらう前提で、時間を計る仕組み（Timer）は画面側が持つ。
 */
public class PomodoroTimer {

    private Mode mode = Mode.WORK;
    private int remainingSeconds = mode.getSeconds();
    private int completedCount;

    /**
     * 残り時間を1秒減らす。（F4-01）
     * 時間になったらモードを切り替えて、終わったモードを返す。それ以外は null を返す。（F4-04）
     */
    public Mode tick() {
        remainingSeconds--;
        if (remainingSeconds > 0) {
            return null;
        }
        Mode finished = mode;
        if (finished == Mode.WORK) {
            completedCount++;                 // F4-06：作業が終わったら回数を増やす
        }
        mode = mode.next();
        remainingSeconds = mode.getSeconds();
        return finished;
    }

    /** F4-03：現在のモードの最初の時間に戻す。 */
    public void reset() {
        remainingSeconds = mode.getSeconds();
    }

    /** 残り時間を「分:秒」の形式（例：24:59）で返す。 */
    public String getRemainingText() {
        Duration duration = Duration.ofSeconds(remainingSeconds);
        return String.format("%02d:%02d", duration.toMinutes(), duration.toSecondsPart());
    }

    public Mode getMode() {
        return mode;
    }

    public int getCompletedCount() {
        return completedCount;
    }
}
