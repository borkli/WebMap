import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;

public class LinkRecursiveTask extends RecursiveTask<String> {

    private final String url;
    private static final HashSet<String> linksSet = new HashSet<>();

    public LinkRecursiveTask(String url) {
        this.url = url;
    }

    @Override
    protected String compute() {
        HashSet<LinkRecursiveTask> linkRecursiveSet = new HashSet<>();
        StringBuilder result = new StringBuilder(url + "\n");
        try {
            getLink(linkRecursiveSet);

            for (LinkRecursiveTask linkRecursiveTask : linkRecursiveSet) {
                result.append(linkRecursiveTask.join());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void getLink(HashSet<LinkRecursiveTask> linkRecursiveTasks) throws Exception {
        Thread.sleep(250);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a");

        for (Element element : links) {
            String absUrl = element.absUrl("href");
            if (getCheckUrl(absUrl)) {
                int urlCount = Main.getLinksObSet().stream()
                        .filter(o -> o.getUrl().equals(url)).findFirst()
                        .orElseThrow().getCount();
                Main.getLinksObSet().add(new LinkObject(absUrl, urlCount + 1));

                LinkRecursiveTask nextLink = new LinkRecursiveTask(absUrl);
                nextLink.fork();
                linkRecursiveTasks.add(nextLink);
                linksSet.add(absUrl);
            }
        }
    }

    private boolean getCheckUrl(String urlCheck) {
        Pattern pattern = Pattern.compile("(png|pdf|jpg|gif|#)");

        return urlCheck.startsWith(url) && !urlCheck.equals(url) &&
                !linksSet.contains(urlCheck) && !pattern.matcher(urlCheck).find();
    }
}
