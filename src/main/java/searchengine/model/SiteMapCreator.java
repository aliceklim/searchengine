package searchengine.model;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class SiteMapCreator extends RecursiveTask<List<String>> {
    public static  Set<WebPage> connectedList = Collections.synchronizedSet(new HashSet<>());
    public static  Set<String> processedLinks = Collections.synchronizedSet(new HashSet<>());
    private WebPage webPage;
    private static List<String> linksToPrint = new ArrayList<>();;
    private static List<String> linksToPrintSafeList = Collections.synchronizedList(linksToPrint);;

    public SiteMapCreator(WebPage webPage) {
        this.webPage = webPage;
    }

    @Override
    protected List<String> compute() {
        String address = webPage.getADDRESS();
        List<SiteMapCreator> taskList = new ArrayList<>();
        webPage.parse();
        for (WebPage page : webPage.getChildrenList()) {
            connectedList.addAll(page.getConnectedList());
            if (page.getParentList().contains(page) || page.equals(webPage)) {
                continue;
            }
            SiteMapCreator smcTask = new SiteMapCreator(page);
            smcTask.fork();
            taskList.add(smcTask);
            try {
                addToPrintListIfProcessed(page);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (SiteMapCreator task : taskList) {
            linksToPrintSafeList.addAll(task.join());
        }
        return linksToPrintSafeList;
    }
    private synchronized void addToPrintListIfProcessed(WebPage webPage) throws SQLException {
        String page = webPage.getADDRESS();
        if (!SiteMapCreator.processedLinks.contains(page)){
            SiteMapCreator.processedLinks.add(page);
            char someChar = '/';
            int count = 0;
            for (int i = 0; i < page.length(); i++) {
                if (page.charAt(i) == someChar) {
                    count++;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++ ){
                sb.append("\t");
            }
            DBConnection.addToDataBase(webPage.getADDRESS(), webPage.getStatusCode(), webPage.getBody());

        }
    }
}

