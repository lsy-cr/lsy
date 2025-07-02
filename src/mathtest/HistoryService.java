package mathtest;

import mathtest.HistoryRecord;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryService {
    private static final String HISTORY_FILE = "math_test_history.txt";
    private List<HistoryRecord> historyRecords;

    public HistoryService() {
        this.historyRecords = new ArrayList<>();
        loadHistory();
    }

    // 加载历史记录
    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            System.out.println("历史记录文件不存在，将在首次保存时创建");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            historyRecords.clear(); // 清空现有记录
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    HistoryRecord record = parseHistoryRecord(line);
                    if (record != null) {
                        historyRecords.add(record);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("读取历史记录失败: " + e.getMessage());
        }
    }

    // 解析历史记录行
    private HistoryRecord parseHistoryRecord(String line) {
        try {
            // 示例行格式: 2025-07-02 15:30 | 得分: 8/10 | 提前提交 | 2位加法
            String[] parts = line.split(" \\| ");
            if (parts.length < 4) {
                System.err.println("无效的历史记录格式: " + line);
                return null;
            }

            // 解析日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date timestamp = dateFormat.parse(parts[0].trim());

            // 解析得分
            String scorePart = parts[1].trim();
            int colonIndex = scorePart.indexOf(':');
            if (colonIndex == -1) return null;

            String[] scoreParts = scorePart.substring(colonIndex + 1).trim().split("/");
            if (scoreParts.length != 2) return null;

            int score = Integer.parseInt(scoreParts[0].trim());
            int total = Integer.parseInt(scoreParts[1].trim());

            // 解析是否超时
            boolean isTimeout = parts[2].trim().contains("时间到自动提交");

            // 解析运算类型和数字位数
            String typeInfo = parts[3].trim();
            int digitCount = Character.getNumericValue(typeInfo.charAt(0));
            String operationType = typeInfo.substring(2);

            return new HistoryRecord(timestamp, score, total, operationType, digitCount, isTimeout);
        } catch (ParseException | NumberFormatException | StringIndexOutOfBoundsException e) {
            System.err.println("解析历史记录失败: " + line + " 错误: " + e.getMessage());
            return null;
        }
    }

    // 添加新的历史记录
    public void addHistoryRecord(HistoryRecord record) {
        historyRecords.add(0, record); // 添加到列表头部（最新记录在前）
        saveHistory();
    }

    // 保存历史记录到文件
    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (HistoryRecord record : historyRecords) {
                writer.write(record.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存历史记录失败: " + e.getMessage());
        }
    }

    // 获取所有历史记录
    public List<HistoryRecord> getHistoryRecords() {
        return new ArrayList<>(historyRecords); // 返回副本，防止外部修改
    }

    // 清除所有历史记录
    public void clearHistory() {
        historyRecords.clear();
        saveHistory(); // 保存空列表以清空文件
    }
}