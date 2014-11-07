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
import java.util.ArrayList;

/**
 * Created by Carlos on 7/01/14.
 */
public class SQLiteHelperCIE10 extends SQLiteOpenHelper {
    private final Context myContext;
    private static final int DATABASE_VERSION = 1;
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
        mInvalidDatabaseFile = false;
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
            int read=0;
            while ((read = myInput.read(buffer)) != -1) {
                myOutput.write(buffer, 0, read);
            }
        }
        catch (IOException ex){
        }
        finally {
            if(myInput != null){
                try{ myInput.close(); }
                catch(IOException ex){ }
            }
            if(myOutput!=null){
                try{ myOutput.close(); }
                catch (IOException ex){ }
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
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    private void doUpgrade(){

    }

    public String[][] getCapitulos() {
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select numCapitulo, nombreCapitulo, descripCapitulo, codCapInicial, codCapFinal from capitulos ORDER BY numCapitulo", null);
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, new int[] { cursor.getCount(),5 });
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
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getGrupos(int cap){
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            String[] array = new String[1];
            array[0] = String.valueOf(cap);
            Cursor cursor = db.rawQuery("select numGrupo, nombreGrupo, codGrupoInicial, codGrupoFinal FROM grupos WHERE numCapitulo = ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),4 });
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
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCategorias(String codInicial, String codFinal){
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            String[] array = new String[2];
            array[0] = codInicial;
            array[1] = codFinal;
            Cursor cursor = db.rawQuery("select codCategoria, nombreCategoria, favorito FROM categorias WHERE codCategoria >= ? AND codCategoria <= ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),3 });
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
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }


    public boolean es_favorito(String codCat) {
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select favorito from CATEGORIAS WHERE codCategoria = ? ", new String[]{codCat});
            if(cursor.moveToFirst()){
                if(cursor.getInt(0) == 1)
                    return true;
                else
                    return false;
            }
            else
                return false;
        }catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getFavoritos(){
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT codCategoria, nombreCategoria, favorito  FROM CATEGORIAS WHERE favorito = ? ORDER BY codCategoria",new String[]{String.valueOf(1)});
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, new int[] {cursor.getCount(),3});
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
            cursor.close();
            return arrayOfString;
        }catch(SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

   public boolean setFavorito(String codCat){
        SQLiteDatabase db=null;
        try{
            boolean flag = false;
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FAVORITO",1);
            String[] whereArgs={codCat};
            int x = db.update("CATEGORIAS",values,"CODCATEGORIA = ? ",whereArgs);
            if (x > 0){
                flag=true;
            }
            return flag;
        } catch (SQLiteException ex){
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
            ContentValues values = new ContentValues();
            values.put("FAVORITO",0);
            String[] whereArgs={codCat};
            int x = db.update("CATEGORIAS",values,"CODCATEGORIA = ? ",whereArgs);
            if (x > 0){
                flag=true;
            }
            return flag;
        } catch (SQLiteException ex){
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
        String sqlLike = "SELECT numCapitulo, numGrupo, codCategoria, nombreCategoria, favorito FROM categorias WHERE (nombreCategoria LIKE '%" + cadena  + "%' OR codCategoria LIKE '%" + cadena  + "%')";
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery(sqlLike, null);
            int j = 0;
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, new int[] { cursor.getCount(),5});
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    arrayOfString[j][0] = cursor.getString(0);
                    arrayOfString[j][1] = cursor.getString(1);
                    arrayOfString[j][2] = cursor.getString(2);
                    arrayOfString[j][3] = cursor.getString(3);
                    arrayOfString[j][4] = cursor.getString(4);
                    j++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }
}
