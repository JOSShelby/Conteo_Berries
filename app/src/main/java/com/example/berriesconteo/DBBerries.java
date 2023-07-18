package com.example.berriesconteo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBBerries extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "DBBerries.bd";
    private static final  int VERSION_BD=1;
    private static final String TABLA_CUBETASCONT="CREATE TABLE cubetascontadasberries" +
            "(idcubeta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "fecha TEXT, estacion TEXT, sector TEXT, invernadero TEXT, numero_empleado INTEGER" +
            "fruto TEXT, cubetas_cont INTEGER)";
    public DBBerries(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLA_CUBETASCONT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLA_CUBETASCONT);
        sqLiteDatabase.execSQL(TABLA_CUBETASCONT);
    }
    public void agregarCubetas(String fecha, String estacion, String sector, String invernadero, Integer numero_empleado, String fruto, Integer cubetas_cont){
        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){
            bd.execSQL("INSERT INTO cubetascontadasberries VALUES('"+fecha+"', '"+estacion+"', '"+sector+"', '"+invernadero+"', '"+numero_empleado+"', '"+fruto+"', '"+cubetas_cont+"')");
            bd.close();
        }
    }
}
