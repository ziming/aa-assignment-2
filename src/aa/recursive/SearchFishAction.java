package aa.recursive;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class SearchFishAction extends RecursiveAction {

    public static void main(String[] args) {
        SearchFishAction searchFishAction = new SearchFishAction();
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        forkJoinPool.invoke(searchFishAction);
        searchFishAction.go();
    }

    public void go() {


        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        // create new csv parser object
        CsvParser parser = new CsvParser(settings);

        // might be too big and run out of memory 1.5 million lines per file after all
        // if fail look at real all rows of a csv iterator style
        try {
            List<String[]> allRows = parser.parseAll(getReader("fish0.dat"));
            for (String[] strArr : allRows) {
                System.out.println(strArr);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * The main computation performed by this task.
     */
    @Override
    protected void compute() {

        // if problem too small just do it yourself
        if (true) {
            // search for stuff
            return;
        }

        // otherwise
        // invokeAll(...)
    }

    public Reader getReader(String relativePath) throws UnsupportedEncodingException {
        return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
    }
}
