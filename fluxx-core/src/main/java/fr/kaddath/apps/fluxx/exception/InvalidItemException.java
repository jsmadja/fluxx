package fr.kaddath.apps.fluxx.exception;

public class InvalidItemException extends Exception {

    private static final long serialVersionUID = -638292981563414815L;

    public InvalidItemException(String msg) {
        super(msg);
    }
}
