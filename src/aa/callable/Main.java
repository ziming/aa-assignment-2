package aa.callable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        //arrayList of all the HashMaps
        List<HashMap<String, String>> listHashMap = new ArrayList<>();
        //remove all the white spaces
        /*
        Scanner scanner = new Scanner(new File("test.txt"));
		PrintStream out = new PrintStream(new File("outfile.txt"));
		while(scanner.hasNextLine()){
    		String line = scanner.nextLine();
    		line = line.trim();
    		if(line.length() > 0) {
        		out.println(line);
			}
		}
		*/
        //introduce the buffered reader
        //add a for loop to count the number of times of items then add a new hashmap into the array
        BufferedReader in = new BufferedReader(new FileReader("/Users/Melody/Desktop/test.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i <= 7; i++) {
                String parts[] = line.split(":", -1);
                String key = parts[0];
                String value = parts[1];
                map.put(parts[0], parts[1]);
            }
            listHashMap.add(map);
            System.out.println(map.toString());
        }
        in.close();

    }
}