package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    private Image image1;
    private Image image2;
    private Image image3;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();

        image1 = new Image(1, 100, "img1.jpg");
        image1.setFavori(true);

        image2 = new Image(1, 100, "img2.jpg");
        image2.setFavori(false);

        image3 = new Image(2, 200, "img3.jpg");
        image3.setFavori(true);

        imageRepository.save(image1);
        imageRepository.save(image2);
        imageRepository.save(image3);
    }

    @Test
    void testFindByArtid() {
        List<Image> images = imageRepository.findByArtid(100);
        assertEquals(2, images.size());
        assertTrue(images.stream().anyMatch(img -> img.getImage().equals("img1.jpg")));
        assertTrue(images.stream().anyMatch(img -> img.getImage().equals("img2.jpg")));
    }

    @Test
    void testFindByCustomidAndArtid() {
        List<Image> images = imageRepository.findByCustomidAndArtid(1, 100);
        assertEquals(2, images.size());
        assertTrue(images.contains(image1));
        assertTrue(images.contains(image2));

        images = imageRepository.findByCustomidAndArtid(2, 200);
        assertEquals(1, images.size());
        assertEquals(image3, images.get(0));
    }

    @Test
    void testFindByCustomidAndArtidOrderByFavoriDescIdAsc() {
        List<Image> images = imageRepository.findByCustomidAndArtidOrderByFavoriDescIdAsc(1, 100);
        assertEquals(2, images.size());
        assertEquals(image1.getId(), images.get(0).getId()); // favori = true en premier
        assertEquals(image2.getId(), images.get(1).getId()); // favori = false ensuite
    }

    @Test
    void testSaveAndVisibility() {
        Image img = new Image(3, 300, "img4.jpg");
        img.setVisibility(false);
        Image saved = imageRepository.save(img);

        assertNotNull(saved.getId());
        assertFalse(saved.isVisible());

        saved.setVisibility(true);
        Image updated = imageRepository.save(saved);
        assertTrue(updated.isVisible());
    }

    @Test
    void testFavoriFlag() {
        Image img = new Image(3, 301, "img5.jpg");
        img.setFavori(true);
        Image saved = imageRepository.save(img);

        assertTrue(saved.isFavori());

        saved.setFavori(false);
        Image updated = imageRepository.save(saved);
        assertFalse(updated.isFavori());
    }
}
