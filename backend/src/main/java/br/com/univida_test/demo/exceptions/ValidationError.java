package br.com.univida_test.demo.exceptions;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {
 

List <FieldMessage> errors = new ArrayList<>();


    public ValidationError(LocalDateTime timestamp, Integer status, String message, String path) {
        super(timestamp, status, message, path);
    }


   public List<FieldMessage> getErrors() {
        return errors;
    }

    
    public void addError(String fieldName, String defsultMessage) {
       this.errors.add(new FieldMessage(fieldName, defsultMessage));
    }
 
    
}
