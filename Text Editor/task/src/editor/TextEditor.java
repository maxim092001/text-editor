package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextEditor extends JFrame {

    private final JFileChooser fileChooser = new JFileChooser();
    private List<Pair<Integer, Integer>> searchIndexes;
    private int currentIndex = -1;

    public TextEditor() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(700, 500);
        setTitle("Maxon");
        fileChooser.setVisible(false);
        fileChooser.setName("FileChooser");
        add(fileChooser, BorderLayout.PAGE_END);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        ActionListeners.setExitAction(actionEvent -> {
            dispose();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        JTextArea textArea = createTextArea();
        JScrollPane scrollPane = makeTextAreaScrollable(textArea);

        JPanel saveLoadPanel = createTopBar(textArea);

        add(scrollPane, BorderLayout.CENTER);
        add(saveLoadPanel, BorderLayout.NORTH);

        MyMenuBar myMenuBar = new MyMenuBar();
        myMenuBar.createFileMenu();
        myMenuBar.createSearchMenu();
        setJMenuBar(myMenuBar.getMenuBar());

    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");

        return textArea;
    }

    private JScrollPane makeTextAreaScrollable(JTextArea textArea) {

        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setName("ScrollPane");

        return scrollPane;
    }

    private JPanel createTopBar(JTextArea textArea) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField searchField = new JTextField();
        searchField.setName("SearchField");
        forceSize(searchField, 300, 20);

        JCheckBox useRegExCheckBox = new JCheckBox("Use regex");
        useRegExCheckBox.setName("UseRegExCheckbox");

        setActionListeners(textArea, searchField, useRegExCheckBox);

        JButton saveButton = new JButton(getImageIcon(nameToAbsolutePath("save.png")));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(ActionListeners.getSaveAction());

        JButton openButton = new JButton(getImageIcon(nameToAbsolutePath("open.png")));
        openButton.setName("OpenButton");
        openButton.addActionListener(ActionListeners.getOpenAction());

        JButton startSearchButton = new JButton(getImageIcon(nameToAbsolutePath("search.png")));
        startSearchButton.setName("StartSearchButton");

        startSearchButton.addActionListener(ActionListeners.getSearchAction());

        JButton previousMatchButton = new JButton(getImageIcon(nameToAbsolutePath("prev-arrow.png")));
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(ActionListeners.getPrevMatchAction());

        JButton nextMatchButton = new JButton(getImageIcon(nameToAbsolutePath("next-arrow.png")));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(ActionListeners.getNextMatchAction());

        searchPanel.add(openButton);
        searchPanel.add(saveButton);
        searchPanel.add(searchField);
        searchPanel.add(startSearchButton);
        searchPanel.add(previousMatchButton);
        searchPanel.add(nextMatchButton);
        searchPanel.add(useRegExCheckBox);
        return searchPanel;
    }


    private void setActionListeners(JTextArea textArea, JTextField searchField, JCheckBox useRegExCheckBox) {
        ActionListeners.setSaveAction(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile == null) {
                    textArea.setText("");
                } else {
                    try (Writer writer = new FileWriter(selectedFile)) {
                        writer.write(textArea.getText());
                    } catch (IOException e) {
                        textArea.setText("");
                        e.printStackTrace();
                    }
                }
            }
        });

        ActionListeners.setOpenAction(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile == null) {
                    textArea.setText("");
                } else {
                    try {
                        textArea.setText(new String(Files.readAllBytes(selectedFile.toPath())));
                    } catch (IOException e) {
                        textArea.setText("");
                        e.printStackTrace();
                    }

                }
            }
        });

        ActionListeners.setSearchAction(actionEvent -> {
            currentIndex = -1;
            textArea.setCaretPosition(textArea.getText().length() - 1);
            if (searchField.getText().isEmpty()) {
                return;
            }
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<List<Pair<Integer, Integer>>> futureCall = executor.submit(new SearchTask(textArea, searchField.getText(),
                    useRegExCheckBox.isSelected()));
            try {
                searchIndexes = futureCall.get();
                if (searchIndexes.isEmpty()) {
                    return;
                }
                currentIndex = 0;
                setCaret(textArea, searchField.getText().length());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        ActionListeners.setNextMatchAction(actionEvent -> {
            if (currentIndex == -1) {
                textArea.setCaretPosition(textArea.getText().length() - 1);
                textArea.grabFocus();
                return;
            }
            currentIndex = Math.min(currentIndex + 1, searchIndexes.size() - 1);
            setCaret(textArea, searchField.getText().length());
        });

        ActionListeners.setPrevMatchAction(actionEvent -> {
            if (currentIndex == -1) {
                textArea.setCaretPosition(textArea.getText().length() - 1);
                textArea.grabFocus();
                return;
            }

            currentIndex = Math.max(currentIndex - 1, 0);
            setCaret(textArea, searchField.getText().length());
        });

        ActionListeners.setUseRegExAction(actionEvent -> useRegExCheckBox.setSelected(true));
    }

    private void setCaret(JTextArea textArea, int wordSize) {
        textArea.setCaretPosition(searchIndexes.get(currentIndex).getFirst() + wordSize);
        textArea.select(searchIndexes.get(currentIndex).getFirst(),
                searchIndexes.get(currentIndex).getSecond());
        textArea.grabFocus();
    }

    private String nameToAbsolutePath(String fileName) {
        String workingDirectory = System.getProperty("user.dir") + "/Text Editor/task/src/editor/resources";
        return workingDirectory + File.separator + fileName;
    }

    private ImageIcon getImageIcon(String filePath) {
        ImageIcon icon = new ImageIcon(filePath);
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    private void forceSize(final JComponent component, final int width, final int height) {
        Dimension d = new Dimension(width, height);
        component.setMinimumSize(d);
        component.setMaximumSize(d);
        component.setPreferredSize(d);
    }
}
