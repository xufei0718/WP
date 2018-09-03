package com.mybank.pc.qrcode.wxacct;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.core.CoreValidator;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;

import java.util.List;

/**
 * 表单字段验证
 */
public class QrcodeWxValidator extends CoreValidator {
	public static final String MERCHANT_EXIST = "微信账号已经存在";



	@Override
	protected void validate(Controller controller) {
		QrcodeWxacct qrcodeWxacct = controller.getModel(QrcodeWxacct.class, "", true);
		String ak = getActionKey();
		LogKit.debug("ActionKey =" + ak);
		if (ak.equals("/qr00/save")) {

			List _list = MerchantInfo.dao.find("select id from qrcode_wxacct qw where qw.wxAcct=? and qw.dat is null", qrcodeWxacct.getWxAcct());
			if (!_list.isEmpty()) {
				addError(Consts.REQ_JSON_CODE.fail.name(), MERCHANT_EXIST);
				return;
			}


		}
	}

	@Override
	protected void handleError(Controller controller) {
		controller.renderJson(getErrorJSON());
	}
}
