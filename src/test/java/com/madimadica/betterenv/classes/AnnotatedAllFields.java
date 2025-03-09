package com.madimadica.betterenv.classes;

public class AnnotatedAllFields {
    @FieldAnnotation
    public int fPublic;
    @FieldAnnotation
    private int fPrivate;
    @FieldAnnotation
    protected int fProtected;
    @FieldAnnotation
    int fPackagePrivate;

}
