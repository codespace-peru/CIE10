package pe.com.codespace.cie10;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

/**
 * Creado por Carlos on 7/01/14.
 */
public class SQLiteHelperCIE10 extends SQLiteOpenHelper {
    private final Context myContext;
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "cie10.db";
    private static final String DATABASE_PATH = "databases/";
    private static File DATABASE_FILE = null;
    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded  = false;
    private int mOpenConnections=0;
    private static SQLiteHelperCIE10 mInstance;

    public synchronized static SQLiteHelperCIE10 getInstance (Context context){
        if(mInstance == null){
            mInstance = new SQLiteHelperCIE10(context.getApplicationContext());
        }
        return mInstance;
    }

    private SQLiteHelperCIE10(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
            if(mInvalidDatabaseFile){
                copyDatabase();
            }
            if(mIsUpgraded){
                doUpgrade();
            }
        }
        catch(SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mInvalidDatabaseFile = true;
        mIsUpgraded = true;
    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        mOpenConnections++;
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public synchronized void close(){
        mOpenConnections--;
        if(mOpenConnections == 0){
            super.close();
        }
    }

    public void copyDatabase()  {
        AssetManager assetManager = myContext.getResources().getAssets();
        InputStream myInput = null;
        OutputStream myOutput = null;
        try{
            myInput = assetManager.open(DATABASE_PATH +DATABASE_NAME);
            myOutput = new FileOutputStream(DATABASE_FILE);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = myInput.read(buffer)) != -1) {
                myOutput.write(buffer, 0, read);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if(myInput != null){
                try{ myInput.close(); }
                catch(IOException ex){ex.printStackTrace(); }
            }
            if(myOutput!=null){
                try{ myOutput.close(); }
                catch (IOException ex){ex.printStackTrace();}
            }
            setDataBaseVersion();
            mInvalidDatabaseFile = false;
        }
    }

