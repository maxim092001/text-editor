package editor;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Maksim Grankin (maximgran@gmail.com)
 */
public class MyMenuBar {

    private JMenuBar menuBar;

    public MyMenuBar() {
        menuBar = new JMenuBar();
    }

    public void createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.setName("MenuFile");

        fileMenu.add(getOpenItem());
        fileMenu.add(getSaveItem());
        fileMenu.add(getSaveAsItem());
        fileMenu.add(getExitItem());
        menuBar.add(fileMenu);
    }

    public void createSearchMenu() {
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        searchMenu.add(getStartSearchItem());
        searchMenu.add(getPreviousSearchItem());
        searchMenu.add(getNextMatchItem());
        searchMenu.add(getUseRegExpItem());

        menuBar.add(searchMenu);
    }

    private JMenuItem getStartSearchItem() {
        JMenuItem startSearchItem = new JMenuItem("Start search");
        startSearchItem.setName("MenuStartSearch");
        startSearchItem.addActionListener(ActionListeners.getSearchAction());
        return startSearchItem;
    }

    private JMenuItem getPreviousSearchItem() {
        JMenuItem previousSearchItem = new JMenuItem("Previous match");
        previousSearchItem.setName("MenuPreviousMatch");
        previousSearchItem.addActionListener(ActionListeners.getPrevMatchAction());
        return previousSearchItem;
    }

    private JMenuItem getNextMatchItem() {
        JMenuItem nextMatchItem = new JMenuItem("Next match");
        nextMatchItem.setName("MenuNextMatch");
        nextMatchItem.addActionListener(ActionListeners.getNextMatchAction());
        return nextMatchItem;
    }

    private JMenuItem getUseRegExpItem() {
        JMenuItem useRegExpItem = new JMenuItem("Use regular expression");
        useRegExpItem.setName("MenuUseRegExp");
        useRegExpItem.addActionListener(ActionListeners.getUseRegExAction());
        return useRegExpItem;
    }

    private JMenuItem getOpenItem() {
        JMenuItem openItem = new JMenuItem("Open");
        openItem.setName("MenuOpen");
        openItem.addActionListener(ActionListeners.getOpenAction());
        return openItem;
    }

    private JMenuItem getSaveItem() {
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setName("MenuSave");
        saveItem.addActionListener(ActionListeners.getSaveAction());
        return saveItem;
    }
    private JMenuItem getSaveAsItem() {
        JMenuItem saveItem = new JMenuItem("Save as...");
        saveItem.setName("MenuSaveAs");
        saveItem.addActionListener(ActionListeners.getSaveAsAction());
        return saveItem;
    }

    private JMenuItem getExitItem() {
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");
        exitItem.addActionListener(ActionListeners.getExitAction());
        return exitItem;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(final JMenuBar menuBar) {
        this.menuBar = menuBar;
    }
}
