package com.infernokun.amaterasu.models.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;

import java.util.List;

@Converter(autoApply = true)
public class IntegerListConverter extends JsonListConverter<Integer> {
    @Override
    protected TypeReference<List<Integer>> getTypeReference() {
        return new TypeReference<>() {
        };
    }

    @Override
    protected String getTypeName() {
        return "Integer";
    }
}