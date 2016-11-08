package com.github.nizshee.shared.procedure;


import java.util.Optional;

public interface Registry {

    Optional<byte[]> executeDump(byte[] ip, byte[] bytes);
}
