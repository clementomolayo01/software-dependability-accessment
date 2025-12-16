package com.urlshortener.repository;

import com.urlshortener.entity.ShortUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findByShortCode_and_incrementClickCount_and_countByCreatedBy() {
        ShortUrl s = new ShortUrl("abc123", "https://example.com", "bob");
        s = shortUrlRepository.save(s);

        var fetched = shortUrlRepository.findByShortCode("abc123");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getClickCount()).isEqualTo(0L);

        // increment click count via repository @Modifying query
        shortUrlRepository.incrementClickCount("abc123");
        em.refresh(s);
        assertThat(shortUrlRepository.findByShortCode("abc123")).isPresent()
                .get().extracting(ShortUrl::getClickCount).isEqualTo(1L);

        // count by createdBy
        Long count = shortUrlRepository.countByCreatedBy("bob");
        assertThat(count).isEqualTo(1L);
    }
}
