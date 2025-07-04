package next.dao;

import core.jdbc.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    public void update(String sql, PreparedStatementSetter setter) {
        try (Connection con = ConnectionManager.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            setter.setValues(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("update 실패 " + sql, e);
        }
    }

    public void update(String sql, Object... params) {
        try (Connection con = ConnectionManager.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("update 실패: " + sql, e);
        }
    }

    public <T> List<T> query(String sql, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
        try (Connection con = ConnectionManager.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            setter.setValues(pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("query 실패 "+ sql, e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        try (Connection con = ConnectionManager.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("query 실패 "+ sql, e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("query 실패: " + sql, e);
        }
    }

    public <T> T queryForObject(String sql, PreparedStatementSetter setter, RowMapper<T> rowMapper) {
        List<T> list = query(sql, setter, rowMapper);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> list = query(sql, rowMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

}
