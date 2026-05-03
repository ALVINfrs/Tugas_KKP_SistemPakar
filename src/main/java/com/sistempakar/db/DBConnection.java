package com.sistempakar.db;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Database Connection Singleton
 * Sistem Pakar Rekomendasi Jurusan
 */
public class DBConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "sistempakar_jurusan";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Sesuaikan dengan password MySQL Anda

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            showError("Driver MySQL tidak ditemukan!\nPastikan mysql-connector-java sudah ada di classpath.\n" + e.getMessage());
        } catch (SQLException e) {
            showError("Gagal terhubung ke database!\n\nHost  : " + HOST + "\nPort  : " + PORT +
                    "\nDB    : " + DATABASE + "\nUser  : " + USERNAME +
                    "\n\nError : " + e.getMessage() +
                    "\n\nPastikan MySQL server berjalan dan database sudah dibuat.");
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Helper: execute update (INSERT/UPDATE/DELETE)
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) throw new SQLException("Koneksi database tidak tersedia");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        }
    }

    /**
     * Helper: execute update and return generated key
     */
    public static int executeInsertGetKey(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) throw new SQLException("Koneksi database tidak tersedia");
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /**
     * Helper: execute query
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        if (conn == null) throw new SQLException("Koneksi database tidak tersedia");
        PreparedStatement ps = conn.prepareStatement(sql);
        setParams(ps, params);
        return ps.executeQuery();
    }

    private static void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) ps.setNull(i + 1, Types.NULL);
            else if (params[i] instanceof Integer) ps.setInt(i + 1, (Integer) params[i]);
            else if (params[i] instanceof Long) ps.setLong(i + 1, (Long) params[i]);
            else if (params[i] instanceof Double) ps.setDouble(i + 1, (Double) params[i]);
            else if (params[i] instanceof Boolean) ps.setBoolean(i + 1, (Boolean) params[i]);
            else if (params[i] instanceof java.util.Date)
                ps.setTimestamp(i + 1, new Timestamp(((java.util.Date) params[i]).getTime()));
            else ps.setString(i + 1, params[i].toString());
        }
    }
}
