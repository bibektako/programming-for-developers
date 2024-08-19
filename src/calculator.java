import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Stack;

public class calculator extends JFrame {
    private JTextField inputField;
    private JButton calculateButton;
    private JLabel resultLabel;
    private JPanel buttonPanel;
    private JPanel topPanel;
    private JPanel themePanel;
    private JToggleButton themeToggle;

    public calculator() {
        setTitle("Modern Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout(20, 20));

        // Initialize components
        inputField = new JTextField();
        calculateButton = new RoundedButton("Calculate");
        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);

        // Set font and size for components
        Font customFont = new Font("Arial", Font.PLAIN, 20);
        inputField.setFont(customFont);
        calculateButton.setFont(new Font("Arial", Font.PLAIN, 18));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 22));

        // Set result label properties
        resultLabel.setOpaque(true);
        resultLabel.setBackground(Color.DARK_GRAY);
        resultLabel.setForeground(Color.WHITE);

        // Border settings
        inputField.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Button panel (number and operation buttons)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 4, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttons = {
                "0", "(", ")", "+",
                "1", "2", "3", "-",
                "4", "5", "6", "*",
                "7", "8", "9", "/"



        };

        for (String text : buttons) {
            JButton button = new RoundedButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 22));

            // Set button size and color
            button.setPreferredSize(new Dimension(70, 70)); // Increased button size
            button.setBackground(new Color(180, 180, 180)); // Light gray color
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            button.addActionListener(e -> inputField.setText(inputField.getText() + button.getText()));
            buttonPanel.add(button);
        }

        // Top panel (input and result labels)
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.add(inputField, BorderLayout.NORTH);
        topPanel.add(resultLabel, BorderLayout.SOUTH);

        // Theme panel (theme toggle button)
        themePanel = new JPanel();
        themeToggle = new JToggleButton("Toggle Dark/Light");
        themeToggle.setFont(new Font("Arial", Font.PLAIN, 16));
        themeToggle.addItemListener(e -> toggleTheme());
        themePanel.add(themeToggle);

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(themePanel, BorderLayout.WEST); // Moved to the left side for better positioning
        add(calculateButton, BorderLayout.SOUTH);

        // Calculate button action listener
        calculateButton.addActionListener(e -> {
            String expression = inputField.getText();
            try {
                double result = evaluateExpression(expression);
                resultLabel.setText("Result: " + result);
            } catch (Exception ex) {
                resultLabel.setText("Error: Invalid expression");
            }
        });
    }

    private double evaluateExpression(String expression) {
        return new ExpressionEvaluator().evaluate(expression);
    }

    private void toggleTheme() {
        if (themeToggle.isSelected()) {
            // Dark mode
            getContentPane().setBackground(Color.DARK_GRAY);
            inputField.setBackground(Color.BLACK);
            inputField.setForeground(Color.WHITE);
            resultLabel.setBackground(Color.BLACK);
            resultLabel.setForeground(Color.WHITE);
            themeToggle.setText("Light Mode");
        } else {
            // Light mode
            getContentPane().setBackground(Color.WHITE);
            inputField.setBackground(Color.WHITE);
            inputField.setForeground(Color.BLACK);
            resultLabel.setBackground(Color.DARK_GRAY);
            resultLabel.setForeground(Color.WHITE);
            themeToggle.setText("Dark Mode");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new calculator().setVisible(true));
    }
}

// Custom button with rounded corners
class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getWidth() * 0.2f, getHeight() * 0.2f)); // Rounded corners with 20% radius
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getWidth() * 0.2f, getHeight() * 0.2f)); // Border with 20% radius
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getWidth() * 0.2f, getHeight() * 0.2f);
        return shape.contains(x, y);
    }
}
class ExpressionEvaluator {
    public double evaluate(String expression) {
        return evaluateExpression(expression);
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        Stack<Character> brackets = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ') continue;

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (ch == '(' || ch == '[' || ch == '{') {
                operators.push(ch);
                brackets.push(ch);
            } else if (ch == ')' || ch == ']' || ch == '}') {
                while (!operators.isEmpty() && !isMatchingPair(operators.peek(), ch)) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                if (!operators.isEmpty() && isMatchingPair(operators.peek(), ch)) {
                    operators.pop();
                    brackets.pop();
                }
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek()) && !isBracket(operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')') ||
                (open == '[' && close == ']') ||
                (open == '{' && close == '}');
    }

    private boolean isBracket(char ch) {
        return ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }
}
