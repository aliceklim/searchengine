package searchengine.parser;
import searchengine.model.DBConnection;
import searchengine.model.WebPage;

import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class SiteMapCreator extends RecursiveTask<List<String>> {
    public static  Set<ParsingData> connectedList = Collections.synchronizedSet(new HashSet<>());
    public static  Set<String> processedLinks = Collections.synchronizedSet(new HashSet<>());
    private ParsingData parsingData;
    private static List<String> linksToPrint = new ArrayList<>();;
    private static List<String> linksToPrintSafeList = Collections.synchronizedList(linksToPrint);;

    public SiteMapCreator(ParsingData parsingData) {
        this.parsingData = parsingData;
    }

    @Override
    protected List<String> compute() {
        String address = parsingData.getADDRESS();
        List<SiteMapCreator> taskList = new ArrayList<>();
        parsingData.parse();
        for (ParsingData page : parsingData.getChildrenList()) {
            connectedList.addAll(page.getConnectedList());
            if (page.getParentList().contains(page) || page.equals(parsingData)) {
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
    private synchronized void addToPrintListIfProcessed(ParsingData parsingData) throws SQLException {
        String page = parsingData.getADDRESS();
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
            DBConnection.addToDataBase(parsingData.getADDRESS(), parsingData.getStatusCode(), parsingData.getBody());

        }
    }
}

