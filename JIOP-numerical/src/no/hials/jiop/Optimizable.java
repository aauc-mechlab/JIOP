/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.hials.jiop;
import no.hials.utilities.DoubleArray;

/**
 *
 * @author LarsIvar
 */
public interface Optimizable {
    
    public int getNumberOfFreeParameters();
    public void setFreeParameters(DoubleArray array);
    public DoubleArray getFreeParameters();
}
