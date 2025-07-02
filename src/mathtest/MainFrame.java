package mathtest;


import mathtest.HistoryService;
import mathtest.ProblemGenerator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private MainPanel mainPanel;
    private TestPanel testPanel;
    private HistoryPanel historyPanel;

    private ProblemGenerator problemGenerator;
    private HistoryService historyService;

    public MainFrame() {
        problemGenerator = new ProblemGenerator();
        historyService = new HistoryService();

        initUI();
    }

    private void initUI() {
        setTitle("数学自动出题系统");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new MainPanel(this);
        testPanel = new TestPanel(this, problemGenerator, historyService);
        historyPanel = new HistoryPanel(this, historyService);

        add(mainPanel, BorderLayout.CENTER);
    }

    // 新增：启动测试的方法
    public void startTest(String operationType, int digitCount) {
        testPanel.initializeTest(operationType, digitCount);
        showTestPanel();
    }

    public void showMainPanel() {
        getContentPane().removeAll();
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showTestPanel() {
        getContentPane().removeAll();
        add(testPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showHistoryPanel() {
        historyPanel.updateHistoryDisplay(); // 更新历史记录显示
        getContentPane().removeAll();
        add(historyPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
