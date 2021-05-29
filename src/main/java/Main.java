import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final String URL = "https://skillbox.ru/";
    private static final int AMOUNT_POOL = 4;
    private static final HashSet<LinkObject> linksObSet = new HashSet<>();

    public static void main(String[] args) throws IOException {
        linksObSet.add(new LinkObject(URL, 0));

        ForkJoinPool pool = new ForkJoinPool(AMOUNT_POOL);
        String linksRaw = pool.invoke(new LinkRecursiveTask(URL));
        String[] arrayStr = linksRaw.split("\n");
        Set<String> linksSet = new TreeSet<>(Arrays.asList(arrayStr));

        writeFileAndAddSpace(linksSet);
    }

    public static void writeFileAndAddSpace(Set<String> links) throws IOException {
        FileWriter writer = new FileWriter("webSite.txt");
        for (String link : links) {
            String space = "\t";
            int count = linksObSet.stream()
                    .filter(o -> o.getUrl().equals(link))
                    .findFirst().orElseThrow().getCount();
            String result = space.repeat(count) + link;
            writer.write(result + "\n");
        }
        writer.flush();
        System.out.println("Файл записан");
    }

    public static HashSet<LinkObject> getLinksObSet() {
        return linksObSet;
    }
}
