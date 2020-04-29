package editor;

import java.awt.event.ActionListener;

public class ActionListeners {
    private static ActionListener saveAction;
    private static ActionListener openAction;
    private static ActionListener exitAction;
    private static ActionListener searchAction;
    private static ActionListener prevMatchAction;
    private static ActionListener nextMatchAction;
    private static ActionListener useRegExAction;
    private static ActionListener saveAsAction;

    ActionListeners() {}

    public static ActionListener getSaveAction() {
        return saveAction;
    }

    public static void setSaveAction(final ActionListener saveAction) {
        ActionListeners.saveAction = saveAction;
    }

    public static ActionListener getOpenAction() {
        return openAction;
    }

    public static void setOpenAction(final ActionListener openAction) {
        ActionListeners.openAction = openAction;
    }

    public static ActionListener getExitAction() {
        return exitAction;
    }

    public static void setExitAction(final ActionListener exitAction) {
        ActionListeners.exitAction = exitAction;
    }

    public static ActionListener getSearchAction() {
        return searchAction;
    }

    public static void setSearchAction(final ActionListener searchAction) {
        ActionListeners.searchAction = searchAction;
    }

    public static ActionListener getPrevMatchAction() {
        return prevMatchAction;
    }

    public static void setPrevMatchAction(final ActionListener prevMatchAction) {
        ActionListeners.prevMatchAction = prevMatchAction;
    }

    public static ActionListener getNextMatchAction() {
        return nextMatchAction;
    }

    public static void setNextMatchAction(final ActionListener nextMatchAction) {
        ActionListeners.nextMatchAction = nextMatchAction;
    }

    public static ActionListener getUseRegExAction() {
        return useRegExAction;
    }

    public static void setUseRegExAction(final ActionListener useRegExAction) {
        ActionListeners.useRegExAction = useRegExAction;
    }

    public static ActionListener getSaveAsAction() {
        return saveAsAction;
    }

    public static void setSaveAsAction(final ActionListener saveAsAction) {
        ActionListeners.saveAsAction = saveAsAction;
    }
}
