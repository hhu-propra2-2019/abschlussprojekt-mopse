package mops.persistence;

/**
 * Represents a 'file' of the file server.
 */
public interface FileInfo {
   /**
    * Display Name of a file.
    */
   String name = null;
   /**
    * Creation date of a file.
    */
   int date = 0;
   /**
    * Content Type of a File (pdf, png, etc.).
    */
   String contentType = null;
   /**
    * Byte size of a file.
    */
   int size = 0;
}
