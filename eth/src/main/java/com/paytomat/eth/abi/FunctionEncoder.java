package com.paytomat.eth.abi;

import com.paytomat.core.util.HashUtil;
import com.paytomat.eth.abi.datatypes.StaticArray;
import com.paytomat.eth.abi.datatypes.Type;
import com.paytomat.eth.abi.datatypes.num.Uint;
import com.paytomat.eth.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Alex Ivanov on 2019-02-15.
 */
public class FunctionEncoder {

    public static String encode(Function function) {
        List<Type> parameters = function.getInputParameters();

        String methodSignature = buildMethodSignature(function.getName(), parameters);
        String methodId = buildMethodId(methodSignature);

        StringBuilder result = new StringBuilder();
        result.append(methodId);

        return encodeParameters(parameters, result);
    }

    private static String encodeParameters(List<Type> parameters, StringBuilder result) {
        int dynamicDataOffset = getLength(parameters) * Type.MAX_BYTE_LENGTH;
        StringBuilder dynamicData = new StringBuilder();

        for (Type parameter:parameters) {
            String encodedValue = TypeEncoder.encode(parameter);

            if (TypeEncoder.isDynamic(parameter)) {
                String encodedDataOffset = TypeEncoder.encodeNumeric(
                        new Uint(BigInteger.valueOf(dynamicDataOffset)));
                result.append(encodedDataOffset);
                dynamicData.append(encodedValue);
                dynamicDataOffset += encodedValue.length() >> 1;
            } else {
                result.append(encodedValue);
            }
        }
        result.append(dynamicData);

        return result.toString();
    }

    private static int getLength(List<Type> parameters) {
        int count = 0;
        for (Type type:parameters) {
            if (type instanceof StaticArray) {
                count += ((StaticArray) type).getValue().size();
            } else {
                count++;
            }
        }
        return count;
    }

    private static String buildMethodSignature(String methodName, List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        result.append(methodName);
        result.append("(");

        List<String> types = new ArrayList<String>(parameters.size());
        String params = "";

        for (int i = 0; i < parameters.size(); i++) {
            Type type = parameters.get(i);

            params += type.getTypeAsString();
            if (i + 1 < parameters.size()) {
                params += ",";  // no whitespace
            }

            types.add(type.getTypeAsString());
        }

        result.append(params);
        result.append(")");
        return result.toString();
    }

    private static String buildMethodId(String methodSignature) {
        byte[] input = methodSignature.getBytes();
        byte[] hash = HashUtil.sha3(input);
        return Numeric.toHexString(hash).substring(0, 10);
    }
}
