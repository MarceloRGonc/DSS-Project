/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author USER
 */
public class MembroNaoExisteException extends Exception{
    public MembroNaoExisteException(int id){
        super("Membro com id: "+id+" não existe");
    }
}