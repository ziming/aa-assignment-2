package aa.recursive;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class SearchFishAction extends RecursiveAction {

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        SearchFishAction searchFishAction = new SearchFishAction();
        forkJoinPool.invoke(searchFishAction);
        
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
}
