package mathtest;


import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryRecord {
    private Date timestamp;
    private int score;
    private int total;
    private String operationType;
    private int digitCount;
    private boolean isTimeout;

    public HistoryRecord(Date timestamp, int score, int total, String operationType, int digitCount, boolean isTimeout) {
        this.timestamp = timestamp;
        this.score = score;
        this.total = total;
        this.operationType = operationType;
        this.digitCount = digitCount;
        this.isTimeout = isTimeout;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String status = isTimeout ? "时间到自动提交" : "提前提交";
        return String.format("%s | 得分: %d/%d | %s | %d位%s",
                dateFormat.format(timestamp),
                score,
                total,
                status,
                digitCount,
                operationType);
    }

    // Getters (如果需要)
    public Date getTimestamp() { return timestamp; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getOperationType() { return operationType; }
    public int getDigitCount() { return digitCount; }
    public boolean isTimeout() { return isTimeout; }
}
