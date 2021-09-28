package com.example.demo.model;

public class Views {
    public interface UserView{}
    public interface DetailedView extends UserView{}
    public interface ProtectedView extends DetailedView{}
}
