package com.github.nizshee.shared.util;


import com.github.nizshee.shared.exception.WrongDataException;

public interface Dumper<Value> {

    byte[] dump(Value value) throws WrongDataException;

    Value restore(byte[] bytes) throws WrongDataException;
}
