package ru.yandex.practicum.filmrate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
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
    public Review getById(long id) throws EmptyResultDataAccessException {
        String sqlQuery = "WITH l AS (" +
                "SELECT rr.review_id, SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END) AS rating " +
                "FROM review_reactions rr " +
                "GROUP BY rr.review_id " +
                ") " +
                "SELECT r.id, r.user_id, r.film_id, r.text_review, r.is_positive, COALESCE(l.rating, 0) AS rating " +
                "FROM review r " +
                "LEFT JOIN l ON r.id = l.review_id " +
                "WHERE r.id = ? ";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
    }

    @Override
    public List<Review> getAllFilmReviews(int count, Long filmId) {
        List<Object> params = new ArrayList<>();

        String sqlQuery = "WITH l AS (" +
                "SELECT rr.review_id, SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END) AS rating " +
                "FROM review_reactions rr " +
                "GROUP BY rr.review_id " +
                ") " +
                "SELECT r.id, r.user_id, r.film_id, r.text_review, r.is_positive, COALESCE(l.rating, 0) AS rating " +
                "FROM review r " +
                "LEFT JOIN l ON r.id = l.review_id ";

        if (filmId != null) {
            sqlQuery += " WHERE r.film_id = ? ";
            params.add(filmId);
        }

        sqlQuery += "ORDER BY COALESCE(l.rating, 0) DESC " +
                "LIMIT ? ";
        params.add(count);

        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToReview, params.toArray()));
    }

    @Override
    public Review create(Review review) {
        String sqlQuery = "INSERT INTO review (user_id, film_id, text_review, is_positive) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, review.getUserId().intValue());
            stmt.setInt(2, review.getFilmId().intValue());
            stmt.setString(3, review.getTextReview());
            stmt.setBoolean(4, review.getIsPositiveReview());

            return stmt;
        }, keyHolder);

        review.setId(keyHolder.getKey().longValue());
        review.setRating(0);

        return review;
    }

    @Override
    public Review update(Review newReview) {
        String sqlQuery = "UPDATE review " +
                "SET film_id = ?, text_review = ?, is_positive = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                newReview.getFilmId(),
                newReview.getTextReview(),
                newReview.getIsPositiveReview(),
                newReview.getId());

        Review review = getById(newReview.getId());
        newReview.setRating(review.getRating());

        return newReview;
    }


    @Override
    public void like(Long id, Long userId) {
        String sqlQueryLikes = "INSERT INTO review_reactions (user_id, review_id, is_positive)" +
                "VALUES (?, ?, TRUE)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQueryLikes);
                stmt.setInt(1, userId.intValue());
                stmt.setInt(2, id.intValue());
                return stmt;
            });
        } catch (DuplicateKeyException e) {
            updateReaction(id, userId, true);
        }
    }

    @Override
    public void dislike(Long id, Long userId) {
        String sqlQueryDislikes = "INSERT INTO review_reactions (user_id, review_id, is_positive)" +
                "VALUES (?, ?, FALSE)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQueryDislikes);
                stmt.setInt(1, userId.intValue());
                stmt.setInt(2, id.intValue());
                return stmt;
            });
        } catch (DuplicateKeyException e) {
            updateReaction(id, userId, false);
        }
    }


    @Override
    public void delete(long id) {
        String sqlQuery = "DELETE FROM review " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM review_reactions " +
                "WHERE user_id = ? AND review_id = ? AND is_positive = TRUE";

        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        String sqlQuery = "DELETE FROM review_reactions " +
                "WHERE user_id = ? AND review_id = ? AND is_positive = FALSE";

        jdbcTemplate.update(sqlQuery, userId, id);
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

    private void updateReaction(Long id, Long userId, boolean isPositive) {
        String sqlQuery = "UPDATE review_reactions " +
                "SET is_positive = ? " +
                "WHERE user_id = ? AND review_id = ? ";

        jdbcTemplate.update(sqlQuery, isPositive, userId, id);
    }
}
