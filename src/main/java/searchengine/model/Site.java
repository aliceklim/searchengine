package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "status_time", nullable = false)
    private LocalDateTime statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "url", nullable = false)
    private String siteurl;

    @Column(name = "name", nullable = false)
    private String siteName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "site")
    private List<WebPage> pages;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "site")
    private List<Lemma> lemmas;
}
