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
public class AvailabilityFailure extends RuntimeException {

    String as;
    public AvailabilityFailure(String string) {
        super(string);
        this.as=string;
    }

    @Override
    public String toString() {
        return "AvailabilityFailure{" +as+ '}';
    }
    
}
