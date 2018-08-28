package com.mybank.pc.collection.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.collection.model.base.BaseUnionpayBatchCollectionQuery;
import com.mybank.pc.collection.model.sender.BatchQueryRequestBuilder;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.BatchCollectionResponse;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class UnionpayBatchCollectionQuery extends BaseUnionpayBatchCollectionQuery<UnionpayBatchCollectionQuery> {
	public static final UnionpayBatchCollectionQuery dao = new UnionpayBatchCollectionQuery().dao();

	private SendProxy sendProxy;

	public UnionpayBatchCollectionQuery assemblyBatchQueryRequest() {
		this.sendProxy = new BatchQueryRequestBuilder(this).build();
		setReq(JsonKit.toJson(sendProxy.getReqData()));
		return this;
	}

	/**
	 * 对请求参数进行签名并发送http post请求，接收同步应答报文
	 * 
	 * @return
	 * @throws Exception
	 */
	public SendProxy queryResult() throws Exception {
		if (sendProxy == null) {
			assemblyBatchQueryRequest();
		}
		sendProxy.send();
		this.setResp(JsonKit.toJson(sendProxy.getRspData()));
		return sendProxy;
	}

	public boolean validateBatchQueryResp() {
		return sendProxy.validateResp();
	}

	public boolean isTimeout() {
		return isTimeout(getTxnTime(), getMat(), getRespCode());
	}

	public boolean isTimeout(Date queryDate) {
		return isTimeout(getTxnTime(), queryDate, getRespCode());
	}

	public static boolean isTimeout(String txnTime, Date queryTime, String respCode) {
		try {
			Date txnTimeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(txnTime);
			return "34".equals(respCode) && DateUtil.between(txnTimeDate, queryTime, DateUnit.MINUTE,
					false) > UnionpayBatchCollection.TIMEOUT_MINUTE;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public BatchCollectionResponse toBatchCollectionResponse() {
		try {
			return new BatchCollectionResponse(getRespFileContent());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<UnionpayBatchCollectionQuery> find(String txnTime, String batchNo, String merId) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.findUnionpayBatchCollectionQuery",
				Kv.create().set("txnTime", txnTime).set("batchNo", batchNo).set("merId", merId));
		return UnionpayBatchCollectionQuery.dao.find(sqlPara);
	}

	@JSONField(serialize = false)
	public SendProxy getSendProxy() {
		return sendProxy;
	}

}
