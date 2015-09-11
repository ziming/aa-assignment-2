package aa.callable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import java.util.concurrent.Executor;

public class Problem3 implements Callable<String> {

    public static void main(String[] args) {

        List<HashMap<String, String>> productList = new ArrayList<>();

        // TODO: Parse and add to the productList

        // productList is filled so now we continue.

        // Thread Pool Creation
        int poolSize = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        // Callables creation
        HashSet<ReviewStatTask> callables = new HashSet<>();
        int callablesCount = 50;
        int productListSize = productList.size();

        // assuming long is not needed
        int partitionSize = productList.size() / callablesCount;

        for (int i = 0; i < productListSize; i += partitionSize) {

            callables.add(
                    new ReviewStatTask(
                            "coffee",
                            productList.subList(i, Math.min(i + partitionSize, productListSize))
                    ));

        }

        try {
            List<Future<HashMap<String, Long>>> futures = executorService.invokeAll(callables);

            long totalProductWithSearchTermCount = 0;
            long totalReviewScore = 0;

            for (Future<HashMap<String, Long>> future : futures) {
                HashMap<String, Long> result = future.get();
                totalProductWithSearchTermCount += result.get("productWithSearchTermCount");
                totalReviewScore += result.get("partitionTotalReviewScore");
            }

            executorService.shutdown();

            double avgReviewRating = (double) totalReviewScore / totalProductWithSearchTermCount;
            System.out.printf("Average Review Rating is " + avgReviewRating);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public String call() throws Exception {

        // return the total review score and total number of reviews containing the word “coffee”.

        return "Hello";
    }

    static class ReviewStatTask implements Callable<HashMap<String, Long>> {

        private final String searchTerm;
        private final List<HashMap<String, String>> productList;

        public ReviewStatTask(String searchTerm, List<HashMap<String, String>> productList) {
            this.searchTerm = searchTerm;
            this.productList = productList;
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

            for (HashMap<String, String> product : productList) {

                // Assumption only need to search review/text
                String reviewText = product.get("review/text");

                // if didn't contain coffee, skip the rest of the loop
                if (!reviewText.contains(searchTerm)) {
                    continue;
                }

                // if reach here mean review contains "coffee so add the score!
                int productReviewScore = Integer.parseInt(product.get("review/score"));

                partitionTotalReviewScore += productReviewScore;
                productWithSearchTermCount++;

            }

            HashMap<String, Long> result = new HashMap<>();
            result.put("partitionTotalReviewScore", partitionTotalReviewScore);
            result.put("productWithSearchTermCount", productWithSearchTermCount);

            return result;

        }

    }
}
