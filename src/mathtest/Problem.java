package mathtest;

public class Problem {
    private String question;
    private String answer;

    public Problem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String toString() {
        return question + " = " + answer;
    }
}
