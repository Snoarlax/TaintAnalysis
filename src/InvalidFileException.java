public class InvalidFileException extends Exception{

    InvalidFileException(String ErrorMessage){
        super(ErrorMessage);
    }
    InvalidFileException(String ErrorMessage, Throwable err){
        super(ErrorMessage, err);
    }
}
