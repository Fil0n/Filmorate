package ru.yandex.practicum.filmrate.storage.review;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "SELECT * " +
                "FROM review " +
                "WHERE id = ? ";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью с таким id: " + id + " не существует.");
        }
    }

    @Override
    public List<Review> getAllFilmReviews(int count, Long filmId) {
        String sqlQuery = "SELECT * " +
                "FROM review " +
                "WHERE review.film_id = ? " +
                "ORDER BY rating DESC " +
                "LIMIT ?";

        checkFilmExists(filmId);

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count));
    }

    @Override
    public Review createReview(Review review) {
        String sqlQuery = "INSERT INTO review (user_id, film_id, text_review, rating, is_positive) " +
                "VALUES (?, ?, ?, ?, ?)";

        checkFilmExists(review.getFilmId());
        checkUserExists(review.getUserId());
        checkReviewExists(review.getUserId(), review.getFilmId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, review.getUserId().intValue());
            stmt.setInt(2, review.getFilmId().intValue());
            stmt.setString(3, review.getTextReview());
            stmt.setInt(4, 0);
            stmt.setBoolean(5, review.getIsPositiveReview());

            return stmt;
        }, keyHolder);

        review.setId(keyHolder.getKey().longValue());
        review.setRating(0);

        return review;
    }

    @Override
    public Review updateReview(Review newReview) {
        String sqlQuery = "UPDATE review " +
                "SET user_id = ?, film_id = ?, text_review = ?, is_positive = ? " +
                "WHERE id = ?";

        Review review = getReviewById(newReview.getId());

        checkFilmExists(newReview.getFilmId());
        checkUserExists(newReview.getUserId());

        int rowsAffected = jdbcTemplate.update(sqlQuery,
                newReview.getUserId(),
                newReview.getFilmId(),
                newReview.getTextReview(),
                newReview.getIsPositiveReview(),
                newReview.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Ревью с id " + newReview.getId() + " не найдено");
        }

        newReview.setRating(review.getRating());

        return newReview;
    }


    @Override
    public void likeReview(Long id, Long userId) {
        String sqlQueryLikes = "INSERT INTO review_reactions (user_id, review_id, is_positive)" +
                "VALUES (?, ?, TRUE)";

        getReviewById(id);
        checkUserExists(userId);
        checkReactionExists(id, userId, true);

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryLikes);
            stmt.setInt(1, userId.intValue());
            stmt.setInt(2, id.intValue());
            return stmt;
        });

        updateRatingReview(id);
    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        String sqlQueryDislikes = "INSERT INTO review_reactions (user_id, review_id, is_positive)" +
                "VALUES (?, ?, FALSE)";

        getReviewById(id);
        checkUserExists(userId);
        checkReactionExists(id, userId, false);

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryDislikes);
            stmt.setInt(1, userId.intValue());
            stmt.setInt(2, id.intValue());
            return stmt;
        });

        updateRatingReview(id);
    }

    @Override
    public void deleteReview(long id) {
        String sqlQuery = "DELETE FROM review " +
                "WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sqlQuery, id);

        if (rowsAffected == 0) {
            throw new NotFoundException("Ревью с таким id нет");
        }
    }

    @Override
    public void deleteLikeReview(long id, long userId) {
        String sqlQuery = "DELETE FROM review_reactions " +
                "WHERE user_id = ? AND review_id = ? AND is_positive = TRUE";

        int rowsAffected = jdbcTemplate.update(sqlQuery, userId, id);

        if (rowsAffected == 0) {
            throw new NotFoundException("Вы не ставили лайк этому фильму");
        }

        updateRatingReview(id);
    }

    @Override
    public void deleteDislikeReview(long id, long userId) {
        String sqlQuery = "DELETE FROM review_reactions " +
                "WHERE user_id = ? AND review_id = ? AND is_positive = FALSE";

        int rowsAffected = jdbcTemplate.update(sqlQuery, userId, id);

        if (rowsAffected == 0) {
            throw new NotFoundException("Вы не ставили дизлайк этому фильму");
        }

        updateRatingReview(id);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) {
        try {
            Review review = new Review();
            review.setId(resultSet.getLong("id"));
            review.setUserId(resultSet.getLong("user_id"));
            review.setFilmId(resultSet.getLong("film_id"));
            review.setTextReview(resultSet.getString("text_review"));
            review.setRating(resultSet.getInt("rating"));
            review.setIsPositiveReview(resultSet.getBoolean("is_positive"));

            return review;
        } catch (SQLException e) {
            throw new DataRetrievalFailureException("Ошибка при маппинге Review из ResultSet", e);
        }
    }

    private void updateRatingReview(long id) {
        String sqlQueryRating = "SELECT " +
                "COALESCE(SUM(CASE WHEN is_positive = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN is_positive = FALSE THEN 1 ELSE 0 END), 0) AS rating " +
                "FROM review_reactions " +
                "WHERE review_id = ?";

        String sqlQueryReview = "UPDATE review " +
                "SET rating = ? " +
                "WHERE id = ?";

        int rating = jdbcTemplate.queryForObject(sqlQueryRating, Integer.class, id) == null ? 0
                : jdbcTemplate.queryForObject(sqlQueryRating, Integer.class, id);

        jdbcTemplate.update(sqlQueryReview, rating, id);
    }

    private void checkUserExists(Long userId) {
        String sqlQueryUserExists = "SELECT COALESCE(COUNT(*), 0) " +
                "FROM users " +
                "WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(sqlQueryUserExists, Integer.class, userId);

        if (userCount == null || userCount == 0) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    private void checkFilmExists(Long filmId) {
        String sqlQueryFilmExists = "SELECT SELECT COALESCE(COUNT(*), 0) " +
                "FROM film " +
                "WHERE id = ?";

        Integer filmCount = jdbcTemplate.queryForObject(sqlQueryFilmExists, Integer.class, filmId);

        if (filmCount == null || filmCount == 0) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }

    private void checkReactionExists(Long id, Long userId, Boolean reaction) {
        String sqlQueryReactionExists = "SELECT is_positive " +
                "FROM review_reactions " +
                "WHERE review_id = ? AND user_id = ?";

        Boolean filmCount;

        try {
            filmCount = jdbcTemplate.queryForObject(sqlQueryReactionExists, Boolean.class, id, userId);
        } catch (EmptyResultDataAccessException e) {
            return;
        }

        if (filmCount.equals(reaction)) {
            throw new ValidationException("Вы уже оценили это ревью");
        } else if (reaction) {
            deleteDislikeReview(id, userId);
        } else {
            deleteLikeReview(id, userId);
        }
    }

    private void checkReviewExists(Long userId, Long filmId) {
        String sqlQueryReviewExists = "SELECT COALESCE(COUNT(*), 0) " +
                "FROM review " +
                "WHERE user_id = ? AND film_id = ?";

        Integer reviewCount = jdbcTemplate.queryForObject(sqlQueryReviewExists, Integer.class, userId, filmId);

        if (reviewCount != null && reviewCount > 0) {
            throw new ValidationException("Вы уже написали ревью к данному фильму");
        }
    }

}
