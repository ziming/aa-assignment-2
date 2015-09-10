package aa.lb;

import java.util.Random;

public class LoadBalancerExercise {

    private final int PATTERN_CHOICE = Service.EASY_PATTERN;
    Service[] workers;
    private Random random;

    public LoadBalancerExercise(int numWorkers) {

        workers = new Service[numWorkers];

        for (int i = 0; i < numWorkers; i++) {
            //Service has three load patterns: EASY_PATTERN, PATTERN_2 (Spike), PATTERN_3 (Cache)
            workers[i] = new Service(String.format("worker %d", i + 1), PATTERN_CHOICE);
        }

        random = new Random();

    }

    public static void main(String[] args) throws InterruptedException {

        // Specify number of workers in LoadBalancerExercise constructor
        LoadBalancerExercise lbe = new LoadBalancerExercise(2);
        lbe.go();
    }

    /**
     * HERE IS WHERE YOU WILL IMPLEMENT YOUR LOAD BALANCER.  Each request comes here.  You write code to decide
     * which worker it will tag to.  You may use any algorithm you wish.  You may want to try something simple, *look
     * at the response times, then implement something better*.
     *
     * @param requestID        pass this to the service to identify the request
     * @param requestParameter simulates the parameters/data/info set to the service
     */

    private void balance(int requestID, int requestParameter) {

        // See the respective balance methods

        //The given code has no balancing - everything goes to worker_1.


        // currently a basic round robin algorithm, change it to your liking! 12854, 12787
//        if (requestID % 250 == 0) {
//            workers[0].service(requestID, requestParameter);
//            spikeFlag = true;
//        } else if (spikeFlag) {
//            workers[1].service(requestID, requestParameter);
//        }

//        workers[requestID % workers.length].service(requestID, requestParameter);

        //1st attempt at new algorithm, check queue size ~9141

        // Give every 251 to worker 2. 19766, 19796
//        if (requestID % 250 != 0) {
//            workers[0].service(requestID, requestParameter);
//        } else {
//            workers[1].service(requestID, requestParameter);
//        }

//        if(workers[0].currentQueueSize() < workers[1].currentQueueSize()){
//            workers[0].service(requestID, requestParameter); //sends the request to worker 1
//        }else {
//            workers[1].service(requestID, requestParameter); //sends the request to worker 2
//        }

        /*//2nd attempt at new algorithm, weighted round robin + check queue size ~9062
        if(requestID % 3 == 0){
            workers[0].service(requestID, requestParameter); //sends the request to worker 1
        }else if(workers[1].currentQueueSize() < workers[0].currentQueueSize()){
            workers[1].service(requestID, requestParameter); //sends the request to worker 2
        }else{
            workers[0].service(requestID, requestParameter); //sends the request to worker 1
        }*/


    }

    private void basicRoundRobinBalance(int requestID, int requestParameter) {
        workers[requestID % workers.length].service(requestID, requestParameter);
    }

    private void randomBalance(int requestID, int requestParameter) {
        workers[random.nextInt(workers.length)].service(requestID, requestParameter);
    }

    private void evenSizeTaskQueueBalance(int requestID, int requestParameter) {

        // get the worker with lowest queue size
        Service selectedWorker = getWorkerWithLowestQueueSize();
        selectedWorker.service(requestID, requestParameter);
    }

    private Service getWorkerWithLowestQueueSize() {


        // this 1 if only 2 worker
//        return (workers[0].currentQueueSize() < workers[1].currentQueueSize()) ? workers[0] : workers[1];


        // this 1 for any amount of workers.
        // aim for clarity, though when the worker count get large, the code may need to be improved.

        int leastTasksIndex = 0;

        for (int i = 0; i < workers.length; i++) {

            int currentWorkerQueueSize = workers[i].currentQueueSize();
            int currentLeastTasksWorkerQueueSize = workers[leastTasksIndex].currentQueueSize();

            if (currentWorkerQueueSize <= currentLeastTasksWorkerQueueSize) {
                leastTasksIndex = i;
            }

        }

        return workers[leastTasksIndex];

    }

    /**
     * Launches two 'workers' to provide service for requests.  These are implemented as separate threads, so they
     * will run in parallel if you have > 1 core.
     * <p/>
     * Note that in this exercise, requests are sent much faster than they can be processed (the workers queue them up).
     * This may affect what algorithm is most optimal.
     * <p/>
     * You can ask the worker for the number of requests currently waiting in queue by calling:
     * worker.currentQueueSize();  //returns int
     * <p/>
     * All requests sent to a single worker are processed in the order received.  The service will output the time
     * to do actual processing for each request, and the total response time (~time in queue + processing) for that
     * request.
     */
    private void go() throws InterruptedException {
        Thread[] threads = new Thread[workers.length];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(workers[i]);
        }

        for (Thread thread : threads) {
            thread.start();
        }

//        new Thread(worker_1).start();  //"launch" the first worker
//        new Thread(worker_2).start();  //"launch" the second worker

        //total number of "requests" to process.  You can play with this, but it shouldn't make much difference.

        int numRequests = 1000;

        for (int requestID = 0; requestID < numRequests; requestID++) {
            Thread.sleep(4);
            int requestParameter = random.nextInt(5); //there are different values the client can send in the request; this represents "search for ..."

            // uncomment and test

//            basicRoundRobinBalance(requestID, requestParameter);
//            randomBalance(requestID, requestParameter);

            evenSizeTaskQueueBalance(requestID, requestParameter);

        }

        //workers will stop after all requests are processed
        for (Service worker : workers) {
            worker.stop();
        }

//        worker_1.stop();
//        worker_2.stop();
    }

}
