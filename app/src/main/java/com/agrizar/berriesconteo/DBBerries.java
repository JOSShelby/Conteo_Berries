package com.agrizar.berriesconteo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBBerries extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "DBBerries.bd";  //NOMBRE DE LA BASE DE DATOS
    private static final  int VERSION_BD=1; //VERSION

//    ESTRUCTURA DE LA TABLA CUBETASCONTADASBERRIES
    private static final String TABLA_CUBETASCONT = "CREATE TABLE cubetascontadasberries" +
                "(idcubeta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "fecha TEXT," +
            "moduloid integer references modulosberries(idmodulo)," +
            "estacion integer references estacionberries(idestacion)," +
            "sector integer references sectoresberries(idsector)," +
            "numero_empleado integer," +
            "fruto TEXT," +
            "status integer," +
            "bandera integer);";
//    ESTRUCTURA DE LA TABLA MODULOSBERRIES
    private static final String TABLA_MODULOS = "CREATE TABLE modulosberries(" +
            "idmodulo INTEGER  PRIMARY KEY AUTOINCREMENT," +
            "nombremodulo TEXT," +
            "status INTEGER" +
            ");";
//    ESTRUCTURA DE LA TABLA SECTORESBERRIES
    private static final String TABLA_SECTORES = "CREATE TABLE sectoresberries(" +
            "idsector INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombresector TEXT," +
            "idmodulo integer references modulosberries(idmodulo)," +
            "status integer" +
            ");";
//    ESTRUCTURA DE LA TABLA ESTACIONBERRIES
    private static final String TABLA_ESTACIONES = "create table estacionberries(" +
            "idestacion INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombreestacion TEXT," +
            "status integer" +
            ");";
//    NOMBRE DE LA BASE DE DATOS Y VERSION
    public DBBerries(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, NOMBRE_BD, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_CUBETASCONT);  //EJECUTA LA ESTRUCTURA PARA CREAR LA TABLA CUBETAS CONTADAS
        db.execSQL(TABLA_MODULOS);      //EJECUTA LA ESTRUCTURA PARA CREAR LA TABLA MODULOS
        db.execSQL(TABLA_SECTORES);     //EJECUTA LA ESTRUCTURA PARA CREAR LA TABLA SECTORES
        db.execSQL(TABLA_ESTACIONES);   //EJECUTA LA ESTRUCTURA PARA CREAR LA TABLA ESTACIONES

//      INSERT DE LA TABLA MODULOS
        try {
            String cadena = "INSERT INTO modulosberries(nombremodulo,status) VALUES ('MODULO 1',1), ('MODULO 2',1),('MODULO 3',1);";
            db.execSQL(cadena);
//            INSERT CON EXITO
        } catch (SQLException e) {

        }

//      INSERT DE LA TABLA SECTORES
        try{
            String cadenaSectores = "INSERT INTO sectoresberries (nombresector, idmodulo, status) VALUES ('SECTOR 1',1,1), ('SECTOR 2',1,1),('SECTOR 3',1,1),('SECTOR 4',1,1),('SECTOR 5',1,1),('SECTOR 6',1,1),('SECTOR 7',1,1),('SECTOR 8',1,1),('SECTOR 9',1,1),('SECTOR 10',1,1)" +
                    ",('SECTOR 11',1,1),('SECTOR 12',1,1),('SECTOR 13',1,1),('SECTOR 14',1,1),('SECTOR 15',1,1),('SECTOR 16',1,1),('SECTOR 17',2,1),('SECTOR 18',2,1),('SECTOR 19',2,1),('SECTOR 20',2,1)" +
                    ",('SECTOR 21',2,1),('SECTOR 22',2,1),('SECTOR 23',2,1),('SECTOR 24',2,1),('SECTOR 25',2,1),('SECTOR 26',2,1),('SECTOR 27',2,1),('SECTOR 28',2,1),('SECTOR 29',3,1),('SECTOR 30',3,1)" +
                    ",('SECTOR 31',3,1),('SECTOR 32',3,1),('SECTOR 33',3,1),('SECTOR 34',3,1),('SECTOR 35',3,1),('SECTOR 36',3,1),('SECTOR 37',3,1),('SECTOR 38',3,1);";
            db.execSQL(cadenaSectores);
//             INSERT CON EXITO
        }catch (SQLException e){

        }

