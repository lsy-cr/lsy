package mathtest;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculationService {
    private ScriptEngine scriptEngine;

    public CalculationService() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        scriptEngine = mgr.getEngineByName("JavaScript");
    }

    public double calculate(String expression) throws ScriptException {
        // 将中文运算符转换为Java识别的运算符
        expression = expression.replace("×", "*").replace("÷", "/");
        Object result = scriptEngine.eval(expression);
        return Double.parseDouble(result.toString());
    }
}
