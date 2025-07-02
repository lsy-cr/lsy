package mathtest;


import mathtest.HistoryRecord;
import mathtest.HistoryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HistoryPanel extends JPanel {
    private MainFrame mainFrame;
    private HistoryService historyService;
    private JTextArea historyArea;

    public HistoryPanel(MainFrame mainFrame, HistoryService historyService) {
        this.mainFrame = mainFrame;
        this.historyService = historyService;
        initUI();
        updateHistoryDisplay();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 历史记录显示区域
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 返回按钮
        JButton backBtn = new JButton("返回主菜单");
        backBtn.addActionListener(e -> mainFrame.showMainPanel());

        // 删除历史记录按钮
        JButton deleteHistoryBtn = new JButton("删除历史记录");
        deleteHistoryBtn.addActionListener(e -> deleteHistory());

        // 添加组件
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(backBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(deleteHistoryBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateHistoryDisplay() {
        List<HistoryRecord> records = historyService.getHistoryRecords();
        if (records.isEmpty()) {
            historyArea.setText("暂无历史记录");
        } else {
            StringBuilder sb = new StringBuilder();
            for (HistoryRecord record : records) {
                sb.append(record).append("\n\n");
            }
            historyArea.setText(sb.toString());
        }
        historyArea.setCaretPosition(0); // 滚动到顶部
    }

    private void deleteHistory() {
        List<HistoryRecord> records = historyService.getHistoryRecords();
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "当前没有可删除的历史记录",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "确定要删除所有历史记录吗？此操作不可恢复！",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            historyService.clearHistory();
            updateHistoryDisplay();
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "已成功删除所有历史记录",
                    "操作成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
