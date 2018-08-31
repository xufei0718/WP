package com.mybank.pc.admin;

import cn.hutool.core.util.ObjectUtil;
import com.mybank.pc.Consts;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.merchant.cust.MerchantCustSrv;
import com.mybank.pc.merchant.model.MerchantFee;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeCtr extends CoreController {
	private MerchantCustSrv merchantCustSrv = enhance(MerchantCustSrv.class);

	// 首页查询手续费组件服务
	public void fee() {

		// 获取当前登录用户信息
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		StringBuffer tradeType1 = new StringBuffer(
				"select * from merchant_fee mf  where 1=1 and mf.dat is null  and mf.merID=? and mf.tradeType='1'  order by mf.amountLower desc ");
		StringBuffer tradeType2 = new StringBuffer(
				"select * from merchant_fee mf  where 1=1 and mf.dat is null  and mf.merID=? and mf.tradeType='2'  order by mf.amountLower desc");
		List<MerchantFee> feeListJ = MerchantFee.dao.find(tradeType1.toString(), merInfo.getId());
		List<MerchantFee> feeListB = MerchantFee.dao.find(tradeType2.toString(), merInfo.getId());

		Map map = new HashMap();
		map.put("feeListJ", feeListJ);
		map.put("feeListB", feeListB);
		map.put("merInfo", merInfo);
		renderJson(map);

	}

	// 首页各项数字的累计
	public void total() {
		int custCount;
		MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
		if (ObjectUtil.isNotNull(merInfo)) {
			custCount = merchantCustSrv.totalLastCust(merInfo.getId());
		} else {
			custCount = merchantCustSrv.totalLastCust(null);
		}


		Map map = new HashMap();
		map.put("amount", "0");
		map.put("tradeCount", "0");
		map.put("custCount", custCount);
		map.put("feeAmount", "0");
		renderJson(map);
	}

}
