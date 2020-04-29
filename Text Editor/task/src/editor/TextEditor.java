package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextEditor extends JFrame {

    private final int standardIndex = -1;
    private final FileDialog fileDialog = new FileDialog(this);
    private List<StartEndPair> patternIndexes;
    private int currentIndex = standardIndex;
    private String currentDirectory = System.getProperty("user.home");
    private final String OS = System.getProperty("os.name").toLowerCase();
    JTextArea textArea;
    public TextEditor() {
        if (OS.contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setSize((int)width, (int)height);
        setTitle("Maxon");
        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        ActionListeners.setExitAction(actionEvent -> {
            dispose();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        textArea = createTextArea();
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

        JButton saveButton = new JButton(getImageIcon(getUrlByFileName("save.png")));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(ActionListeners.getSaveAction());

        JButton openButton = new JButton(getImageIcon(getUrlByFileName("open.png")));
        openButton.setName("OpenButton");
        openButton.addActionListener(ActionListeners.getOpenAction());

        JButton startSearchButton = new JButton(getImageIcon(getUrlByFileName("search.png")));
        startSearchButton.setName("StartSearchButton");

        startSearchButton.addActionListener(ActionListeners.getSearchAction());

        JButton previousMatchButton = new JButton(getImageIcon(getUrlByFileName("prev-arrow.png")));
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(ActionListeners.getPrevMatchAction());

        JButton nextMatchButton = new JButton(getImageIcon(getUrlByFileName("next-arrow.png")));
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
        ActionListeners.setSaveAsAction(actionEvent -> {
            fileDialog.setMode(FileDialog.SAVE);
            fileDialog.setDirectory(currentDirectory);
            fileDialog.setVisible(true);
            saveText(textArea);
        });

        ActionListeners.setSaveAction(actionEvent -> saveText(textArea));

        ActionListeners.setOpenAction(actionEvent -> {
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setDirectory(currentDirectory);
            fileDialog.setVisible(true);

            String absoluteFileName = getFileDialogAbsolutePath();
            currentDirectory = fileDialog.getDirectory();

            if (fileDialog.getFile() == null) {
                textArea.setText("");
            } else {
                File selectedFile = new File(absoluteFileName);
                try {
                    textArea.setText(new String(Files.readAllBytes(selectedFile.toPath())));
                } catch (IOException e) {
                    textArea.setText("");
                    e.printStackTrace();
                }
            }
        });

        ActionListeners.setSearchAction(actionEvent -> {
            currentIndex = standardIndex;
            textArea.setCaretPosition(0);
            if (searchField.getText().isEmpty()) {
                return;
            }
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<List<StartEndPair>> futureCall = executor.submit(new SearchTask(textArea, searchField.getText(),
                    useRegExCheckBox.isSelected()));
            try {
                patternIndexes = futureCall.get();
                if (patternIndexes.isEmpty()) {
                    return;
                }
                currentIndex = 0;
                setCaret(textArea, searchField.getText().length());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        ActionListeners.setNextMatchAction(actionEvent -> {
            if (currentIndex == standardIndex) {
                return;
            }
            currentIndex = Math.min(currentIndex + 1, patternIndexes.size() - 1);
            setCaret(textArea, searchField.getText().length());
        });

        ActionListeners.setPrevMatchAction(actionEvent -> {
            if (currentIndex == standardIndex) {
                return;
            }

            currentIndex = Math.max(currentIndex - 1, 0);
            setCaret(textArea, searchField.getText().length());
        });

        ActionListeners.setUseRegExAction(actionEvent -> useRegExCheckBox.setSelected(true));
    }

    private void saveText(final JTextArea textArea) {
        if (fileDialog.getFile() == null) {
            textArea.setText("");
        } else {
            File selectedFile = new File(getFileDialogAbsolutePath());
            try (Writer writer = new FileWriter(selectedFile)) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                textArea.setText("");
                e.printStackTrace();
            }
        }
    }

    private void setCaret(JTextArea textArea, int wordSize) {
        textArea.setCaretPosition(patternIndexes.get(currentIndex).getStart() + wordSize);
        textArea.select(patternIndexes.get(currentIndex).getStart(),
                patternIndexes.get(currentIndex).getEnd());
        textArea.grabFocus();
    }

    private URL getUrlByFileName(String filename) {
        return getClass().getClassLoader().getResource(filename);
    }

    private ImageIcon getImageIcon(URL url) {
        ImageIcon icon = new ImageIcon(url);
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


    private String getFileDialogAbsolutePath() {
        return fileDialog.getDirectory() + fileDialog.getFile();
    }
}
