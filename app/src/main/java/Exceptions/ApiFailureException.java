/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author ashish
 */
public class ApiFailureException extends RuntimeException {

    public ApiFailureException(String response_Code_401) {
        super(response_Code_401); //To change body of generated methods, choose Tools | Templates.
    }
    
}
