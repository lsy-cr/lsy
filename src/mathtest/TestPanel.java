package mathtest;


import mathtest.Problem;
import mathtest.CalculationService;
import mathtest.HistoryService;
import mathtest.ProblemGenerator;
import mathtest.TimerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestPanel extends JPanel {
    private MainFrame mainFrame;
    private ProblemGenerator problemGenerator;
    private HistoryService historyService;
    private CalculationService calculationService;

    private JLabel timerLabel;
    private JPanel questionPanel;
    private List<Problem> problems;
    private List<JTextField> answerFields;
    private List<JLabel> questionLabels;
    private TimerUtil timerUtil;
    private boolean isTesting;
    private String operationType;
    private int digitCount;

    public TestPanel(MainFrame mainFrame, ProblemGenerator problemGenerator,
                     HistoryService historyService) {
        this.mainFrame = mainFrame;
        this.problemGenerator = problemGenerator;
        this.historyService = historyService;
        this.calculationService = new CalculationService();
        this.answerFields = new ArrayList<>();
        this.questionLabels = new ArrayList<>();
        this.isTesting = false;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 顶部操作按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("返回主菜单");
        backBtn.addActionListener(e -> mainFrame.showMainPanel());
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // 问题显示面板
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void initializeTest(String operationType, int digitCount) {
        this.operationType = operationType;
        this.digitCount = digitCount;
        this.isTesting = true;

        // 清空之前的内容
        questionPanel.removeAll();
        answerFields.clear();
        questionLabels.clear();
        problems = null;

        // 生成10道题目
        problems = problemGenerator.generateProblems(operationType, digitCount, 10);

        // 添加计时器
        JPanel timerPanel = new JPanel();
        timerPanel.add(new JLabel("剩余时间:"));
        timerLabel = new JLabel("120 秒");
        timerLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        timerPanel.add(timerLabel);
        questionPanel.add(timerPanel);

        // 显示题目和答案输入框
        for (int i = 0; i < problems.size(); i++) {
            Problem problem = problems.get(i);
            JLabel qLabel = new JLabel((i + 1) + ". " + problem.getQuestion() + " = ");
            qLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            JTextField answerField = new JTextField(10);
            answerField.setFont(new Font("微软雅黑", Font.PLAIN, 16));

            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.add(qLabel);
            rowPanel.add(answerField);
            questionPanel.add(rowPanel);

            questionLabels.add(qLabel);
            answerFields.add(answerField);
        }

        // 添加提交按钮
        JButton submitBtn = new JButton("提交");
        submitBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        submitBtn.addActionListener(e -> submitTest(false));
        questionPanel.add(submitBtn);

        // 启动计时器
        startTimer();

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void startTimer() {
        timerUtil = new TimerUtil(120, timerLabel, new TimerUtil.TimerCallback() {

            public void onTimeOut() {
                submitTest(true);
            }
        });
        timerUtil.start();
    }

    // TestPanel.java中的submitTest方法
    private void submitTest(boolean isTimeout) {
        if (!isTesting) return;
        isTesting = false;

        // 停止计时器
        if (timerUtil != null) {
            timerUtil.stop();
        }

        // 检查答案并统计正确数量
        int correctCount = 0;
        for (int i = 0; i < problems.size(); i++) {
            String userAnswer = answerFields.get(i).getText().trim();
            String correctAnswer = problems.get(i).getAnswer();
            boolean isCorrect = false;

            try {
                double userValue = Double.parseDouble(userAnswer);
                double correctValue = Double.parseDouble(correctAnswer);
                isCorrect = Math.abs(userValue - correctValue) < 0.0001;
            } catch (NumberFormatException e) {
                isCorrect = false;
            }

            if (isCorrect) {
                correctCount++;
            }

            // 显示正确答案和用户答案
            String resultText = String.format("%d. %s = %s (你的答案: %s %s)",
                    i + 1, problems.get(i).getQuestion(), correctAnswer,
                    userAnswer, isCorrect ? "对" : "错");

            questionLabels.get(i).setText(resultText);
            answerFields.get(i).setEditable(false);
        }

        // 创建历史记录对象
        HistoryRecord record = new HistoryRecord(
                new Date(),             // 当前时间戳
                correctCount,           // 得分
                10,                     // 总分
                operationType,          // 运算类型
                digitCount,             // 数字位数
                isTimeout               // 是否超时
        );

        // 保存历史记录
        historyService.addHistoryRecord(record);

        // 移除提交按钮，添加操作按钮
        questionPanel.remove(questionPanel.getComponentCount() - 1);
        addActionButtons();

        // 刷新UI
        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void addActionButtons() {
        JPanel buttonPanel = new JPanel();

        // 重做按钮
        JButton redoBtn = new JButton("重做本题");
        redoBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        redoBtn.addActionListener(e -> redoTest());
        buttonPanel.add(redoBtn);

        // 新测试按钮
        JButton newTestBtn = new JButton("新的测试");
        newTestBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        newTestBtn.addActionListener(e -> mainFrame.startTest(operationType, digitCount));
        buttonPanel.add(newTestBtn);

        // 查看历史按钮
        JButton viewHistoryBtn = new JButton("查看历史");
        viewHistoryBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        viewHistoryBtn.addActionListener(e -> mainFrame.showHistoryPanel());
        buttonPanel.add(viewHistoryBtn);

        questionPanel.add(buttonPanel);
    }

    private void redoTest() {
        // 重置测试状态
        this.isTesting = true;

        // 重置答案输入框
        for (int i = 0; i < answerFields.size(); i++) {
            answerFields.get(i).setText("");
            answerFields.get(i).setEditable(true);
            questionLabels.get(i).setText((i + 1) + ". " + problems.get(i).getQuestion() + " = ");
        }

        // 移除操作按钮，添加提交按钮
        questionPanel.remove(questionPanel.getComponentCount() - 1);

        JButton submitBtn = new JButton("提交");
        submitBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        submitBtn.addActionListener(e -> submitTest(false));
        questionPanel.add(submitBtn);

        // 重启计时器
        startTimer();
        questionPanel.revalidate();
        questionPanel.repaint();
    }
}