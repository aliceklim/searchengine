package searchengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.IOException;


@SpringBootApplication
public class Application {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
        //new ForkJoinPool().invoke(new SiteMapCreator(new WebPage("https://yogaplace.by/")));
        //new ForkJoinPool().invoke(new SiteMapCreator(new WebPage("https://www.qcterme.com/it")));
       // System.out.println(SiteMapCreator.processedLinks.size());
    }

}
