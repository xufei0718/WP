package com.mybank.pc.kits;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.exception.ValidateCTRException;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.merchant.model.MerchantFee;

/**
 * 手续费工具类
 * 
 * @author hkun
 *
 */
public class FeeKit {

	private static int[] lenPriorityQueue = new int[] { 9, 6, 11, 8, 10, 7, 5, 4, 12, 3, 2 };

	private static final BigDecimal REALTIME_TYPE_0_MAX_FEE = new BigDecimal(1800);




	/**
	 * 
	 * 按圆计算商户手续费
	 * 
	 * @param amount
	 * @param merID
	 *            商户ID
	 * @param tradeType
	 *            1加急 2标准
	 * @return
	 */
	public static BigDecimal getMerchantFee(BigDecimal amount, int merID, String tradeType) {
		BigDecimal fee = new BigDecimal(0);
		MerchantFee merchantFee = findMerchantFee(amount, merID, tradeType);
		if (merchantFee == null) {
			throw new ValidateCTRException("符合金额[" + amount.toPlainString() + "]的手续费设置不存在!!");
		}

		String feeType = merchantFee.getFeeType();
		if (feeType.equals("1")) {// 定额
			fee = merchantFee.getAmount();
		} else if (feeType.equals("2")) {// 比例
			fee = amount.multiply(merchantFee.getRatio());
		}
		fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
		return fee;
	}

	/**
	 * @param amount
	 * @param merID
	 *            商户ID
	 * @param tradeType
	 *            1加急 2标准
	 * @return
	 */
	public static MerchantFee findMerchantFee(BigDecimal amount, int merID, String tradeType) {
		SqlPara sqlPara = Db.getSqlPara("collection_trade.findMerchantFee",
				Kv.create().set("amount", amount).set("merID", merID).set("tradeType", tradeType));
		return MerchantFee.dao.findFirst(sqlPara);
	}



}
