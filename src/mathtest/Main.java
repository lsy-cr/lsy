package mathtest;
import mathtest.MainFrame;

public class Main {
    public static void main(String[] args) {
        // 使用SwingUtilities确保UI在EDT线程中创建和更新
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
