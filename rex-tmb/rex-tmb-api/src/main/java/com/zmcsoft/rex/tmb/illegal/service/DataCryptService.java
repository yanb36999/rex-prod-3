package com.zmcsoft.rex.tmb.illegal.service;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DataCryptService {

    String decrypt(String data);

    String encrypt(String data);

    default void decryptProperty(Supplier<String> getter, Consumer<String> setter) {
        setter.accept(decrypt(getter.get()));
    }

    default void encryptProperty(Supplier<String> getter, Consumer<String> setter) {
        setter.accept(encrypt(getter.get()));
    }
}
