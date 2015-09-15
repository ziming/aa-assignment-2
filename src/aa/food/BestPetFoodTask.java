package aa.food;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class BestPetFoodTask extends RecursiveTask<Map<String, double[]>> {

    // this is for the string[] in foodReviewList
    private static final int PRODUCT_ID = 0;
    private static final int USER_ID = 1;
    private static final int PROFILE_NAME = 2;
    private static final int HELPFULNESS = 3;
    private static final int SCORE = 4;
    private static final int TIME = 5;
    private static final int SUMMARY = 6;
    private static final int TEXT = 7;
    // this is for the result returned by the callable/recursivetask
    private static final int PRODUCT_TOTAL_SCORE = 0;
    private static final int PRODUCT_REVIEW_COUNT = 1;
    private static final int PRODUCT_AVERAGE_SCORE = 2;

    private static String[] petRelatedWords = {

            // animal names or related
            "dog",
            "cat",
            "bird",
            "fish",
            "reptile",
            "amphibian",
            "hare",
            "rabbit",
            "pet",
            "canine",
            "veterinary",

            // some animal product brands
            // got to check if they sell non animal products too and filter.
            "petsafe",
            "nylabone",
            "tetra", // fish food
            "science diet",
            "kaytee",
            "aqueon",
            "fluval",
            "plurina pro plan",
            "drinkwell"

    };
    private final int LIMIT = 100_000;
    private List<String[]> foodReviewList;

    public BestPetFoodTask(List<String[]> foodReviewList) {
        this.foodReviewList = foodReviewList;
    }

    /*
     * You will search through the reviews to find the best food for pets.
     * foods.txt has both human and pet food.
     * We only need pet food.
     *
     * Definition of best food:
     * add up total score, get average score.
     * If tie, count number of reviews. The more wins.
     *
     * What about product with very few reviews?
     * Get average review count every food product.
     *
     *
     */
    public static void main(String[] args) {

        List<String[]> foodReviewList = readFile();

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        BestPetFoodTask bestPetFoodTask = new BestPetFoodTask(foodReviewList);

        Map<String, double[]> mergedResult = forkJoinPool.invoke(bestPetFoodTask);

        // calculate average for every item
        int totalReviewCount = 0;

        for (double[] productResult : mergedResult.values()) {
            productResult[PRODUCT_AVERAGE_SCORE] = productResult[PRODUCT_TOTAL_SCORE] / productResult[PRODUCT_REVIEW_COUNT];
//            System.out.println(Arrays.toString(productResult));
            totalReviewCount += productResult[PRODUCT_REVIEW_COUNT];
        }

        double productAvgReviewCount = (double) totalReviewCount / mergedResult.size();

        // products with less than the average review counts are dropped from the short list of best.
        Iterator<Map.Entry<String, double[]>> iter = mergedResult.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, double[]> entry = iter.next();
            double[] productResult = entry.getValue();

            if (productResult[PRODUCT_REVIEW_COUNT] < productAvgReviewCount) {
                iter.remove();
            }

        }

        // So now nobody "can" complain the winner has too few reviews. Send it all to a list and sort it now.
        // dunno what am I doing now.
        List<FoodReviewStat> foodReviewStatList = new ArrayList<>();
        iter = mergedResult.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, double[]> entry = iter.next();
            String productId = entry.getKey();
            double[] productResult = entry.getValue();
            double totalScore = productResult[PRODUCT_TOTAL_SCORE];
            double reviewCount = productResult[PRODUCT_REVIEW_COUNT];
            double avgReviewScore = productResult[PRODUCT_AVERAGE_SCORE];

            FoodReviewStat foodReviewStat = new FoodReviewStat(productId, totalScore, reviewCount, avgReviewScore);
            foodReviewStatList.add(foodReviewStat);

        }

        Collections.sort(foodReviewStatList);

        // Print only the top 50. SG 50!
        for (FoodReviewStat foodReviewStat : foodReviewStatList.subList(0, 50)) {
            System.out.println(foodReviewStat);
        }

    }

    /*
     * Return only pet food reviews ideally.
     */
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

        List<String[]> foodReviewList = new ArrayList<>(568454);

        // Hmmm
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


                // read the empty line
                br.readLine();

                // only add to foodreview list if it is legit.
                for (String word : petRelatedWords) {

                    if (row[SUMMARY].contains(word) || row[TEXT].contains(word)) {
                        foodReviewList.add(row);
                        break;
                    }
                }


            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return foodReviewList;

    }

    public Map<String, double[]> compute() {

        // assume handle all first, assume all pet food at this stage.
        // keep it simple only go by score first.

        if (foodReviewList.size() <= LIMIT) {
            Map<String, double[]> foodReviewListMap = new HashMap<>();

            for (String[] foodReview : foodReviewList) {

                double score = Double.parseDouble(foodReview[SCORE]);
                String productId = foodReview[PRODUCT_ID];

                double[] productStat = foodReviewListMap.get(productId);

                if (productStat == null) {

                    // 3rd item to store average rating later.
                    productStat = new double[3];

                    productStat[PRODUCT_TOTAL_SCORE] = score;
                    productStat[PRODUCT_REVIEW_COUNT] = 1.0;

                    // add to food review list map.
                    foodReviewListMap.put(productId, productStat);

                } else {

                    productStat[PRODUCT_TOTAL_SCORE] += score;
                    productStat[PRODUCT_REVIEW_COUNT]++;

                }

            }

            return foodReviewListMap;
        } else {
            int foodReviewListSize = foodReviewList.size();
            BestPetFoodTask subTask1 = new BestPetFoodTask(foodReviewList.subList(0, foodReviewListSize / 2));
            BestPetFoodTask subTask2 = new BestPetFoodTask(foodReviewList.subList(foodReviewListSize / 2, foodReviewListSize));

            subTask1.fork();
            subTask2.fork();

            // now the join
            Map<String, double[]> result1 = subTask1.join();
            Map<String, double[]> result2 = subTask2.join();

            for (String keyStr : result2.keySet()) {

                if (result1.containsKey(keyStr)) {
                    double[] productStat = result1.get(keyStr);
                    productStat[PRODUCT_TOTAL_SCORE] += result2.get(keyStr)[PRODUCT_TOTAL_SCORE];
                    productStat[PRODUCT_REVIEW_COUNT] += result2.get(keyStr)[PRODUCT_REVIEW_COUNT];
                }

            }

            return result1;

        }

    }
}
