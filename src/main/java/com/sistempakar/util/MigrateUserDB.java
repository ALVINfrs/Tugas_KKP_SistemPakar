package com.sistempakar.util;

import com.sistempakar.db.DBConnection;

public class MigrateUserDB {
    public static void main(String[] args) {
        try {
            System.out.println("Starting migration...");
            
            // 1. Create log_aktivitas table
            String createLog = "CREATE TABLE IF NOT EXISTS log_aktivitas ("
                             + "id INT AUTO_INCREMENT PRIMARY KEY, "
                             + "user_id INT NOT NULL, "
                             + "aksi VARCHAR(255) NOT NULL, "
                             + "tanggal DATETIME DEFAULT CURRENT_TIMESTAMP"
                             + ")";
            DBConnection.executeUpdate(createLog);
            System.out.println("log_aktivitas table created or exists.");
            
            // 2. Add terakhir_login to pengguna
            try {
                DBConnection.executeUpdate("ALTER TABLE pengguna ADD COLUMN terakhir_login DATETIME");
                System.out.println("Added terakhir_login to pengguna.");
            } catch (Exception e) {
                System.out.println("terakhir_login might already exist: " + e.getMessage());
            }
            
            System.out.println("Migration completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
