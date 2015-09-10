package aa.lb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTimeData {

    public static void main(String[] args) {
        System.out.println("request_id,request_parameter,processing_time,time_in_queue,performed_on_worker,total_time_elapsed");

        BufferedReader br = null;

        // Forgotten Regex already...sigh
        // Request 106 req parameter = 0; processing time ~14 ms; time in queue ~1659 ms.  preformed on (worker 0).  Total elapsed time so far ~ 2109
        Pattern pattern = Pattern.compile("(\\d+).+?(\\d+).+?(\\d+).+?(\\d+).+?(\\d+).+?(\\d+)");

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("timeData.txt"));


            while ((sCurrentLine = br.readLine()) != null) {
                Matcher m = pattern.matcher(sCurrentLine);
                if (m.find()) {
                    System.out.printf("%s,%s,%s,%s,%s,%s\n", m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
