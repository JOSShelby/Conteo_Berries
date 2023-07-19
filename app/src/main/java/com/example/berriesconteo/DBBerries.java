package com.example.berriesconteo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBBerries extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "DBBerries.bd";
    private static final  int VERSION_BD=1;
    private static final String TABLA_CUBETASCONT = "CREATE TABLE cubetascontadasberries" +
            "(idcubeta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "fecha TEXT,moduloid integer, estacion integer, sector integer, numero_empleado INTEGER," +
            "fruto TEXT)";

    private static final String TABLA_MODULOS = "CREATE TABLE modulosberries(" +
            "idmodulo INTEGER  PRIMARY KEY AUTOINCREMENT," +
            "nombremodulo TEXT," +
            "status INTEGER" +
            ");";

    private static final String TABLA_SECTORES = "CREATE TABLE sectoresberries(" +
            "idsector INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombresector TEXT," +
            "idmodulo integer references modulosberries(idmodulo)," +
            "status integer" +
            ");";

    private static final String TABLA_ESTACIONES = "create table estacionberries(" +
            "idestacion INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombreestacion TEXT," +
            "status integer" +
            ");";

    public DBBerries(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, NOMBRE_BD, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_CUBETASCONT);//tabla cubetas
        db.execSQL(TABLA_MODULOS);//tabla modulos
        //haciendo los insert de la tabla

        try {
            String cadena = "INSERT INTO modulosberries(nombremodulo,status) VALUES ('MODULO_1',1), ('MODULO_2',1),('MODULO_3',1);";
            db.execSQL(cadena);
            // La inserción se realizó correctamente
        } catch (SQLException e) {
            // Ocurrió un error durante la inserción

            // Manejar la excepción según tus necesidades
        }

        db.execSQL(TABLA_SECTORES);//tabla sectores

        String cadenaSectores = "INSERT INTO sectoresberries (nombresector, idmodulo, status) VALUES ('SECTOR 1',1,1), ('SECTOR 2',1,1),('SECTOR 3',1,1),('SECTOR 4',1,1),('SECTOR 5',1,1),('SECTOR 6',1,1),('SECTOR 7',1,1),('SECTOR 8',1,1),('SECTOR 9',1,1),('SECTOR 10',1,1)" +
                ",('SECTOR 11',1,1),('SECTOR 12',1,1),('SECTOR 13',1,1),('SECTOR 14',1,1),('SECTOR 15',1,1),('SECTOR 16',1,1),('SECTOR 17',2,1),('SECTOR 18',2,1),('SECTOR 19',2,1),('SECTOR 20',2,1)" +
                ",('SECTOR 21',2,1),('SECTOR 22',2,1),('SECTOR 23',2,1),('SECTOR 24',2,1),('SECTOR 25',2,1),('SECTOR 26',2,1),('SECTOR 27',2,1),('SECTOR 28',2,1),('SECTOR 29',3,1),('SECTOR 30',3,1)" +
                ",('SECTOR 31',3,1),('SECTOR 32',3,1),('SECTOR 33',3,1),('SECTOR 34',3,1),('SECTOR 35',3,1),('SECTOR 36',3,1),('SECTOR 37',3,1),('SECTOR 38',3,1),('SEC',3,1);";
        db.execSQL(cadenaSectores);

        db.execSQL(TABLA_ESTACIONES);//tabla estaciones

        String cadenaEstacion = "INSERT INTO estacionberries (nombreestacion, status) " +
                "VALUES ('GALERA 1',1), ('GALERA 2',1),('GALERA 3',1),('GALERA 4',1),('GALERA 5',1),('GALERA 6',1),('GALERA 7',1),('GALERA 8',1),('GALERA 9',1),('GALERA 10',1)" +
                ",('GALERA 10,5',1),('GALERA 10.5',1),('GALERA 11',1),('GALERA 12',1),('GALERA 12 Y 13',1),('GALERA 13',1),('GALERA 14',1),('GALERA 15',1),('GALERA 16',1),('GALERA 17',1)" +
                ",('GALERA 18',1),('GALERA 19',1);";
        db.execSQL(cadenaEstacion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //tabla de las cubetas
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cubetascontadasberries " );
        sqLiteDatabase.execSQL(TABLA_CUBETASCONT);
        //tabla modulos
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS modulosberries");
        sqLiteDatabase.execSQL(TABLA_MODULOS);

        String cadena = "INSERT INTO modulosberries(nombremodulo,status) VALUES ('MODULO_1',1), ('MODULO_2',1),('MODULO_3',1);";
        sqLiteDatabase.execSQL(cadena);
        //tabla sectores
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS sectoresberries" );
        sqLiteDatabase.execSQL(TABLA_SECTORES);

        String cadenaSectores = "INSERT INTO sectoresberries (nombresector, idmodulo, status) VALUES ('SECTOR 1',1,1), ('SECTOR 2',1,1),('SECTOR 3',1,1),('SECTOR 4',1,1),('SECTOR 5',1,1),('SECTOR 6',1,1),('SECTOR 7',1,1),('SECTOR 8',1,1),('SECTOR 9',1,1),('SECTOR 10',1,1)" +
                ",('SECTOR 11',1,1),('SECTOR 12',1,1),('SECTOR 13',1,1),('SECTOR 14',1,1),('SECTOR 15',1,1),('SECTOR 16',1,1),('SECTOR 17',2,1),('SECTOR 18',2,1),('SECTOR 19',2,1),('SECTOR 20',2,1)" +
                ",('SECTOR 21',2,1),('SECTOR 22',2,1),('SECTOR 23',2,1),('SECTOR 24',2,1),('SECTOR 25',2,1),('SECTOR 26',2,1),('SECTOR 27',2,1),('SECTOR 28',2,1),('SECTOR 29',3,1),('SECTOR 30',3,1)" +
                ",('SECTOR 31',3,1),('SECTOR 32',3,1),('SECTOR 33',3,1),('SECTOR 34',3,1),('SECTOR 35',3,1),('SECTOR 36',3,1),('SECTOR 37',3,1),('SECTOR 38',3,1),('SEC',3,1);";
        sqLiteDatabase.execSQL(cadenaSectores);
        //tabla estacion
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS estacionberries" );
        sqLiteDatabase.execSQL(TABLA_ESTACIONES);

        String cadenaEstacion = "INSERT INTO estacionberries (nombreestacion, status) " +
                "VALUES ('GALERA 1',1), ('GALERA 2',1),('GALERA 3',1),('GALERA 4',1),('GALERA 5',1),('GALERA 6',1),('GALERA 7',1),('GALERA 8',1),('GALERA 9',1),('GALERA 10',1)" +
                ",('GALERA 10,5',1),('GALERA 10.5',1),('GALERA 11',1),('GALERA 12',1),('GALERA 12 Y 13',1),('GALERA 13',1),('GALERA 14',1),('GALERA 15',1),('GALERA 16',1),('GALERA 17',1)" +
                ",('GALERA 18',1),('GALERA 19',1);";
        sqLiteDatabase.execSQL(cadenaEstacion);
    }
    public void agregarCubetas(String fecha, String estacion, String sector, String invernadero, Integer numero_empleado, String fruto, Integer cubetas_cont){
        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){
            bd.execSQL("INSERT INTO cubetascontadasberries VALUES('"+fecha+"', '"+estacion+"', '"+sector+"', '"+invernadero+"', '"+numero_empleado+"', '"+fruto+"', '"+cubetas_cont+"')");
            bd.close();
        }
    }

    public void verTabla(){

        SQLiteDatabase bd=getWritableDatabase();
        if(bd!=null){

            String[] columns = {"nombremodulo"};

            Cursor cursor = bd.query("modulosberries",columns, null, null, null, null, null);

            while (cursor.moveToNext()) {

                String nombremodulo = cursor.getString(cursor.getColumnIndexOrThrow("nombremodulo"));

                Log.d("TablaModulos", " Nombre: " + nombremodulo);
            }
            bd.close();
        }
    }

}
