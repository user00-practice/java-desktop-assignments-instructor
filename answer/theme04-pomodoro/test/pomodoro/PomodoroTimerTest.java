package pomodoro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * PomodoroTimer（残り時間とモード切り替え）のテスト。
 * 実際に25分待たなくても、tick() を必要な回数だけ呼べば確かめられる。
 */
class PomodoroTimerTest {

    private PomodoroTimer timer;

    @BeforeEach
    void setUp() {
        timer = new PomodoroTimer();
    }

    /** tick() を指定した回数呼び、最後の戻り値を返す。 */
    private Mode tickTimes(int times) {
        Mode last = null;
        for (int i = 0; i < times; i++) {
            last = timer.tick();
        }
        return last;
    }

    @Test
    @DisplayName("最初は作業モードで 25:00")
    void initialState() {
        assertEquals(Mode.WORK, timer.getMode());
        assertEquals("25:00", timer.getRemainingText());
        assertEquals(0, timer.getCompletedCount());
    }

    @Test
    @DisplayName("F4-01：1秒進むと 24:59 になり、時間の途中では null が返る")
    void tickOneSecond() {
        assertNull(timer.tick());
        assertEquals("24:59", timer.getRemainingText());
    }

    @Test
    @DisplayName("F4-04・F4-06：作業時間が終わると休憩になり、完了回数が1増える")
    void workToBreak() {
        Mode finished = tickTimes(Mode.WORK.getSeconds());
        assertEquals(Mode.WORK, finished);
        assertEquals(Mode.BREAK, timer.getMode());
        assertEquals("05:00", timer.getRemainingText());
        assertEquals(1, timer.getCompletedCount());
    }

    @Test
    @DisplayName("F4-04：休憩が終わると作業に戻る。休憩では完了回数は増えない")
    void breakToWork() {
        tickTimes(Mode.WORK.getSeconds());
        Mode finished = tickTimes(Mode.BREAK.getSeconds());
        assertEquals(Mode.BREAK, finished);
        assertEquals(Mode.WORK, timer.getMode());
        assertEquals(1, timer.getCompletedCount());
    }

    @Test
    @DisplayName("F4-03：リセットすると、今のモードの最初の時間に戻る")
    void reset() {
        tickTimes(Mode.WORK.getSeconds() + 10);   // 休憩に入って10秒
        timer.reset();
        assertEquals(Mode.BREAK, timer.getMode());
        assertEquals("05:00", timer.getRemainingText());
    }

    @Test
    @DisplayName("Mode.next：作業の次は休憩、休憩の次は作業")
    void modeNext() {
        assertEquals(Mode.BREAK, Mode.WORK.next());
        assertEquals(Mode.WORK, Mode.BREAK.next());
    }
}