//      INSERT DE LA TABLA ESTACION
        try{
            String cadenaEstacion = "INSERT INTO estacionberries (nombreestacion, status) " +
                    "VALUES ('ESTACIÓN 1',1), ('ESTACIÓN 2',1),('ESTACIÓN 3',1),('ESTACIÓN 4',1),('ESTACIÓN 5',1),('ESTACIÓN 6',1),('ESTACIÓN 7',1),('ESTACIÓN 8',1),('ESTACIÓN 9',1),('ESTACIÓN 10',1)" +
                    ",('ESTACIÓN 10.5',1),('ESTACIÓN 11',1),('ESTACIÓN 12',1),('ESTACIÓN 13',1),('ESTACIÓN 14',1),('ESTACIÓN 15',1),('ESTACIÓN 16',1),('ESTACIÓN 17',1)" +
                    ",('ESTACIÓN 18',1),('ESTACIÓN 19',1);";
            db.execSQL(cadenaEstacion);
        }catch (SQLException e){

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//      BORRAR Y CREAR LA TABLA CUBETAS SI YA EXISTE
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cubetascontadasberries " );
        sqLiteDatabase.execSQL(TABLA_CUBETASCONT);

//      BORRAR Y CREAR LA TABLA MODULOS SI YA EXISTE
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS modulosberries");
        sqLiteDatabase.execSQL(TABLA_MODULOS);

//      BORRAR Y CREAR LA TABLA SECTORES SI YA EXISTE
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS sectoresberries" );
        sqLiteDatabase.execSQL(TABLA_SECTORES);

//      BORRAR Y CREAR LA TABLA ESTACIONES SI YA EXISTE
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS estacionberries" );
        sqLiteDatabase.execSQL(TABLA_ESTACIONES);

//      INSERT DE LA TABLA MODULOS
        try {
            String cadena = "INSERT INTO modulosberries(nombremodulo,status) VALUES ('MODULO 1',1), ('MODULO 2',1),('MODULO 3',1);";
            sqLiteDatabase.execSQL(cadena);
        }catch (SQLException e){

        }

//      INSERT DE LA TABLA SECTORES
        try {
            String cadenaSectores = "INSERT INTO sectoresberries (nombresector, idmodulo, status) VALUES  ('SECTOR 1',1,1), ('SECTOR 2',1,1),('SECTOR 3',1,1),('SECTOR 4',1,1),('SECTOR 5',1,1),('SECTOR 6',1,1),('SECTOR 7',1,1),('SECTOR 8',1,1),('SECTOR 9',1,1),('SECTOR 10',1,1)" +
                    ",('SECTOR 11',1,1),('SECTOR 12',1,1),('SECTOR 13',1,1),('SECTOR 14',1,1),('SECTOR 15',1,1),('SECTOR 16',1,1),('SECTOR 17',2,1),('SECTOR 18',2,1),('SECTOR 19',2,1),('SECTOR 20',2,1)" +
                    ",('SECTOR 21',2,1),('SECTOR 22',2,1),('SECTOR 23',2,1),('SECTOR 24',2,1),('SECTOR 25',2,1),('SECTOR 26',2,1),('SECTOR 27',2,1),('SECTOR 28',2,1),('SECTOR 29',3,1),('SECTOR 30',3,1)" +
                    ",('SECTOR 31',3,1),('SECTOR 32',3,1),('SECTOR 33',3,1),('SECTOR 34',3,1),('SECTOR 35',3,1),('SECTOR 36',3,1),('SECTOR 37',3,1),('SECTOR 38',3,1);";
            sqLiteDatabase.execSQL(cadenaSectores);
        }catch (SQLException e){

        }

//      INSERT DE LA TABLA ESTACION
        try{
            String cadenaEstacion = "INSERT INTO estacionberries (nombreestacion, status) " +
                    "VALUES ('ESTACIÓN 1',1), ('ESTACIÓN 2',1),('ESTACIÓN 3',1),('ESTACIÓN 4',1),('ESTACIÓN 5',1),('ESTACIÓN 6',1),('ESTACIÓN 7',1),('ESTACIÓN 8',1),('ESTACIÓN 9',1),('ESTACIÓN 10',1)" +
                    ",('ESTACIÓN 10.5',1),('ESTACIÓN 11',1),('ESTACIÓN 12',1),('ESTACIÓN 13',1),('ESTACIÓN 14',1),('ESTACIÓN 15',1),('ESTACIÓN 16',1),('ESTACIÓN 17',1)" +
                    ",('ESTACIÓN 18',1),('ESTACIÓN 19',1);";
            sqLiteDatabase.execSQL(cadenaEstacion);
        }catch (SQLException e){

        }
    }
}
