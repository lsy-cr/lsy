package mathtest;

import mathtest.Problem;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProblemGenerator {
    private ScriptEngine scriptEngine;
    private Random random;

    public ProblemGenerator() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        scriptEngine = mgr.getEngineByName("JavaScript");
        random = new Random();
    }

    public List<Problem> generateProblems(String operationType, int digitCount, int count) {
        List<Problem> problems = new ArrayList<>();
        int min = (int) Math.pow(10, digitCount - 1);
        int max = (int) Math.pow(10, digitCount) - 1;

        for (int i = 0; i < count; i++) {
            try {
                Problem problem;
                if ("混合运算".equals(operationType)) {
                    problem = generateMixedOperationProblem(min, max);
                } else {
                    problem = generateSimpleOperationProblem(operationType, min, max);
                }
                problems.add(problem);
            } catch (Exception e) {
                // 生成失败时，生成简单题目作为后备
                Problem fallbackProblem = generateSimpleOperationProblem(
                        getRandomOperationType(), min, max);
                problems.add(fallbackProblem);
            }
        }

        return problems;
    }

    private Problem generateMixedOperationProblem(int min, int max) throws ScriptException {
        int operatorCount = 2 + random.nextInt(2); // 2或3个运算符
        StringBuilder problem = new StringBuilder();
        String answer = "0";
        int attempt = 0;

        while (attempt < 10) { // 最多尝试10次生成有效题目
            try {
                problem.setLength(0);
                // 生成第一个数
                int num1 = random.nextInt(max - min + 1) + min;
                problem.append(num1);

                // 生成运算符和数字
                for (int i = 0; i < operatorCount; i++) {
                    String op = getRandomOperator();
                    int num = random.nextInt(max - min + 1) + min;

                    // 处理特殊运算规则
                    if (op.equals("÷")) {
                        num = random.nextInt(max/2 - min + 1) + 1; // 避免除数为0或太大
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
                    return new Problem(problem.toString(), answer);
                }
            } catch (Exception e) {
                // 生成失败，继续尝试
                attempt++;
                continue;
            }
        }
        // 如果尝试多次仍失败，生成简单题目
        return generateSimpleOperationProblem(getRandomOperationType(), min, max);
    }

    private Problem generateSimpleOperationProblem(String operationType, int min, int max) {
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
            // 减法：先生成num2，再生成num1，确保num1>num2
            num2 = random.nextInt(max - min + 1) + min;
            num1 = random.nextInt(max - min + 1) + num2;
            answer = String.valueOf(num1 - num2);
        } else if ("÷".equals(operation)) {
            // 除法：确保除数不为0且能整除
            num2 = random.nextInt(max - min + 1) + 1;
            int quotient = random.nextInt(max / num2) + 1;
            num1 = num2 * quotient;
            answer = String.valueOf(quotient);
        } else {
            // 加法、乘法
            num1 = random.nextInt(max - min + 1) + min;
            num2 = random.nextInt(max - min + 1) + min;

            if ("+".equals(operation)) {
                answer = String.valueOf(num1 + num2);
            } else {
                answer = String.valueOf(num1 * num2);
            }
        }

        String problem = num1 + " " + operation + " " + num2;
        return new Problem(problem, answer);
    }

    private String getRandomOperator() {
        String[] ops = {"+", "-", "×", "÷"};
        return ops[random.nextInt(4)];
    }

    private String getRandomOperationType() {
        String[] types = {"加法", "减法", "乘法", "除法"};
        return types[random.nextInt(4)];
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
}
