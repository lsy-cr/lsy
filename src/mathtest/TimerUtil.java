package mathtest;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerUtil {
    private int timeLeft;
    private JLabel timerLabel;
    private Timer timer;
    private TimerCallback callback;

    public TimerUtil(int initialTime, JLabel timerLabel, TimerCallback callback) {
        this.timeLeft = initialTime;
        this.timerLabel = timerLabel;
        this.callback = callback;
        initTimer();
    }

    private void initTimer() {
        timer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                updateTimerLabel();

                if (timeLeft <= 0) {
                    timer.stop();
                    if (callback != null) {
                        callback.onTimeOut();
                    }
                }
            }
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void reset(int time) {
        timeLeft = time;
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        if (timerLabel != null) {
            timerLabel.setText(timeLeft + " 秒");
        }
    }

    public interface TimerCallback {
        void onTimeOut();
    }
}