    private void setDataBaseVersion(){
        SQLiteDatabase db = null;
        try{
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version=" + DATABASE_VERSION);
        }
        catch (SQLiteException ex){
			ex.printStackTrace();
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    private void doUpgrade(){
        try{
            myContext.deleteDatabase(DATABASE_NAME);
            copyDatabase();
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    public String[][] getCapitulos() {
        SQLiteDatabase db = null;
        Cursor cursor=null;
        try{
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT idCapitulo, nombreCapitulo, descripCapitulo, codCapInicial, codCapFinal FROM capitulos ORDER BY idCapitulo", null);
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, cursor.getCount(),5);
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    arrayOfString[i][4] = cursor.getString(4);
                    i++;
                    cursor.moveToNext();
                }
            }
            return arrayOfString;
        }
        catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if (cursor!=null)
                cursor.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getGrupos(int cap){
        SQLiteDatabase db = null;
        Cursor cursor=null;
        try{
            db = getReadableDatabase();
            String[] array = new String[1];
            array[0] = String.valueOf(cap);
            cursor = db.rawQuery("SELECT numGrupo, nombreGrupo, codGrupoInicial, codGrupoFinal FROM grupos WHERE numCapitulo = ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, cursor.getCount(),4);
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    i++;
                    cursor.moveToNext();
                }
            }
            return arrayOfString;
        }
        catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if (cursor!=null)
                cursor.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCategorias(String codInicial, String codFinal){
        SQLiteDatabase db = null;
        Cursor cursor=null;

        try{
            db = getReadableDatabase();
            String[] array = new String[2];
            array[0] = codInicial;
            array[1] = codFinal;

            //db.beginTransaction();
            cursor = db.rawQuery("SELECT codCategoria, nombreCategoria, es_favorito FROM categorias WHERE codCategoria BETWEEN ? AND ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, cursor.getCount(), 3);
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    i++;
                    cursor.moveToNext();
                }
            }
            //db.setTransactionSuccessful();
            //db.endTransaction();
            //cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if (cursor!=null)
                cursor.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }


    public boolean es_favorito(String codCat) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT codCategory FROM favoritos WHERE codCategory = ? ", new String[]{codCat});
            return cursor.moveToFirst() && cursor.getString(0).equals(codCat);
        }catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if(cursor!=null)
                cursor.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getFavoritos(){
        SQLiteDatabase db = null;
        String[] array = new String[1];
        String[][] arrayOfString = null;
        Cursor cursor=null, cursor1=null;
        try {
            db = getReadableDatabase();
            cursor1 = db.rawQuery("SELECT codCategory FROM favoritos ORDER BY codCategory", null);
            int i = 0;
            arrayOfString = (String[][]) Array.newInstance(String.class, cursor1.getCount(), 4);
            if (cursor1.moveToFirst()) {
                while (!cursor1.isAfterLast()) {
                    array[0] = cursor1.getString(0);
                    cursor = db.rawQuery("SELECT codCategoria, nombreCategoria, numCapitulo, numGrupo FROM categorias WHERE codCategoria = ?", array);
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            arrayOfString[i][0] = cursor.getString(0);
                            arrayOfString[i][1] = cursor.getString(1);
                            arrayOfString[i][2] = cursor.getString(2);
                            arrayOfString[i][3] = cursor.getString(3);
                            i++;
                            cursor.moveToNext();
                        }
                    }
                    cursor1.moveToNext();
                }
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        finally {
            if(cursor!=null)
                cursor.close();
            if(cursor1!=null)
                cursor1.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
        return arrayOfString;
    }

   public boolean setFavorito(String codCat){
        SQLiteDatabase db=null;
        try{
            boolean flag = false;
            db = getWritableDatabase();
            db.beginTransaction();
            ContentValues values1 = new ContentValues();
            values1.put("codCategory",codCat);
            long x = db.insert("favoritos", null, values1);
            ContentValues values2 = new ContentValues();
            values2.put("es_favorito",1);
            String[] whereArgs={codCat};
            int y = db.update("categorias", values2, "codCategoria=?", whereArgs);
            if (x + y >= 2)
                flag=true;
            db.setTransactionSuccessful();
            db.endTransaction();
            return flag;
        } catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
   }

   public boolean eliminarFavorito(String codCat){
        SQLiteDatabase db = null;
        try{
            boolean flag = false;
            db = getWritableDatabase();
            db.beginTransaction();
            String[] whereArgs0={codCat};
            int x = db.delete("favoritos","codCategory = ?", whereArgs0);
            ContentValues values = new ContentValues();
            values.put("es_favorito",0);
            int y = db.update("categorias",values,"codCategoria=?",whereArgs0);
            if (x + y >= 2){
                flag=true;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            return flag;
        } catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
   }

    public String[][] searchTexto(String cadena) {
        SQLiteDatabase db = null;
        Cursor cursor=null, cursor1=null;
        String sqlLike = "SELECT numCapitulo, numGrupo, codCategoria, nombreCategoria FROM categorias WHERE (nombreCategoria LIKE '%" + cadena  + "%' OR codCategoria LIKE '%" + cadena  + "%') COLLATE NOCASE";
        try{
            db = getReadableDatabase();
            cursor = db.rawQuery(sqlLike, null);
            int j = 0;
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, cursor.getCount(),5);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    arrayOfString[j][0] = cursor.getString(0);
                    arrayOfString[j][1] = cursor.getString(1);
                    arrayOfString[j][2] = cursor.getString(2);
                    arrayOfString[j][3] = cursor.getString(3);
                    arrayOfString[j][4] = String.valueOf(0);
                    cursor1 = db.rawQuery("SELECT codCategory FROM favoritos WHERE codCategory = ?", new String[]{arrayOfString[j][2]});
                    if(cursor1!=null && cursor1.moveToFirst() && cursor1.getString(0).equals(arrayOfString[j][2]))
                        arrayOfString[j][4] = String.valueOf(1);
                    j++;
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                    cursor.moveToNext();
                }
            }
            return arrayOfString;
        }
        catch (SQLiteException ex){
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if(cursor!=null)
                cursor.close();
            if(cursor1!=null)
                cursor1.close();
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }
}
