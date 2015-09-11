package aa.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class MyCallable implements Callable {

    public static void main(String[] args) {

        int poolSize = 10;
        Executors pool;
        //Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        //read from txt file and store into a list
        List<Future<String>> list = new ArrayList<Future<String>>();

        MyCallable task = new MyCallable();

        Future result = task.submit(new averageCoffeeReviewRatingCalculator(list));
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Object call() throws Exception {
        return null;
    }
}

class averageCoffeeReviewRatingCalculator implements Callable<Long> {

    List<Future<String>> list = new ArrayList<Future<String>>();

    public averageCoffeeReviewRatingCalculator(List list){
        this.list = list;
    }

    @Override
    public Long call() throws Exception {
        return calculator(list);
    }

    private Long calculator (List list) throws InterruptedException {
        long result = 0;
       /*
        while ()
        while (n != 0) {
            result = n * result;
            n = n - 1;
            Thread.sleep(100);
        }
        */
        return result;
    }

}
