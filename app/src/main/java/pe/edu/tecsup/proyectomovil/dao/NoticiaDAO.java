package pe.edu.tecsup.proyectomovil.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renzo on 08/07/2016.
 */
public class NoticiaDAO {

    private DbHelper _dbHelper;

    public NoticiaDAO(Context c) {
        _dbHelper = new DbHelper(c);
    }

    public void insertar(String idNoticia, String titulo, String fecha) throws DAOExcepcion {
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        try {
            String[] args = new String[]{idNoticia, titulo, fecha};
            db.execSQL("INSERT INTO noticia(_id, titulo, fecha) VALUES(?, ?, ?)", args);
        } catch (Exception e) {
            throw new DAOExcepcion("NoticiaDAO: Error al insertar: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<Noticia> listar() throws DAOExcepcion {
        SQLiteDatabase db = _dbHelper.getReadableDatabase();
        List<Noticia> lista = new ArrayList<Noticia>();
        Noticia modelo = new Noticia();
        try {
            Cursor c = db.rawQuery("select _id, titulo, fecha from noticia", null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    int id = c.getInt(c.getColumnIndex("_id"));
                    String titulo = c.getString(c.getColumnIndex("titulo"));
                    String fecha = c.getString(c.getColumnIndex("fecha"));

                    modelo.setId(id);
                    modelo.setTitulo(titulo);
                    modelo.setFecha(fecha);

                    lista.add(modelo);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            throw new DAOExcepcion("NoticiaDAO: Error al listar: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return lista;
    }

    public Cursor listar2() throws DAOExcepcion {
        SQLiteDatabase db = _dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select _id, titulo, fecha from noticia ORDER BY fecha DESC", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
        }
        //c.close();
        return c;
    }

    public void eliminarTodos() throws DAOExcepcion {
        SQLiteDatabase db = _dbHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM noticia");
        } catch (Exception e) {
            throw new DAOExcepcion("NoticiaDAO: Error al eliminar todos: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

}
