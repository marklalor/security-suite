package edu.cwru.eecs444.fall2018;

import edu.cwru.eecs444.fall2018.implementations.Rsa;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class GraphicalInterface {

    private static JComponent labelAbove(String label, JComponent other) {
        JPanel compositionPanel = new JPanel();
        compositionPanel.setLayout(new BoxLayout(compositionPanel, BoxLayout.Y_AXIS));
        JLabel labelComponent = new JLabel(label);
        labelComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        compositionPanel.add(labelComponent);
        compositionPanel.add(other);
        return compositionPanel;
    }

    private static JComponent borderify(JComponent component) {
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        component.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return component;
    }

    private static JComponent makeFullComponent(String name, JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JComponent composedComponent = labelAbove(name, borderify(scrollPane));
        return composedComponent;
    }


    private static JTextArea makeTextArea() {
        JTextArea area = new JTextArea(4, 14);
        area.setFont(new Font("monospaced", Font.PLAIN, 12));
        area.setWrapStyleWord(false);
        area.setLineWrap(true);
        return area;
    }

    public static JComponent createBinaryPanel(String buttonText, final Interfacers.BinaryAction action) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton actionButton = new JButton(buttonText);
        panel.add(actionButton);

        // Input boxes
        JTextArea inputKey = makeTextArea();
        JTextArea inputText = makeTextArea();
        JTextArea inputKeyBinary = makeTextArea();
        JTextArea inputTextBinary = makeTextArea();
        // Output boxes
        JTextArea outputBinary = makeTextArea();
        JTextArea outputText = makeTextArea();

        JComponent inputKeyComponent = makeFullComponent("Key", inputKey);
        JComponent inputTextComponent = makeFullComponent("Text", inputText);
        JComponent inputKeyBinaryComponent = makeFullComponent("Key Bytes", inputKeyBinary);
        JComponent inputTextBinaryComponent = makeFullComponent("Text Bytes", inputTextBinary);
        // Output boxes
        JComponent outputBinaryComponent = makeFullComponent("Result Bytes", outputBinary);
        JComponent outputTextComponent = makeFullComponent("Result Text", outputText);

        actionButton.addActionListener(e -> {
            final byte[] key = inputKey.getText().getBytes(Interfacers.UTF8_CHARSET);
            final byte[] text = inputText.getText().getBytes(Interfacers.UTF8_CHARSET);
            inputKeyBinary.setText(Utilities.bytesToHexSpaced(key));
            inputTextBinary.setText(Utilities.bytesToHexSpaced(text));

            try {
                final byte[] output = action.doAction(key, text);
                outputBinary.setText(Utilities.bytesToHexSpaced(output));
                outputText.setText(new String(output, Interfacers.UTF8_CHARSET));
            } catch (Exception ex) {
                outputBinary.setText("Exception occurred: " + ex.toString());
                outputText.setText("Exception occurred: " + ex.toString());
            }
        });

        Arrays.asList(inputKeyComponent, inputTextComponent, inputKeyBinaryComponent,
                inputTextBinaryComponent, outputBinaryComponent, outputTextComponent).stream().forEach(panel::add);

        return panel;
    }

    public static JComponent createPureBinaryPanel(String buttonText, final Interfacers.BinaryAction action) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton actionButton = new JButton(buttonText);
        panel.add(actionButton);

        // Input boxes
        JTextArea inputKeyBinary = makeTextArea();
        JTextArea inputTextBinary = makeTextArea();
        // Output boxes
        JTextArea outputBinary = makeTextArea();

        JComponent inputKeyBinaryComponent = makeFullComponent("Key Bytes", inputKeyBinary);
        JComponent inputTextBinaryComponent = makeFullComponent("Text Bytes", inputTextBinary);
        // Output boxes
        JComponent outputBinaryComponent = makeFullComponent("Result Bytes", outputBinary);

        actionButton.addActionListener(e -> {
            final byte[] key = Utilities.hexStringToByteArray(
                    inputKeyBinary.getText().replaceAll("\\s+",""));
            final byte[] text = Utilities.hexStringToByteArray(
                    inputTextBinary.getText().replaceAll("\\s+",""));

            try {
                final byte[] output = action.doAction(key, text);
                outputBinary.setText(Utilities.bytesToHexSpaced(output));
            } catch (Exception ex) {
                outputBinary.setText("Exception occurred: " + ex.toString());
            }
        });

        Arrays.asList(inputKeyBinaryComponent, inputTextBinaryComponent, outputBinaryComponent).stream().forEach(panel::add);

        return panel;
    }

    public static JComponent createTextPanel(String buttonText, final Interfacers.TextAction action) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton actionButton = new JButton(buttonText);
        panel.add(actionButton);

        JTextArea inputKey = makeTextArea();
        JTextArea inputText = makeTextArea();
        JTextArea outputText = makeTextArea();

        JComponent inputKeyComponent = makeFullComponent("Key", inputKey);
        JComponent inputTextComponent = makeFullComponent("Text", inputText);
        JComponent outputTextComponent = makeFullComponent("Result Text", outputText);

        actionButton.addActionListener(e -> {

            try {
                final String output = action.doAction(inputKey.getText(), inputText.getText());
                outputText.setText(output);
            } catch (Exception ex) {
                outputText.setText("Exception occurred: " + ex.toString());
            }
        });

        Arrays.asList(inputKeyComponent, inputTextComponent, outputTextComponent).stream().forEach(panel::add);

        return panel;
    }

    private static JComponent createKeypairGenPanel(String buttonText, Interfacers.KeypairGenInterfacer keypairGenInterfacer) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton actionButton = new JButton(buttonText);
        panel.add(actionButton);

        JTextArea outputKeyPrivate = makeTextArea();
        JTextArea outputKeyPublic = makeTextArea();

        JComponent inputKeyComponent = makeFullComponent("Private Key", outputKeyPrivate);
        JComponent inputTextComponent = makeFullComponent("Public Key", outputKeyPublic);

        actionButton.addActionListener(e -> {

            try {
                final Rsa.KeyPair output = keypairGenInterfacer.generateKeyPair();
                outputKeyPrivate.setText(Utilities.bytesToHexSpaced(output.getPrivateKey()));
                outputKeyPublic.setText(Utilities.bytesToHexSpaced(output.getPublicKey()));
            } catch (Exception ex) {
                outputKeyPrivate.setText("Exception occurred: " + ex.toString());
            }
        });

        Arrays.asList(inputKeyComponent, inputTextComponent).stream().forEach(panel::add);

        return panel;
    }

    public static JFrame buildGUI(Map<String, Object> interfacers) {
        JFrame frame = new JFrame("Security Suite");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Place the statically-defined interfacers
        for (String interfacerName: interfacers.keySet()) {
            Object interfacer = interfacers.get(interfacerName);
            if (interfacer instanceof Interfacers.BinaryInterfacer) {
                Interfacers.BinaryInterfacer binaryInterfacer = (Interfacers.BinaryInterfacer) interfacer;
                frame.getContentPane().add(createPureBinaryPanel("Encipher " + interfacerName, binaryInterfacer::encipher));
                frame.getContentPane().add(createPureBinaryPanel("Decipher " + interfacerName, binaryInterfacer::decipher));
            } else if (interfacer instanceof Interfacers.TextInterfacer) {
                Interfacers.TextInterfacer textInterfacer = (Interfacers.TextInterfacer) interfacer;
                frame.getContentPane().add(createTextPanel("Encipher " + interfacerName, textInterfacer::encipher));
                frame.getContentPane().add(createTextPanel("Decipher " + interfacerName, textInterfacer::decipher));
            } else if (interfacer instanceof Interfacers.KeypairGenInterfacer) {
                Interfacers.KeypairGenInterfacer keypairGenInterfacer = (Interfacers.KeypairGenInterfacer) interfacer;
                frame.getContentPane().add(createKeypairGenPanel("Keygen " + interfacerName, keypairGenInterfacer));
            } else {
                frame.add(new JLabel("No GUI to handle interfacer of type " + interfacer.getClass().getName()));
            }
        }

        return frame;
    }

    public static void main(String[] args) {
        JFrame frame = buildGUI(Interfacers.INTERFACERS);
        frame.pack();
        frame.setVisible(true);
    }
}
