package ir.maktabsharif.util.exception;

public class ExistingEntityCannotBeFetchedException extends RuntimeException{
    public ExistingEntityCannotBeFetchedException() {
    }

    public ExistingEntityCannotBeFetchedException(String message) {
        super(message);
    }
}
