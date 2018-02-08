import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sgu197 on 5/15/2017.
 */
public class RegexCriteria {

    public static final String EXAMPLE_TEST = "PV_BOD_1.writing";
            //"This is my small example string which I'm going to use for pattern matching.";

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile(".*\\.writing");
        // in case you would like to ignore case sensitivity,
        // you could use this statement:
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(EXAMPLE_TEST);
        //EXAMPLE_TEST.matches(pattern);
        // check all occurance
        while (matcher.find()) {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.println(matcher.group());
        }
        // now create a new pattern and matcher to replace whitespace with tabs

        String ip = "PV_(BOD|EOD|FIXING)_.*\\.zip,Risk_delta_.*\\.zip," +
                "PLS_BOND_CS01_.*\\.zip";
        HashSet<String> myHashSet = new HashSet(500000);  // Or a more realistic size
        StringTokenizer st = new StringTokenizer(ip, ",");
        while(st.hasMoreTokens())
            myHashSet.add(st.nextToken());

        System.out.println(produce(myHashSet));

        String completeRegex = produce(myHashSet);

        String filename = "Risk_delta_10.zip";
        System.out.println("Matches"+filename.matches(completeRegex));
        System.out.println("Matches"+filename.matches("PV_BOD_.*\\.zip"));
    }

    public static String produce(Set<String> patterns){

        Iterator<String> itr =  patterns.iterator();
        if( patterns.size() == 1){
            return itr.next();
        }

        String regex = "(";
        String end = ")";
        String OR = "|";

        while(itr.hasNext()){
            regex += OR + "(" + itr.next() + ")";

        }

        System.out.println("regex --->"+regex);
        regex = regex.replaceFirst("\\|","");
        System.out.println("regex --->"+regex);
        regex += end;
        return regex;
    }
}
