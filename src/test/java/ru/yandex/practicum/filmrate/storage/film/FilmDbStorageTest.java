//package ru.yandex.practicum.filmrate.storage.film;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.yandex.practicum.filmrate.model.Film;
//import ru.yandex.practicum.filmrate.model.User;
//import ru.yandex.practicum.filmrate.service.FilmService;
//import ru.yandex.practicum.filmrate.storage.user.UserDbStorage;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//class FilmDbStorageTest {
//    private final FilmDbStorage filmDbStorage;
//
//    @Autowired
//    private final FilmService filmService;
//    private final UserDbStorage userDbStorage;
//    Film film;
//    Film film2;
//    User user;
//    User user2;
//
//
//    @BeforeEach
//    void setUp() {
//        film = Film.builder()
//                .name("name")
//                .description("desc")
//                .releaseDate(LocalDate.of(1999, 8, 17))
//                .duration(136)
//                .build();
//        //film.setGenres(new HashSet<>());
//        //film.setLikes(new HashSet<>());
//        film.setRating(1);
//
//        film2 = Film.builder()
//                .name("name2")
//                .description("desc")
//                .releaseDate(LocalDate.of(1999, 8, 17))
//                .duration(136)
//                .build();
////        film2.setGenres(new HashSet<>());
////        film2.setLikes(new HashSet<>());
//        film2.setRating(2);
//
//        user = User.builder()
//                .email("mail@mail.mail")
//                .login("login")
//                .birthday(LocalDate.of(1999, 8, 17))
//                .build();
////        user.setFriends(new HashSet<>());
//
//        user2 = User.builder()
//                .email("gmail@gmail.gmail")
//                .login("nelogin")
//                .birthday(LocalDate.of(2001, 6, 19))
//                .build();
////        user2.setFriends(new HashSet<>());
//    }
//
//    @Test
//    void addFilmTest() {
//        filmDbStorage.create(film);
//        assertEquals(film, filmDbStorage.read(film.getId()));
//    }
//
//    @Test
//    void updateFilmTest() {
//        filmDbStorage.create(film);
//        assertEquals(film, filmDbStorage.read(film.getId()));
//
//
//        film.setDescription("some new description");
//        filmDbStorage.update(film);
//        assertEquals("updateName", filmDbStorage.read(film.getId()).getName());
//    }
//
////    @Test
////    void likeAndDeleteLikeTest() {
////        Film filmTest = filmDbStorage.create(film);
////        User user1Test = userDbStorage.create(user);
////        User user2Test = userDbStorage.create(user2);
////        filmDbStorage.addLike(filmTest, user1Test);
////        filmDbStorage.addLike(filmTest, user2Test);
////        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
////        assertEquals(2, film.getLikes().size());
////
////        filmDbStorage.deleteLike(1, 1);
////        film.setLikes(likeDbStorage.getLikesForCurrentFilm(film.getId()));
////        assertEquals(1, film.getLikes().size());
////    }
//
//    @Test
//    void getRatingTest() {
//        filmDbStorage.create(film);
//        User userTest = userDbStorage.create(user);
//        User user2Test = userDbStorage.create(user2);
//        filmDbStorage.addLike(film, userTest);
//        filmDbStorage.addLike(film, user2Test);
//        assertEquals(1, filmService.getMostPopular(1).stream().findFirst().orElseThrow());
//    }
//
//
//}
