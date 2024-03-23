import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HalfPrecisionConverterGUI extends JFrame {
    private JTextField inputField;
    private JTextArea outputArea;

    public HalfPrecisionConverterGUI() {
        setTitle("Half Precision Converter");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel inputLabel = new JLabel("Input:");
        inputField = new JTextField(20);
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertButtonListener());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveButtonListener());

        JLabel outputLabel = new JLabel("IEEE-754 Binary-16 floating point:");
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel inputPanel = new JPanel();
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(convertButton);
        inputPanel.add(saveButton);

        JPanel outputPanel = new JPanel();
        outputPanel.add(outputLabel);
        outputPanel.add(scrollPane);

        getContentPane().add(inputPanel, "North");
        getContentPane().add(outputPanel, "Center");
    }

    private class ConvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputField.getText().trim(); // Get input from the text field

            try {
                String binaryResult = HalfPrecisionConverter.convertInput(input);
                String hexResult = HalfPrecisionConverter.binaryToHex(binaryResult);
                String outputText = "Binary: " + binaryResult + "\nHexadecimal: " + hexResult;
                outputArea.setText(outputText);
            } catch (IllegalArgumentException ex) {
                // If an IllegalArgumentException occurs during conversion, set outputText to
                // the error message
                String binaryResult = HalfPrecisionConverter.convertInput(input);
                outputArea.setText(binaryResult);
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String result = outputArea.getText();
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(HalfPrecisionConverterGUI.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(result);
                    JOptionPane.showMessageDialog(HalfPrecisionConverterGUI.this,
                            "Result saved successfully to: " + filePath,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(HalfPrecisionConverterGUI.this,
                            "Error saving file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HalfPrecisionConverterGUI converterGUI = new HalfPrecisionConverterGUI();
            converterGUI.setVisible(true);
        });
    }
}
