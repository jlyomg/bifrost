package com.dataour.bifrost.core.processor;

import com.dataour.bifrost.common.enums.BasedSourceEnum;
import com.dataour.bifrost.common.enums.OptTypeEnum;
import com.dataour.bifrost.common.module.param.ClientWareHouseInfo;
import com.dataour.bifrost.common.module.request.AdditionalChargeReq;
import com.dataour.bifrost.common.module.request.FreightChargeReq;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.util.BeanUtils;
import com.dataour.bifrost.common.util.ConvertUtils;
import com.dataour.bifrost.common.util.DateUtils;
import com.dataour.bifrost.core.engine.bifrostCategoriyEngine;
import com.dataour.bifrost.domain.RegularSurchargeDO;
import com.dataour.bifrost.domain.TransactionDO;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.dataour.bifrost.common.enums.RespCodeEnums.BIZ_ERROR;
import static com.dataour.bifrost.common.enums.RespCodeEnums.PARAM_ERROR;
import static com.dataour.bifrost.common.util.BigDecimalUtils.toBigDecimal;
import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;
import static com.dataour.bifrost.common.util.DataUtils.dataNotEmpty;
import static com.dataour.bifrost.common.util.ParamUtils.PARAM_RETURN_ORDER_ID;
import static com.dataour.bifrost.common.util.ParamUtils.genAttributes;


/**
 * @Author: JASON
 * @Date: 2024-01-11 10:38
 * @Description: 收费处理器
 */
@Slf4j
@Component
public class BillChargeProcessor {

    @Autowired
    private bifrostCategoriyEngine bifrostCategoriyEngine;
    @Autowired
    private ERPUserProcessor erpUserProcessor;

    public Resp<Boolean> saveAdditionalCharge(AdditionalChargeReq param) {
        TransactionDO transactionDO = BeanUtils.copyValue(param, TransactionDO.class);
        String yearStr = param.getYear();
        if (dataNotEmpty(yearStr)) {
            transactionDO.setYear(ConvertUtils.toInt(yearStr.split("-")[0], null));
            transactionDO.setWeek(ConvertUtils.toInt(yearStr.split("-")[1], null));
        }
        transactionDO.setBillCategoryCode(param.getCategoryCode());
        transactionDO.setOptType(OptTypeEnum.Additional.getCode());
        transactionDO.setAttributes(genAttributes(PARAM_RETURN_ORDER_ID, param.getReturnOrderId()));
        return bifrostCategoriyEngine.saveTransaction(transactionDO, null);
    }

    public Resp<Boolean> saveRegularSurcharge(RegularSurchargeDO regularSurcharge) {
        TransactionDO transactionDO = new TransactionDO();
        transactionDO.setBillCategoryCode(regularSurcharge.getBillCategoryCode());
        transactionDO.setQuantity(regularSurcharge.getChargeRate() == null ? "0" : Float.toString(regularSurcharge.getChargeRate().floatValue() / 100));
        transactionDO.setUnitPrice(regularSurcharge.getChargeValue());
        transactionDO.setClientCode(regularSurcharge.getClientCode());
        transactionDO.setDescription(regularSurcharge.getDescription());
        transactionDO.setOptType(OptTypeEnum.RegularSurcharge.getCode());
        ClientWareHouseInfo clientWareHouseInfo = erpUserProcessor.getClientWareHouseInfo(regularSurcharge.getClientCode(), DateUtils.getLastDayTimeRange(new Date()));
        if (clientWareHouseInfo != null) {
            BigDecimal quantity = toBigDecimal(transactionDO.getQuantity());
            if ((regularSurcharge.getBasedSource().equals(BasedSourceEnum.PALLET_QTY.getCode())) && (clientWareHouseInfo.getPalletQuantity() != null)) {
                transactionDO.setQuantity(quantity.multiply(toBigDecimal(clientWareHouseInfo.getPalletQuantity().toString())).toString());
            } else if ((regularSurcharge.getBasedSource().equals(BasedSourceEnum.ORDER_QTY.getCode())) && (clientWareHouseInfo.getOrderQuantity() != null)) {
                transactionDO.setQuantity(quantity.multiply(toBigDecimal(clientWareHouseInfo.getOrderQuantity().toString())).toString());
            } else if ((regularSurcharge.getBasedSource().equals(BasedSourceEnum.PURCHASE_ORDER_QTY.getCode())) && (clientWareHouseInfo.getPurchaseOrderQuantity() != null)) {
                transactionDO.setQuantity(quantity.multiply(toBigDecimal(clientWareHouseInfo.getPurchaseOrderQuantity().toString())).toString());
            }
        }
        Resp<Boolean> resp = bifrostCategoriyEngine.saveTransaction(transactionDO, null);
        if (!resp.isSuccess()) {
            // todo 发邮件警告
        }
        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    public Resp<Boolean> saveShippingCharge(List<FreightChargeReq> freightCharges) {
        if (dataEmpty(freightCharges)) {
            return Resp.error(PARAM_ERROR, "Param freightCharges is empty");
        }
        try {
            for (FreightChargeReq freightCharge : freightCharges) {
                TransactionDO transactionDO = new TransactionDO();
                transactionDO.setQuantity(freightCharge.getQuantity());
                transactionDO.setClientCode(freightCharge.getClientCode());
                transactionDO.setDescription(freightCharge.getDescription());
                transactionDO.setBillCategoryCode(freightCharge.getCategoryCode());
                transactionDO.setOptType(OptTypeEnum.ShippingSurcharge.getCode());
                Resp<Boolean> resp = bifrostCategoriyEngine.saveTransaction(transactionDO, null);
                if (!resp.isSuccess()) {
                    throw new RuntimeException("saveShippingCharge error");
                }
            }
            return Resp.success(true);
        } catch (Exception e) {
            log.error("saveShippingCharge error", e);
        }
        return Resp.error(BIZ_ERROR, "saveShippingCharge error");
    }
}
