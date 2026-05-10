package me.keremdurgut.zero_trust_personal_vault.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite veritabanı yardımcı sınıfı.
 * vault_items tablosunu oluşturur ve CRUD işlemlerini yönetir.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vault_database.db";
    private static final int DATABASE_VERSION = 1;

    // Tablo ve sütun adları
    public static final String TABLE_VAULT = "vault_items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_ENCRYPTED_PASSWORD = "encrypted_password";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_VAULT + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_ENCRYPTED_PASSWORD + " TEXT NOT NULL" +
                    ");";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VAULT);
        onCreate(db);
    }

    /**
     * Yeni bir parola kaydı ekler.
     * @return eklenen kaydın ID'si
     */
    public long insertItem(VaultItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_USERNAME, item.getUsername());
        values.put(COLUMN_ENCRYPTED_PASSWORD, item.getEncryptedPassword());
        long id = db.insert(TABLE_VAULT, null, values);
        return id;
    }

    /**
     * Mevcut bir parola kaydını günceller.
     * @return güncellenen satır sayısı
     */
    public int updateItem(VaultItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_USERNAME, item.getUsername());
        values.put(COLUMN_ENCRYPTED_PASSWORD, item.getEncryptedPassword());
        return db.update(TABLE_VAULT, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    /**
     * Bir parola kaydını siler.
     * @return silinen satır sayısı
     */
    public int deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_VAULT,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Tüm parola kayıtlarını getirir.
     */
    public List<VaultItem> getAllItems() {
        List<VaultItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VAULT, null, null, null, null, null,
                COLUMN_TITLE + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndex(COLUMN_ENCRYPTED_PASSWORD);

                VaultItem item = new VaultItem(
                        cursor.getLong(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(usernameIndex),
                        cursor.getString(passwordIndex)
                );
                items.add(item);
            }
            cursor.close();
        }
        return items;
    }

    /**
     * Belirli bir parola kaydını getirir.
     */
    public VaultItem getItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_VAULT, null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        VaultItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            int passwordIndex = cursor.getColumnIndex(COLUMN_ENCRYPTED_PASSWORD);

            item = new VaultItem(
                    cursor.getLong(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(usernameIndex),
                    cursor.getString(passwordIndex)
            );
            cursor.close();
        }
        return item;
    }

    /**
     * Tüm kayıtları siler.
     */
    public void deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_VAULT, null, null);
    }
}
