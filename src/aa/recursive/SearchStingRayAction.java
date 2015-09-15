package aa.recursive;

import aa.StopWatch;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class SearchStingRayAction extends RecursiveAction {

    private static final int FILES_LIMIT = 2;
    private static final int numOfFilesToParse = 4;

    // CSV Columns
    // TradeID, BuyerID, SellerID, Fish Type, Price, Number of Fish traded
    private static final int TRADE_ID = 0;
    private static final int BUYER_ID = 1;
    private static final int SELLER_ID = 2;
    private static final int FISH_TYPE = 3;
    private static final int PRICE = 4;
    private static final int NUMBER_OF_FISH_TRADED = 5;

    private static final CsvParserSettings parserSettings = new CsvParserSettings();

    private List<String> fishFileList;

    public SearchStingRayAction(List<String> fishFileList) {
        this.fishFileList = fishFileList;
    }

    public static void main(String[] args) {

        // Case 1: Include file reading time
        StopWatch watch = new StopWatch();
        watch.start();

        // set csv parser settings
        parserSettings.getFormat().setLineSeparator("\n");
        // disable some multithreading that the csv parser lib does by default
        parserSettings.setReadInputOnSeparateThread(false);

        // Create empty list for all fishes.
        List<String> fishFileList = new ArrayList<>();

        for (int i = 0; i < numOfFilesToParse; i++) {
            fishFileList.add(String.format("fish%d.dat", i));
        }

        SearchStingRayAction searchStingRayAction = new SearchStingRayAction(fishFileList);

        int forkJoinPoolSize = 4;
        ForkJoinPool forkJoinPool = new ForkJoinPool(forkJoinPoolSize);
        forkJoinPool.invoke(searchStingRayAction);

        watch.stop();
        System.out.println("Done in " + watch.getTime() + " ms");
    }

    public static Reader getReader(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        return new InputStreamReader(new BufferedInputStream(new FileInputStream(fileName)), "UTF-8");
    }

    /**
     * The main computation performed by this task.
     */
    @Override
    protected void compute() {

        // if problem too small just do it yourself

        int fishFileListSize = fishFileList.size();

        if (fishFileListSize <= FILES_LIMIT) {

            CsvParser parser = new CsvParser(parserSettings);

            try {

                // on the 8th file fish7.dat the program seem to hang :(
                for (String fishFile : fishFileList) {

                    parser.beginParsing(getReader(fishFile));

                    String[] row;

                    while ((row = parser.parseNext()) != null) {

                        if (row[FISH_TYPE].equals("sting ray")) {
                            int fishPrice = Integer.parseInt(row[PRICE]);

                            if (fishPrice > 9000) {
                                System.out.println("score");
                            }

                        }

                    }

                    // the csv lib docs says that close() is automatically called when the end is reached
                    // but not much harm calling it here too again just to make the intent clear to group mates and prof.
                    parser.stopParsing();

                }

            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            invokeAll(
                    new SearchStingRayAction(fishFileList.subList(0, fishFileListSize / 2)),
                    new SearchStingRayAction(fishFileList.subList(fishFileListSize / 2, fishFileListSize))
            );
        }

    }

}
