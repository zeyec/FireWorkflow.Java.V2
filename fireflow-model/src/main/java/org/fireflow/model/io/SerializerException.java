

package org.fireflow.model.io;

import org.fireflow.model.InvalidModelException;

@SuppressWarnings("serial")
public class SerializerException extends InvalidModelException{

    /** 
     * Construct a new FPDLSerializerException. 
     */
    public SerializerException(){
        super();
    }

    /** 
     * Construct a new FPDLSerializerException with the given error message.
     * @param message The error message
     */
    public SerializerException(String message){
        super(message);
    }

    /** 
     * Construct a new FPDLSerializerException with the given nested error.
     * @param t The nested error
     */

    public SerializerException(Throwable t){
        super(t);
    }

    /** 
     * Construct a new FPDLSerializerException with the given error message<br>
     * and nested error.        
     * @param message The error message
     * @param t The error
     */
    public SerializerException(String message, Throwable t){
        super(message, t);
    }

}
