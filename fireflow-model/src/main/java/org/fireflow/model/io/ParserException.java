

package org.fireflow.model.io;

import org.fireflow.model.InvalidModelException;

/**
 * 
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class ParserException extends InvalidModelException{

    /** 
     * Construct a new FPDLParserException. 
     */
    public ParserException(){
        super();
    }

    /** 
     * Construct a new FPDLParserException with the specified message.
     * @param message The error message
     */
    public ParserException(String message){
        super(message);
    }

    /** 
     * Construct a new FPDLParserException with the specified nested error.
     * @param t The nested error
     */
    public ParserException(Throwable t){
        super(t);
    }

    /** 
     * Construct a new FPDLParserException with the specified error message<br>
     * and nested exception.
     * @param message The error message
     * @param t The nested error
     */
    public ParserException(String message, Throwable t){
        super(message, t);
    }

}
