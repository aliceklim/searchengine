package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import searchengine.model.DBConnection;
import searchengine.model.SiteMapCreator;
import searchengine.model.WebPage;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
        //new ForkJoinPool().invoke(new SiteMapCreator(new WebPage("https://yogaplace.by/")));
       new ForkJoinPool().invoke(new SiteMapCreator(new WebPage("https://www.qcterme.com/it")));
        System.out.println(SiteMapCreator.processedLinks.size());
    }
}
