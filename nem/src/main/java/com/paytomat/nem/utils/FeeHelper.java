package com.paytomat.nem.utils;

import com.paytomat.nem.model.Mosaic;
import com.paytomat.nem.model.Xems;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.paytomat.nem.constants.Constants.MAX_MOSAIC_QUANTITY;
import static com.paytomat.nem.constants.Constants.MAX_XEM_FEE;
import static com.paytomat.nem.constants.Constants.MINIMAL_FEE_MSG_CHAR;
import static com.paytomat.nem.constants.Constants.MIN_FEE;
import static com.paytomat.nem.constants.Constants.MIN_FEE_CAP_AMOUNT;
import static com.paytomat.nem.constants.Constants.MIN_MOSAIC_SUPPLY;
import static com.paytomat.nem.constants.Constants.SUPPLY_ADJUSTMENT_FACTOR;

/**
 * created by Alex Ivanov on 2019-01-25.
 */
public class FeeHelper {

    private static Xems xemTransferFee(final Xems amount) {
        return Xems.fromMicro(Math.max(MIN_FEE, Math.min(((amount.getAsMicro() / MIN_FEE_CAP_AMOUNT) * MIN_FEE), MAX_XEM_FEE)));
    }

    private static Xems messageTransferFee(final int messageLength) {
        return messageLength > 0 ? Xems.fromMicro((1 + messageLength / MINIMAL_FEE_MSG_CHAR) * MIN_FEE) : Xems.ZERO;
    }

    private static Xems mosaicTransferFee(final Mosaic mosaic, final Xems amount) {
        if (mosaic == null) return Xems.ZERO;
        if (mosaic.divisibility == 0 && mosaic.totalSupply <= MIN_MOSAIC_SUPPLY) {
            return Xems.fromMicro(MIN_FEE);
        }
        double totalMosaicQuantity = mosaic.totalSupply * Math.pow(10, mosaic.divisibility);
        long supplyRelatedAdjustment = (long) Math.floor(SUPPLY_ADJUSTMENT_FACTOR * Math.log(MAX_MOSAIC_QUANTITY / totalMosaicQuantity));

        BigDecimal xemEquivalent = BigDecimal.valueOf(8_999_999_999L).multiply(BigDecimal.valueOf(amount.getAsMicro())).divide(BigDecimal.valueOf(totalMosaicQuantity), RoundingMode.DOWN);

        long microNemEquivalent = xemEquivalent.multiply(BigDecimal.valueOf(Xems.MICRO_IN_XEM)).longValue();
        long microNemEquivalentFee = Math.max(MIN_FEE, Math.min(((microNemEquivalent / MIN_FEE_CAP_AMOUNT) * MIN_FEE), MAX_XEM_FEE));

        long calculatedFee = microNemEquivalentFee - MIN_FEE * supplyRelatedAdjustment;
        return Xems.fromMicro(Math.max(MIN_FEE, calculatedFee));
    }

    public static Xems transferFee(final Mosaic mosaic, final Xems amount, final int payloadLength) {
        Xems fee;
        if (mosaic == null) {
            fee = xemTransferFee(amount);
        } else {
            fee = mosaicTransferFee(mosaic, amount);
        }
        fee.addXemsMicro(messageTransferFee(payloadLength).getAsMicro());
        return fee;
    }
}
