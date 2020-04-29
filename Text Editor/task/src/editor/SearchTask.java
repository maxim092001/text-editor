package editor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchTask implements Callable<List<StartEndPair>> {

    private final JTextArea textArea;
    private final String pattern;
    private final boolean isRegex;

    public SearchTask(final JTextArea textArea, final String pattern, final boolean isRegex) {
        this.textArea = textArea;
        this.pattern = pattern;
        this.isRegex = isRegex;
    }

    @Override
    public List<StartEndPair> call() {
        String text = textArea.getText();
        List<StartEndPair> occurrenceIndexes = new ArrayList<>();
        if (isRegex) {
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(text);
            while (matcher.find()) {
                occurrenceIndexes.add(new StartEndPair(matcher.start(), matcher.end()));
            }
        } else {
            int index = 0;
            while (index != -1) {
                index = text.indexOf(pattern, index);
                if (index != -1) {
                    occurrenceIndexes.add(new StartEndPair(index, index + pattern.length()));
                    index++;
                }
            }
        }

        return occurrenceIndexes;
    }
}
