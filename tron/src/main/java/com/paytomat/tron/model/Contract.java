package com.paytomat.tron.model;

import com.paytomat.tron.Address;

/**
 * created by Alex Ivanov on 2019-01-09.
 */
public class Contract {

    private final ContractParameter parameter;
    private final String type;

    public Contract(ContractParameter parameter, String type) {
        this.parameter = parameter;
        this.type = type;
    }

    public String getFrom() {
        return this.parameter.value.ownerAddress;
    }

    public String getTo() {
        return this.parameter.value.toAddress;
    }

    public static class ContractParameter {
        private final ParameterValue value;
        //type_url
        private final String typeUrl;

        public ContractParameter(ParameterValue value, String typeUrl) {
            this.value = value;
            this.typeUrl = typeUrl;
        }
    }

    public static class ParameterValue {
        private final long amount;
        //owner_address
        private final String ownerAddress;
        //to_address
        private final String toAddress;

        public ParameterValue(long amount, Address fromAddress, Address toAddress) {
            this.amount = amount;
            this.ownerAddress = fromAddress.toHex();
            this.toAddress = toAddress.toHex();
        }
    }

}
