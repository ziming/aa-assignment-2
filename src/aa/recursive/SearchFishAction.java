package aa.recursive;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class SearchFishAction extends RecursiveAction {

    public static void main(String[] args) {
        SearchFishAction searchFishAction = new SearchFishAction();
        ForkJoinPool forkJoinPool = new ForkJoinPool(1);
        forkJoinPool.invoke(searchFishAction);
        searchFishAction.go();
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

        /*
        if (true) {
            // search for stuff
            return;
        }
        */

        // otherwise
        // invokeAll(...)
    }

    public Reader getReader(String relativePath) throws UnsupportedEncodingException {
        return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
    }
}
