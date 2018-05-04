package com.demo.offlinelocation.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nishi on 4/30/2018.
 */

public class DBUpdation {

    private Context context;

    public DBUpdation(Context c) {

        context = c;

    }


    public static void tableUpgrade(SQLiteDatabase db, String sTblNm, String newTblSctipt) {

        db.beginTransaction();

        try {
            db.execSQL("ALTER TABLE " + sTblNm + " RENAME TO 'temp_" + sTblNm + "'");

            db.execSQL(newTblSctipt);

            String insertScript = "INSERT OR REPLACE INTO " + sTblNm
                    + "(" + getColumns(db, "temp_" + sTblNm) + ")SELECT "
                    + getColumns(db, "temp_" + sTblNm)
                    + " FROM temp_" + sTblNm + "";

            db.execSQL(insertScript);

            db.execSQL("DROP TABLE IF EXISTS temp_" + sTblNm);

            db.setTransactionSuccessful();

        } finally {

            db.endTransaction();
        }

    }

    private static String getColumns(SQLiteDatabase db, String TblNm) {


        Cursor c = db.query(TblNm, null, null, null, null, null, null);

        String[] columnNames = c.getColumnNames();

        String scol = "";

        for (int i = 0; i < columnNames.length; i++) {

//       if(TblNm.equalsIgnoreCase("temp_accmst")&&
            // columnNames[i].equalsIgnoreCase("type")){
//          scol += "varAcType" +",";
//       }else{
            scol += columnNames[i] + ",";
//       }

        }

        scol = scol.substring(0, scol.length() - 1);

        return scol;
    }
}
