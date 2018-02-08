package sftp;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by sgu197 on 5/12/2017.
 */
public class Test {

    public static void main(String[] args){

        SortedMap<Integer,String> map = new TreeMap<Integer,String>();

        map.put(1,"file1.txt");
        map.put(2,"file2.txt");
        map.put(5,"file3.txt");

        map.keySet().removeAll(Arrays.asList(map.keySet().toArray()).subList(0, 2));

        System.out.println(map.size());
        System.out.println(map.headMap(3).size());

        map.headMap(3).clear();
        System.out.println(map.size());

    }
}
