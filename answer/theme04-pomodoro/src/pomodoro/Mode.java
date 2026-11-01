package pomodoro;

/**
 * タイマーのモード（作業・休憩）と、それぞれの時間（秒）を表す列挙型。
 * 動作確認のときは、秒数を 10 などの短い値に書き換えて試すとよい。
 */
public enum Mode {
    WORK("作業中", 25 * 60),
    BREAK("休憩中", 5 * 60);

    private final String label;
    private final int seconds;

    Mode(String label, int seconds) {
        this.label = label;
        this.seconds = seconds;
    }

    public String getLabel() {
        return label;
    }

    public int getSeconds() {
        return seconds;
    }

    /** 次のモード（作業なら休憩、休憩なら作業）を返す。 */
    public Mode next() {
        return switch (this) {
            case WORK -> BREAK;
            case BREAK -> WORK;
        };
    }
}
