package com.mybank.pc.trade.log;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *  交易日志管理
 */

public class TradeLogCtr extends CoreController {


    /**
     * 显示交易日志列表
     */
    public void list() {


    }

    /**
     * 执行交易获取交易二维码
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void exeTradeQrcode() {

        String tradeAmount = getPara("tradeAmount");
        LogKit.info(tradeAmount);
        getResponse().addHeader("Access-Control-Allow-Origin", "*");   //用于ajax post跨域（*，最好指定确定的http等协议+ip+端口号）
        getResponse().setCharacterEncoding("utf-8");
        renderJson(tradeAmount);

    }

    /**
     * 交易结果查询
     */
    @com.jfinal.aop.Clear(AdminIAuthInterceptor.class)
    public void tradeResult(){

    }








}
