package br.edu.ifpb.pps.projeto.modumender.validations;

public interface Validador<T> {
    void validar(T entity) throws IllegalArgumentException;
}
