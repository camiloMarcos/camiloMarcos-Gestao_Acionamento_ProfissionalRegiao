package br.com.univida_test.demo.exceptions;

public class FieldMessage {

    private String field;
    private String defaultMessage;

    public FieldMessage() {}


    public FieldMessage(String field, String defaultMessage) {
        this.field = field;
        this.defaultMessage = defaultMessage;
    }


    public String getField() {
        return field;
    }
    public void setField(String field) { 
        this.field = field;  }

        
    public String getDefaultMessage() {
        return defaultMessage;
    }
    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }    

}
