package searchengine.parser;

import lombok.Data;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ParsingData {
    private int statusCode;
    private String body;
    private static Set<ParsingData> connectedList = new HashSet<>();
    private String ADDRESS;
    private List<ParsingData> childrenList;
    private List<ParsingData> parentList;

    Connection.Response response;
    public ParsingData(String address) {
    this.ADDRESS = address;
    childrenList = new ArrayList<>();
    parentList = new ArrayList<>();
        try{
        Connection.Response response = Jsoup.connect(ADDRESS).execute();
        statusCode = response.statusCode();
        body = response.body();
    } catch (HttpStatusException ex){
        statusCode = ex.getStatusCode();
        body = null;
    } catch (SocketTimeoutException ex){
    } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("constructor of " + address + " " + statusCode);
}
    public int getStatusCode(){
        return statusCode;
    }
    public List<ParsingData> getChildrenList() {
        return childrenList;
    }
    public String getBody(){
        return replaceQuotes(body);
    }
    public String getADDRESS() {
        return ADDRESS;
    }
    public synchronized void setChildrenList(List<ParsingData> childrenList) {
        this.childrenList = childrenList;
    }
    public List<ParsingData> getParentList() {
        return parentList;
    }
    public synchronized void setParentList(List<ParsingData> parentList) {
        this.parentList = parentList;
    }
    public Set<ParsingData> getConnectedList() {
        return connectedList;
    }
    public synchronized static void setConnectedList(Set<ParsingData> connectedList) {
        ParsingData.connectedList = connectedList;
    }

    public void parse(){
        try {
            connectedList.add(this);
            Thread.sleep(600);
            Document document = Jsoup.connect(ADDRESS).get();
            Elements links = document.select("a[href]");
            for (Element link : links){
                String pageAddress = link.attr("abs:href");
                if (matches(pageAddress)) {
                    ParsingData parsingData = new ParsingData(pageAddress);

                    if (parentList.contains(parsingData) || parsingData.equals(this) ||
                            this.childrenList.contains(parsingData)) {
                        continue;
                    }
                    parsingData.parentList.addAll(this.parentList);
                    parsingData.parentList.add(this);
                    childrenList.add(parsingData);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private boolean containsInvalidSymbol(String address){
        return address.contains("#");
    }
    private boolean matches(String pageAddress){
        return pageAddress.startsWith(parentList.isEmpty() ? ADDRESS : parentList.get(0).getADDRESS()) && !containsInvalidSymbol(pageAddress);
    }
    private String replaceQuotes(String s){
        try{
            String string = s.replace("\"", "\\\"");
            String newString = string.replace("'", " ");
            return newString;
        } catch (NullPointerException ex){
            return " ";
        }
    }



}
