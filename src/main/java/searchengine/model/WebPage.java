package searchengine.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "page")
public class WebPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "code", nullable = false)
    private int statusCode;

    @Column(name = "content", nullable = false)
    private String body;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    private List<Index>indices;

    private String ADDRESS;
    private List<WebPage> childrenList;
    private List<WebPage> parentList;
    private static Set<WebPage> connectedList = new HashSet<>();

    Connection.Response response;

    public WebPage(String address) throws IOException {
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
        }

        System.out.println("constructor of " + address + " " + statusCode);
    }
    public int getStatusCode(){
        return statusCode;
    }
    public List<WebPage> getChildrenList() {
        return childrenList;
    }
    public String getBody(){
        return replaceQuotes(body);
    }
    public String getADDRESS() {
        return ADDRESS;
    }
    public synchronized void setChildrenList(List<WebPage> childrenList) {
        this.childrenList = childrenList;
    }
    public List<WebPage> getParentList() {
        return parentList;
    }
    public synchronized void setParentList(List<WebPage> parentList) {
        this.parentList = parentList;
    }
    public Set<WebPage> getConnectedList() {
        return connectedList;
    }
    public synchronized static void setConnectedList(Set<WebPage> connectedList) {
        WebPage.connectedList = connectedList;
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
                    WebPage webPage = new WebPage(pageAddress);

                    if (parentList.contains(webPage) || webPage.equals(this) ||
                            this.childrenList.contains(webPage)) {
                        continue;
                    }
                    webPage.parentList.addAll(this.parentList);
                    webPage.parentList.add(this);
                    childrenList.add(webPage);
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





