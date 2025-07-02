package mathtest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
    private JComboBox<String> operationCombo;
    private JComboBox<String> digitCombo;
    private JButton startBtn;
    private JButton historyBtn;
    private MainFrame mainFrame;

    public MainPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("数学练习题系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        add(titleLabel);

        // 设置区域
        JPanel settingPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        settingPanel.add(new JLabel("运算类型:", JLabel.RIGHT));
        operationCombo = new JComboBox<>(new String[]{"加法", "减法", "乘法", "除法", "混合运算"});
        settingPanel.add(operationCombo);

        settingPanel.add(new JLabel("数字位数:", JLabel.RIGHT));
        digitCombo = new JComboBox<>(new String[]{"1位", "2位", "3位"});
        settingPanel.add(digitCombo);
        add(settingPanel);

        // 开始按钮
        startBtn = new JButton("开始做题");
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户选择的运算类型和数字位数
                String operationType = (String) operationCombo.getSelectedItem();
                int digitCount = digitCombo.getSelectedIndex() + 1;

                // 通知主窗口开始测试，并传递参数
                mainFrame.startTest(operationType, digitCount);
            }
        });
        add(startBtn);

        // 历史记录按钮
        historyBtn = new JButton("查看历史记录");
        historyBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // 显示历史记录面板
                mainFrame.showHistoryPanel();
            }
        });
        add(historyBtn);
    }
}