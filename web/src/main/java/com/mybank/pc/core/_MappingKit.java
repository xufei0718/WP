package com.mybank.pc.core;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.mybank.pc.admin.model.*;
import com.mybank.pc.merchant.model.*;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import com.mybank.pc.trade.model.TradeLog;

/**
 * Generated by JFinal, do not modify this file.
 * 
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {

		arp.addMapping("merchant_info", "id", MerchantInfo.class);
		arp.addMapping("merchant_user", "id", MerchantUser.class);
		arp.addMapping("merchant_amount_log", "id", MerchantAmountLog.class);
		arp.addMapping("s_attachment", "id", Attachment.class);
		arp.addMapping("s_content", "id", Content.class);
		arp.addMapping("s_mapping", "id", Mapping.class);
		arp.addMapping("s_param", "id", Param.class);
		arp.addMapping("s_res", "id", Res.class);
		arp.addMapping("s_role", "id", Role.class);
		arp.addMapping("s_role_res", "id", RoleRes.class);
		arp.addMapping("s_taxonomy", "id", Taxonomy.class);
		arp.addMapping("s_ufile", "id", Ufile.class);
		arp.addMapping("s_user", "id", User.class);
		arp.addMapping("s_user_role", "id", UserRole.class);
		arp.addMapping("log_op", "id", LogOp.class);
		arp.addMapping("qrcode_info", "id", QrcodeInfo.class);
		arp.addMapping("qrcode_wxacct", "id", QrcodeWxacct.class);
		arp.addMapping("trade_log", "id", TradeLog.class);

	}
}
