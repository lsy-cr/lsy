import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class MathTestSystem extends JFrame {
    // 界面组件
    private JComboBox<String> operationCombo, digitCombo;
    private JButton startBtn, historyBtn, deleteHistoryBtn;
    private JPanel mainPanel, testPanel, historyPanel;
    private JLabel timerLabel;
    private List<JTextField> answerFields = new ArrayList<>();
    private List<JLabel> questionLabels = new ArrayList<>();

    // 系统数据
    private List<String> historyRecords = new ArrayList<>();
    private List<String> currentQuestions = new ArrayList<>();
    private List<String> currentAnswers = new ArrayList<>();
    private javax.swing.Timer countdownTimer;
    private int timeLeft = 120;
    private final String HISTORY_FILE = "math_test_history.txt";
    private ScriptEngine scriptEngine;

    public MathTestSystem() {
        // 初始化脚本引擎
        ScriptEngineManager mgr = new ScriptEngineManager();
        scriptEngine = mgr.getEngineByName("JavaScript");

        initUI();
        loadHistory();
    }

    private void initUI() {
        setTitle("数学自动出题系统");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建主面板
        mainPanel = createMainPanel();
        testPanel = createTestPanel();
        historyPanel = createHistoryPanel();

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("数学练习题系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        panel.add(titleLabel);

        // 设置区域
        JPanel settingPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        settingPanel.add(new JLabel("运算类型:", JLabel.RIGHT));
        operationCombo = new JComboBox<>(new String[]{"加法", "减法", "乘法", "除法", "混合运算"});
        settingPanel.add(operationCombo);

        settingPanel.add(new JLabel("数字位数:", JLabel.RIGHT));
        digitCombo = new JComboBox<>(new String[]{"1位", "2位", "3位"});
        settingPanel.add(digitCombo);
        panel.add(settingPanel);

        // 开始按钮
        startBtn = new JButton("开始做题");
        startBtn.addActionListener(e -> startNewTest());
        panel.add(startBtn);

        // 历史记录按钮
        historyBtn = new JButton("查看历史记录");
        historyBtn.addActionListener(e -> showHistoryPanel());
        panel.add(historyBtn);

        return panel;
    }

    private JPanel createTestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 历史记录显示区域
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 返回按钮
        JButton backBtn = new JButton("返回主菜单");
        backBtn.addActionListener(e -> showMainPanel());

        // 删除历史记录按钮
        deleteHistoryBtn = new JButton("删除历史记录");
        deleteHistoryBtn.addActionListener(e -> deleteHistory());

        // 添加组件
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(backBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(deleteHistoryBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 更新历史记录显示
        updateHistoryDisplay(historyArea);

        return panel;
    }

    private void updateHistoryDisplay(JTextArea historyArea) {
        if (historyRecords.isEmpty()) {
            historyArea.setText("暂无历史记录");
        } else {
            historyArea.setText(String.join("\n\n", historyRecords));
        }
    }

    private void deleteHistory() {
        if (historyRecords.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "当前没有可删除的历史记录",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除所有历史记录吗？此操作不可恢复！",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            historyRecords.clear();
            saveHistory();

            // 更新显示
            JTextArea historyArea = (JTextArea) ((JScrollPane) historyPanel.getComponent(0)).getViewport().getView();
            updateHistoryDisplay(historyArea);

            JOptionPane.showMessageDialog(
                    this,
                    "已成功删除所有历史记录",
                    "操作成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void startNewTest() {
        // 重置测试数据
        currentQuestions.clear();
        currentAnswers.clear();
        answerFields.clear();
        questionLabels.clear();
        testPanel.removeAll();

        // 获取用户选择
        String operationType = (String) operationCombo.getSelectedItem();
        int digits = digitCombo.getSelectedIndex() + 1;

        // 添加计时器
        JPanel timerPanel = new JPanel();
        timerPanel.add(new JLabel("剩余时间:"));
        timerLabel = new JLabel("120 秒");
        timerLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        timerPanel.add(timerLabel);
        testPanel.add(timerPanel);

        // 生成并显示10道题目
        for (int i = 0; i < 10; i++) {
            String[] problem = generateProblem(operationType, digits);
            currentQuestions.add(problem[0]);
            currentAnswers.add(problem[1]);

            JLabel qLabel = new JLabel((i + 1) + ". " + problem[0] + " = ");
            qLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            JTextField answerField = new JTextField(10);
            answerField.setFont(new Font("微软雅黑", Font.PLAIN, 16));

            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.add(qLabel);
            rowPanel.add(answerField);
            testPanel.add(rowPanel);

            questionLabels.add(qLabel);
            answerFields.add(answerField);
        }

        // 添加提交按钮
        JButton submitBtn = new JButton("提交");
        submitBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        submitBtn.addActionListener(e -> submitTest(false));
        testPanel.add(submitBtn);

        // 启动倒计时
        timeLeft = 120;
        startTimer();

        showTestPanel();
    }

    private String[] generateProblem(String operationType, int digits) {
        Random rand = new Random();
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits) - 1;

        if ("混合运算".equals(operationType)) {
            // 生成包含2-3个运算符的混合运算题
            int operatorCount = 2 + rand.nextInt(2); // 2或3个运算符
            StringBuilder problem = new StringBuilder();
            String answer = "0";
            int attempt = 0;

            while (attempt < 10) { // 最多尝试10次生成有效题目
                try {
                    problem.setLength(0);
                    // 生成第一个数
                    int num1 = rand.nextInt(max - min + 1) + min;
                    problem.append(num1);

                    // 生成运算符和数字
                    for (int i = 0; i < operatorCount; i++) {
                        String op = getRandomOperator(rand);
                        int num = rand.nextInt(max - min + 1) + min;

                        // 处理特殊运算规则
                        if (op.equals("÷")) {
                            num = rand.nextInt(max/2 - min + 1) + 1; // 避免除数为0或太大
                            int temp = (int)Double.parseDouble(answer);
                            if (temp % num != 0) {
                                num = findDivisor(temp, min, max);
                            }
                        } else if (op.equals("-")) {
                            int temp = (int)Double.parseDouble(answer);
                            if (temp < num) {
                                // 交换确保结果不为负
                                int t = temp;
                                temp = num;
                                num = t;
                            }
                        }

                        problem.append(" ").append(op).append(" ").append(num);
                        answer = calculate(problem.toString());

                        // 检查计算结果是否合理
                        double result = Double.parseDouble(answer);
                        if (Double.isInfinite(result) || Double.isNaN(result)) {
                            throw new ArithmeticException("Invalid result");
                        }
                    }

                    // 确保答案不是小数（除非是除法）
                    if (!answer.contains(".") || answer.endsWith(".0")) {
                        answer = answer.replace(".0", "");
                        return new String[]{problem.toString(), answer};
                    }
                } catch (Exception e) {
                    // 生成失败，继续尝试
                    attempt++;
                    continue;
                }
            }
            // 如果尝试多次仍失败，生成简单题目
            return generateSimpleProblem(rand, min, max);
        } else {
            // 单运算符题目
            return generateSimpleProblem(operationType, rand, min, max);
        }
    }

    private String[] generateSimpleProblem(String operationType, Random rand, int min, int max) {
        String operation;
        switch (operationType) {
            case "加法":
                operation = "+";
                break;
            case "减法":
                operation = "-";
                break;
            case "乘法":
                operation = "×";
                break;
            case "除法":
                operation = "÷";
                break;
            default:
                operation = "+";
        }

        int num1, num2;
        String answer;

        if ("-".equals(operation)) {
            // 减法：确保结果不为负
            num2 = rand.nextInt(max - min + 1) + min;
            num1 = rand.nextInt(max - min + 1) + num2;
            answer = String.valueOf(num1 - num2);
        } else if ("÷".equals(operation)) {
            // 除法：确保除数不为0且能整除
            num2 = rand.nextInt(max - min + 1) + 1;
            int quotient = rand.nextInt(max / num2) + 1;
            num1 = num2 * quotient;
            answer = String.valueOf(quotient);
        } else {
            // 加法、乘法
            num1 = rand.nextInt(max - min + 1) + min;
            num2 = rand.nextInt(max - min + 1) + min;

            if ("+".equals(operation)) {
                answer = String.valueOf(num1 + num2);
            } else {
                answer = String.valueOf(num1 * num2);
            }
        }

        String problem = num1 + " " + operation + " " + num2;
        return new String[]{problem, answer};
    }

    private String[] generateSimpleProblem(Random rand, int min, int max) {
        // 生成简单的单运算符题目作为后备
        String[] ops = {"+", "-", "×", "÷"};
        String op = ops[rand.nextInt(4)];
        return generateSimpleProblem(op.equals("+") ? "加法" :
                        op.equals("-") ? "减法" :
                                op.equals("×") ? "乘法" : "除法",
                rand, min, max);
    }

    private String getRandomOperator(Random rand) {
        String[] ops = {"+", "-", "×", "÷"};
        return ops[rand.nextInt(4)];
    }

    private int findDivisor(int number, int min, int max) {
        // 找一个能整除number的除数
        if (number == 0) return 1;
        for (int i = min; i <= Math.min(max, number); i++) {
            if (i != 0 && number % i == 0) {
                return i;
            }
        }
        return 1;
    }

    private String calculate(String expression) throws ScriptException {
        // 将中文运算符转换为Java识别的运算符
        expression = expression.replace("×", "*").replace("÷", "/");
        Object result = scriptEngine.eval(expression);
        return result.toString();
    }

    private void startTimer() {
        // 停止之前的计时器
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        // 创建新计时器
        countdownTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText(timeLeft + " 秒");

                if (timeLeft <= 0) {
                    countdownTimer.stop();
                    submitTest(true);
                }
            }
        });
        countdownTimer.start();
    }

    private void submitTest(boolean isTimeout) {
        // 停止计时器
        countdownTimer.stop();

        int correctCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // 检查答案并显示结果
        for (int i = 0; i < 10; i++) {
            String userAnswer = answerFields.get(i).getText().trim();
            boolean isCorrect = false;

            try {
                // 处理小数答案
                double userValue = Double.parseDouble(userAnswer);
                double correctValue = Double.parseDouble(currentAnswers.get(i));
                isCorrect = Math.abs(userValue - correctValue) < 0.0001; // 处理浮点精度问题
            } catch (NumberFormatException e) {
                isCorrect = false;
            }

            if (isCorrect) {
                correctCount++;
            }

            // 显示正确答案和用户答案
            String resultText = String.format("%d. %s = %s (你的答案: %s %s)",
                    i + 1, currentQuestions.get(i), currentAnswers.get(i),
                    userAnswer, isCorrect ? "对" : "错");

            questionLabels.get(i).setText(resultText);
            answerFields.get(i).setEditable(false);
        }

        // 创建历史记录
        String timeInfo = isTimeout ? "时间到自动提交" : "提前提交";
        String record = String.format("%s | 得分: %d/10 | %s | %s位%s",
                sdf.format(new Date()), correctCount, timeInfo,
                digitCombo.getSelectedItem(), operationCombo.getSelectedItem());

        historyRecords.add(0, record); // 添加到开头
        saveHistory();

        // 移除提交按钮，添加操作按钮
        testPanel.remove(testPanel.getComponentCount() - 1);
        addActionButtons();

        testPanel.revalidate();
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
        newTestBtn.addActionListener(e -> startNewTest());
        buttonPanel.add(newTestBtn);

        // 查看历史按钮
        JButton viewHistoryBtn = new JButton("查看历史");
        viewHistoryBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        viewHistoryBtn.addActionListener(e -> showHistoryPanel());
        buttonPanel.add(viewHistoryBtn);

        testPanel.add(buttonPanel);
    }

    private void redoTest() {
        // 重置答案输入框
        for (int i = 0; i < 10; i++) {
            answerFields.get(i).setText("");
            answerFields.get(i).setEditable(true);
            questionLabels.get(i).setText((i + 1) + ". " + currentQuestions.get(i) + " = ");
        }

        // 移除操作按钮，添加提交按钮
        testPanel.remove(testPanel.getComponentCount() - 1);

        JButton submitBtn = new JButton("提交");
        submitBtn.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        submitBtn.addActionListener(e -> submitTest(false));
        testPanel.add(submitBtn);

        // 重启计时器
        timeLeft = 120;
        startTimer();
        testPanel.revalidate();
    }

    private void showHistoryPanel() {
        // 更新历史记录显示
        JTextArea historyArea = (JTextArea) ((JScrollPane) historyPanel.getComponent(0)).getViewport().getView();
        updateHistoryDisplay(historyArea);

        // 显示历史记录面板
        getContentPane().removeAll();
        add(historyPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    historyRecords.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "读取历史记录失败: " + e.getMessage());
        }
    }

    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String record : historyRecords) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存历史记录失败: " + e.getMessage());
        }
    }

    private void showTestPanel() {
        getContentPane().removeAll();
        add(testPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showMainPanel() {
        getContentPane().removeAll();
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MathTestSystem system = new MathTestSystem();
            system.setVisible(true);
        });
    }
}