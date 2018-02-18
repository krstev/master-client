package com.krstev.masterclient.models;

import java.util.List;

/**
 * @author fkrstev
 * Created on 17-Feb-18
 */

public class PrimeNumbers {
    private List<Integer> primeNumbers;

    public PrimeNumbers() {
    }

    public PrimeNumbers(List<Integer> primeNumbers) {
        this.primeNumbers = primeNumbers;
    }

    public List<Integer> getPrimeNumbers() {
        return primeNumbers;
    }

    public void setPrimeNumbers(List<Integer> primeNumbers) {
        this.primeNumbers = primeNumbers;
    }
}
