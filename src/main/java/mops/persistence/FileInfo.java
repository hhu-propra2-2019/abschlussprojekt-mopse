package mops.persistence;

import java.time.LocalDateTime;

/**
 * Represents a 'file' of the file server.
 */
public interface FileInfo {
   /**
    * @return file id.
    */
   long getId();

   /**
    * @return display name.
    */
   String getFileName();

   /**
    * @return creation date.
    */
   LocalDateTime getCreationDate();
   /**
    * @return content type (pdf, png, etc.).
    */

   String getContentType();
   /**
    * @return size in bytes.
    */
   long getSize();
}
