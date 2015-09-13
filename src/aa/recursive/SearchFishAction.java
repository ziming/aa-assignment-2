package aa.recursive;

import aa.StopWatch;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class SearchFishAction extends RecursiveAction {

    private static final int LIMIT = 500_000;

    // CSV Columns
    // TradeID, BuyerID, SellerID, Fish Type, Price, Number of Fish traded
    private static final int TRADE_ID = 0;
    private static final int BUYER_ID = 1;
    private static final int SELLER_ID = 2;
    private static final int FISH_TYPE = 3;
    private static final int PRICE = 4;
    private static final int NUMBER_OF_FISH_TRADED = 5;
    private List<String[]> fishList;

    public SearchFishAction(List<String[]> fishList) {
        this.fishList = fishList;
    }

    public static void main(String[] args) {

        // Case 1: Include file reading time
        StopWatch watch = new StopWatch();
        watch.start();

        // Create empty list for all fishes.
        List<String[]> fishList = new ArrayList<>(15_000_000);

        // 1. Read all the files and add them to a list.
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        // By default readInputOnSeparateThread is true, for the purpose of this problem
        // let's set it to false. So that it does not use multi threading.
        settings.setReadInputOnSeparateThread(false);

        // creates a CSV parser
        CsvParser parser = new CsvParser(settings);

        // parses all rows in one go. fish0.dat to fish9.dat
        try {

            // on the 8th file fish7.dat the program seem to hang :(
            for (int i = 0; i < 10; i++) {

                fishList.addAll(parser.parseAll(getReader(String.format("fish%d.dat", i))));

            }


        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println(fishList.size());

        // Case 2: Exclude file reading time
//        StopWatch watch = new StopWatch();
//        watch.start();

        SearchFishAction searchFishAction = new SearchFishAction(fishList);

        int forkJoinPoolSize = 5;
        ForkJoinPool forkJoinPool = new ForkJoinPool(forkJoinPoolSize);
        forkJoinPool.invoke(searchFishAction);

        watch.stop();
        System.out.println("Done in " + watch.getTime() + " ms");
        // searchFishAction.go();
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

        int fishListSize = fishList.size();
        if (fishListSize <= LIMIT) {

            for (String[] fishRow : fishList) {

                if (fishRow[FISH_TYPE].equals("sting ray")) {

                    // assumption it will all be proper integer strings, hence no exception
                    int fishPrice = Integer.parseInt(fishRow[PRICE]);

                    if (fishPrice > 9000) {
                        System.out.println("score");
                    }

                }
            }

        } else {

            // hmmm
            invokeAll(
                    new SearchFishAction(fishList.subList(0, fishListSize / 2)),
                    new SearchFishAction(fishList.subList(fishListSize / 2, fishListSize))
            );
        }

    }

}
