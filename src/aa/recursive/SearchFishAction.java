package aa.recursive;

import aa.StopWatch;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;


public class SearchFishAction extends RecursiveAction {

    public static void main(String[] args) {


        // Stop Watch Start!
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Create empty list for all fishes.
        List<String[]> allFishes = new LinkedList<>();

        // 1. Read all the files and add them to a list.
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        // creates a CSV parser
        CsvParser parser = new CsvParser(settings);

        // parses all rows in one go. fish0.dat to fish9.dat
        try {

            // on the 8th file fish7.dat the program seem to hang :(
            for (int i = 0; i < 1; i++) {
                allFishes.addAll(parser.parseAll(getReader(String.format("fish%d.dat", i))));
            }

            System.out.println(allFishes.size());

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.printf("Time taken (ms): %d\n", stopWatch.stop());


//        SearchFishAction searchFishAction = new SearchFishAction();
//        ForkJoinPool forkJoinPool = new ForkJoinPool(1);
//        forkJoinPool.invoke(searchFishAction);
        // searchFishAction.go();
    }

    public static Reader getReader(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        return new InputStreamReader(new FileInputStream(fileName), "UTF-8");
    }

    public void go() {

        BeanListProcessor<FishBean> rowProcessor = new BeanListProcessor<>(FishBean.class);

        CsvParserSettings parserSettings = new CsvParserSettings();
        CsvParser parser = new CsvParser(parserSettings);
        parserSettings.setRowProcessor(rowProcessor);

        // might be too big and run out of memory 1.5 million lines per file after all
        // if fail look at real all rows of a csv iterator style
        try {
            parser.parseAll(getReader("../../fish0.dat"));
            List<FishBean> fishBeans = rowProcessor.getBeans();

            for (FishBean fishBean : fishBeans) {

                // Getting prices of stingrays only
                if (fishBean.getPrice() >= 9000) {
                    if (fishBean.getFishType().equals("sting ray")){
                        System.out.println("TradeID: " + fishBean.getTradeID() + "- score!");
                    }
                }
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * The main computation performed by this task.
     */
    @Override
    protected void compute() {

        // if problem too small just do it yourself

        /*
        if (true) {
            // search for stuff
            return;
        }
        */

        // otherwise
        // invokeAll(...)
    }

    class FishBean {
        @Parsed(index = 0)
        private String tradeID;

        @Parsed(index = 3)
        private String fishType;

        @Parsed(index = 4)
        private Integer price;

        public String getTradeID() {
            return tradeID;
        }

        public String getFishType() {
            return fishType;
        }

        public Integer getPrice() {
            return price;
        }

        public String toString() {
            return "FishBean [tradeID=" + tradeID + ", fishType=" + fishType + ", price=" + price + "]";
        }
    }
}
