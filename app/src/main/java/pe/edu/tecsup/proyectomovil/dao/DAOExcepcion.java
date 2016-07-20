package pe.edu.tecsup.proyectomovil.dao;

/**
 * Created by Renzo on 08/07/2016.
 */
public class DAOExcepcion extends Exception {

    private static final long serialVersionUID = 1L;

    //Comentando
    public DAOExcepcion() {
        super();
    }

    public DAOExcepcion(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DAOExcepcion(String detailMessage) {
        super(detailMessage);
    }

    public DAOExcepcion(Throwable throwable) {
        super(throwable);
    }

}