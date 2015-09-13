package aa.callable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import java.util.concurrent.Executor;

public class Problem3 {

    private static final int PRODUCT_ID = 0;
    private static final int USER_ID = 1;
    private static final int PROFILE_NAME = 2;
    private static final int HELPFULNESS = 3;
    private static final int SCORE = 4;
    private static final int TIME = 5;
    private static final int SUMMARY = 6;
    private static final int TEXT = 7;

    public static void main(String[] args) {

        // all the food!
        List<String[]> foodList = readFile();

        // foodList is filled so now we continue.

        // Thread Pool Creation
        int poolSize = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        // Callables creation
        HashSet<ReviewStatTask> callableSet = new HashSet<>();


        // this is assuming the later divide is clean. i.e. % callableCount == 0.
        int callableCount = 50;
        int productListSize = foodList.size();

        // assuming long is not needed
        int partitionSize = foodList.size() / callableCount;

        for (int i = 0, j = 1; i < productListSize; i += partitionSize, j++) {

            if (j != callableCount) {

                callableSet.add(
                        new ReviewStatTask(
                                "coffee",
                                foodList.subList(i, i + partitionSize)
                        ));

            } else {
                callableSet.add(
                        new ReviewStatTask(
                                "coffee",
                                foodList.subList(i, productListSize))
                );

                // if it's the last you break out of the loop of course
                break;
            }

        }

        // just to check
//        System.out.println(callableSet.size());

        try {
            List<Future<HashMap<String, Long>>> futures = executorService.invokeAll(callableSet);

            long totalProductWithSearchTermCount = 0;
            long totalReviewScore = 0;

            for (Future<HashMap<String, Long>> future : futures) {
                HashMap<String, Long> result = future.get();
                totalProductWithSearchTermCount += result.get("PRODUCT_WITH_SEARCH_TERM_COUNT");
                totalReviewScore += result.get("PARTITION_TOTAL_REVIEW_SCORE");
            }

            executorService.shutdown();

            double avgReviewRating = (double) totalReviewScore / totalProductWithSearchTermCount;
            System.out.printf("Average Review Rating is " + avgReviewRating);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static List<String[]> readFile() {

        /*
            product/productId: B0026Y3YBK
            review/userId: A38BUM0OXH38VK
            review/profileName: singlewinder
            review/helpfulness: 0/0
            review/score: 5.0
            review/time: 1347667200
            review/summary: Best everyday cookie!
            review/text: In the 1980s I spent several

            product/productId: B0026Y3YSS
            ...
         */

        List<String[]> foodList = new ArrayList<>(568454);

        // kind of bad here.. works can le, hope no bugs!
        try (BufferedReader br = new BufferedReader(new FileReader("foods.txt"))) {

            String currentLine;

            while ((currentLine = br.readLine()) != null) {

                String[] row = new String[8];

                row[0] = currentLine.replace("product/productId: ", "");

                currentLine = br.readLine();
                row[1] = currentLine.replace("review/userId: ", "");

                currentLine = br.readLine();
                row[2] = currentLine.replace("review/profileName: ", "");

                currentLine = br.readLine();
                row[3] = currentLine.replace("review/helpfulness: ", "");

                currentLine = br.readLine();
                row[4] = currentLine.replace("review/score: ", "");

                currentLine = br.readLine();
                row[5] = currentLine.replace("review/time: ", "");

                currentLine = br.readLine();
                row[6] = currentLine.replace("review/summary: ", "");

                currentLine = br.readLine();
                row[7] = currentLine.replace("review/text: ", "");

//                System.out.println(Arrays.toString(row));

                // read the empty line
                br.readLine();

                foodList.add(row);

            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return foodList;

    }

    static class ReviewStatTask implements Callable<HashMap<String, Long>> {

        private final String searchTerm;
        private final List<String[]> foodList;

        public ReviewStatTask(String searchTerm, List<String[]> foodList) {
            this.searchTerm = searchTerm;
            this.foodList = foodList;
        }

        @Override
        public HashMap<String, Long> call() {

            // that will return the total review score and total number of reviews containing the word “coffee”.
            // Compute the average review rating based on the return values.

            // for now I assume all the review/score are integers.
            long partitionTotalReviewScore = 0;
            long productWithSearchTermCount = 0;

            /*
                product/productId: B0026Y3YBK
                review/userId: A38BUM0OXH38VK
                review/profileName: singlewinder
                review/helpfulness: 0/0
                review/score: 5.0
                review/time: 1347667200
                review/summary: Best everyday cookie!
                review/text: In the 1980s I spent several
             */

            for (String[] food : foodList) {

                // Assumption only need to search review/text
                String reviewText = food[Problem3.TEXT];

                // if didn't contain coffee, skip the rest of the loop
                if (!reviewText.contains(searchTerm)) {
                    continue;
                }

                // if reach here mean review contains "coffee so add the score!
                // all the score are 1.0, 2.0, 3.0, 4.0, 5.0 so far... I didn't see things like 3.5 or 5.5 after an
                // exhaustive search. But just to play safe I will still use double
                long productReviewScore = (long) Double.parseDouble(food[Problem3.SCORE]);

                partitionTotalReviewScore += productReviewScore;
                productWithSearchTermCount++;

            }

            HashMap<String, Long> result = new HashMap<>();
            result.put("PARTITION_TOTAL_REVIEW_SCORE", partitionTotalReviewScore);
            result.put("PRODUCT_WITH_SEARCH_TERM_COUNT", productWithSearchTermCount);

            return result;

        }

    }
}
