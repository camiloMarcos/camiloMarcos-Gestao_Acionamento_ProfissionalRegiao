package br.com.univida_test.demo.exceptions;

import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;


//É uma anotação usada para tratar exceções e aplicar lógica comum a todos os controllers de um aplicativo Spring.
@ControllerAdvice
public class GlobalException {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

//É uma anotação do Spring Framework usada para tratar exceções de forma centralizada dentro de um Controller ou RestController.
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFoundException (ObjectNotFoundException e, HttpServletRequest request){
        StandardError se = new StandardError(LocalDateTime.now(),HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(se);
    }
//IllegalArgumentException em Java é uma exceção que acontece quando você passa um argumento inválido ou inadequados para um método.
//erro não está na lógica interna do método, mas sim nos valores passados pelo usuário ou pelo código chamador.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgumentException (IllegalArgumentException e, HttpServletRequest request){
        StandardError se = new StandardError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(se);
    }
//quando ocorre uma violação de integridade de dados em uma op no DB.Chave pri duplicada, Campos obrigatórios (NOT NULL) não preenchidos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolationException (DataIntegrityViolationException e, HttpServletRequest request){
        StandardError se = new StandardError (LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(se);
    }

    //FAZER O TREATMENT PARA MethodArgumentNotValidException -> AULA DE VALIDAÇÃO



}
