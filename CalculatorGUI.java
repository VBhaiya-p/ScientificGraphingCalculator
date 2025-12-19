package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI extends JFrame {
    private JTextField input = new JTextField();
    private JTextArea history = new JTextArea();
    private GraphPanel graph = new GraphPanel();
    private ExpressionParser parser = new ExpressionParser();
    private HistoryManager historyManager = new HistoryManager();

    public CalculatorGUI() {
        setTitle("Scientific Graphing Calculator");
        setSize(1000,650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // --- LEFT PANEL (Equation / Input / History / Keypad) ---
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Input field
        leftPanel.add(input, BorderLayout.NORTH);

        // Make Enter key work like "="
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) evaluateInput();
            }
        });

        // History area
        history.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(history);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // Keypad
        JPanel keypad = new JPanel(new GridLayout(6,4));
        String[] keys = {"7","8","9","+","4","5","6","-","1","2","3","*",
                         "0",".","=","/","sin","cos","tan","^","log","ln","sqrt","x"};
        for (String k : keys) {
            JButton b = new JButton(k);
            b.addActionListener(e -> press(k));
            keypad.add(b);
        }
        leftPanel.add(keypad, BorderLayout.SOUTH);

        // --- RIGHT PANEL (Graph) ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(graph, BorderLayout.CENTER);

        // Controls for graph
        JPanel controls = new JPanel();
        JButton zoomIn=new JButton("Zoom+"), zoomOut=new JButton("Zoom-"),
                panL=new JButton("←"), panR=new JButton("→"), grid=new JButton("Grid"),
                info=new JButton("Info");
        JButton save=new JButton("Save"), load=new JButton("Load");

        zoomIn.addActionListener(e -> graph.zoom(0.8));
        zoomOut.addActionListener(e -> graph.zoom(1.25));
        panL.addActionListener(e -> graph.pan(-1,0));
        panR.addActionListener(e -> graph.pan(1,0));
        grid.addActionListener(e -> graph.toggleGrid());
        info.addActionListener(e -> {
            double y0 = graph.getYIntercept();
            double[] r = graph.getRange();
            JOptionPane.showMessageDialog(this,"y-int="+y0+"\nrange≈["+r[0]+","+r[1]+"]");
        });

        save.addActionListener(e -> {
            try { historyManager.saveToFile("history.txt"); }
            catch(Exception ex) { JOptionPane.showMessageDialog(this,"Error saving file"); }
        });

        load.addActionListener(e -> {
            try {
                historyManager.loadFromFile("history.txt");
                history.setText("");
                for (String s : historyManager.getAll()) history.append(s+"\n");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this,"Error loading file"); }
        });

        controls.add(save); controls.add(load); controls.add(info);
        controls.add(zoomIn); controls.add(zoomOut); controls.add(panL);
        controls.add(panR); controls.add(grid);

        rightPanel.add(controls, BorderLayout.SOUTH);

        // --- SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        add(splitPane);

    }

    private void press(String s) {
        if (s.equals("=")) evaluateInput();
        else input.setText(input.getText() + s);
    }

    private void evaluateInput() {
        try {
            String text = input.getText().trim();
            if (text.isEmpty()) return;

            if (text.contains("=")) { // Solve equation
                String[] p = text.split("=");
                EquationSolver solver = new EquationSolver();
                double root = solver.solve(p[0], p[1], -100, 100);
                historyManager.add(text + " → x ≈ " + root);
                history.append(text + " → x ≈ " + root + "\n");
            } else { // Evaluate expression
                double result = parser.evaluate(text, 0);
                historyManager.add(text + " = " + result);
                history.append(text + " = " + result + "\n");
                graph.setFunction(text);
            }

            input.setText("");
            history.setCaretPosition(history.getDocument().getLength());

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI().setVisible(true));
    }
}
