package com.mybank.pc.merchant.info;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.core.CoreValidator;
import com.mybank.pc.merchant.model.MerchantInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 表单字段验证
 */
public class MerchantInfoValidator extends CoreValidator {
	public static final String MERCHANT_EXIST = "商户名称已被占用";



	@Override
	protected void validate(Controller controller) {
		MerchantInfo merInfo = controller.getModel(MerchantInfo.class, "", true);
		String ak = getActionKey();
		LogKit.debug("ActionKey =" + ak);
		if (ak.equals("/mer00/save")) {

			List _list = MerchantInfo.dao.find("select id from merchant_info where merchantName=?", merInfo.getMerchantName());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), MERCHANT_EXIST);
				return;
			}


		} else if (ak.equals("/mer00/update")) {

			User _user = User.dao.findFirst("select id from merchant_info where merchantName=? and id<>?", merInfo.getMerchantName(),
					merInfo.getId());
			if (_user != null) {
				addError(Consts.REQ_JSON_CODE.fail.name(), MERCHANT_EXIST);
				return;
			}
		}else if (ak.equals("/mer00/updateAmount")) {
			try{
			String amount = controller.getPara("amount");
			String merID = controller.getPara("merID");
			MerchantInfo mi =MerchantInfo.dao.findById(new Integer(merID));
			BigDecimal tAmount = new BigDecimal(amount);

			if (mi.getMaxTradeAmount().compareTo(tAmount) <0) {
				addError(Consts.REQ_JSON_CODE.fail.name(), "商户余额不足");
				return;
			}
			}catch (Exception e){
				e.printStackTrace();
				addError(Consts.REQ_JSON_CODE.fail.name(), "商户金额输入错误");
				return;
			}
		}
	}

	@Override
	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}
