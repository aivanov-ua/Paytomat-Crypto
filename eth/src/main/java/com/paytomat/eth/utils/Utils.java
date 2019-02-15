package com.paytomat.eth.utils;

import com.paytomat.eth.abi.datatypes.Type;
import com.paytomat.eth.abi.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Alex Ivanov on 2019-02-15.
 */
public class Utils {

    @SuppressWarnings("unchecked")
    public static List<TypeReference<Type>> convert(List<TypeReference<?>> input) {
        List<TypeReference<Type>> result = new ArrayList<TypeReference<Type>>(input.size());

        for (TypeReference<?> typeReference:input) {
            result.add((TypeReference<Type>) typeReference);
        }

        return result;
    }

}
