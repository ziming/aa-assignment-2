package aa.food;

public class FoodReviewStat implements Comparable<FoodReviewStat> {

    private String productId;
    private double totalScore;
    private double totalReviewCount;
    private double avgScore;

    public FoodReviewStat(String productId, double totalScore, double totalReviewCount, double avgScore) {
        this.productId = productId;
        this.totalScore = totalScore;
        this.totalReviewCount = totalReviewCount;
        this.avgScore = avgScore;
    }

    @Override
    public String toString() {
        return "FoodReviewStat{" +
                "productId='" + productId + '\'' +
                ", totalScore=" + totalScore +
                ", totalReviewCount=" + totalReviewCount +
                ", avgScore=" + avgScore +
                '}';
    }

    public String getProductId() {
        return productId;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getTotalReviewCount() {
        return totalReviewCount;
    }

    public double getAvgScore() {
        return avgScore;
    }

    @Override
    public int compareTo(FoodReviewStat foodReviewStat) {

        // aaa bad.
        if (avgScore > foodReviewStat.avgScore) {
            return -1;
        } else if (avgScore < foodReviewStat.avgScore) {
            return 1;
        } else {
            if (totalReviewCount > foodReviewStat.totalReviewCount) {
                return -1;
            } else if (totalReviewCount < foodReviewStat.totalReviewCount) {
                return 1;
            } else {
                return 0;
            }
        }


    }
}
