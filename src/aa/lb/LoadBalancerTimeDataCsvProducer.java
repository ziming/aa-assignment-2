package aa.lb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadBalancerTimeDataCsvProducer {

    public static void main(String[] args) {

        // Set intelliJ log to timeData.csv or java LoadBalancerTimeDataCsvProducer > timeData.csv

        System.out.println("request_id,request_parameter,processing_time,time_in_queue,performed_on_worker,total_time_elapsed");


        // Forgotten Regex already...sigh
        // Request 106 req parameter = 0; processing time ~14 ms; time in queue ~1659 ms.  preformed on (worker 0).  Total elapsed time so far ~ 2109
        Pattern pattern = Pattern.compile("(\\d+).+?(\\d+).+?(\\d+).+?(\\d+).+?(\\d+).+?(\\d+)");

        try (BufferedReader br = new BufferedReader(new FileReader("timeData.txt"))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                Matcher m = pattern.matcher(sCurrentLine);
                if (m.find()) {
                    System.out.printf("%s,%s,%s,%s,%s,%s\n", m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6));
                }

            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
