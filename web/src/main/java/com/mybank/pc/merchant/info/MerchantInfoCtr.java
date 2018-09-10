package com.mybank.pc.merchant.info;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.mybank.pc.admin.model.Role;
import com.mybank.pc.admin.model.Taxonomy;
import com.mybank.pc.admin.model.User;
import com.mybank.pc.admin.model.UserRole;
import com.mybank.pc.core.CoreController;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ext.BCrypt;
import com.mybank.pc.merchant.model.*;
import com.mybank.pc.trade.log.TradeLogSrv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户信息管理
 */

public class MerchantInfoCtr extends CoreController {
    private MerchantInfoSrv merchantInfoSrv =enhance(MerchantInfoSrv.class);
    private TradeLogSrv tradeLogSrv =enhance(TradeLogSrv.class);

    public void list() {
        Page<MerchantInfo> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from merchant_info mi  where 1=1 and mi.dat is null  ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(mi.merchantNo,?)>0 or instr(mi.merchantName,?)>0 or instr(mi.perName,?)>0) ");
            where.append(" ORDER BY mi.cat desc");
            page = MerchantInfo.dao.paginate(getPN(), getPS(), "select * ", where.toString(), serach, serach, serach);
        } else {
            where.append(" ORDER BY mi.cat desc");
            page = MerchantInfo.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        List<Taxonomy> tlist =CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"merTypeList");
        List<Taxonomy> fList =CacheKit.get(Consts.CACHE_NAMES.taxonomy.name(),"feeCollectTypeList");
        Map map = new HashMap();
        map.put("tList" ,tlist);
        map.put("fList" ,fList);
        map.put("page",page);
        renderJson(map);

    }

    @Before({MerchantInfoValidator.class, Tx.class})
    public void save() {

        MerchantInfo merInfo = getModel(MerchantInfo.class,"",true);
        merInfo.setMerchantType("1");
        String merNo = merchantInfoSrv.getMerchantNo(merInfo.getMerchantType());
        merInfo.setMerchantNo(merNo);
        merInfo.setCat(new Date());
        merInfo.setMobile1(RandomUtil.randomString(8));
        merInfo.setMaxTradeAmount(new BigDecimal(0));
        merInfo.setMat(new Date());
        merInfo.setStatus(Consts.STATUS.enable.getVal());
        merInfo.setOperID(String.valueOf(currUser().getId()));
        merInfo.save();

        //增加用户信息
        User user = new User();
        user.setLoginname("oper@"+merInfo.getMerchantNo());
        user.setIdcard(merInfo.getCardID());
        user.setNickname("商户"+merInfo.getMerchantNo());
        user.setPhone(merInfo.getMobile());
        user.setCAt(new Date());
        user.setEmailStatus(Consts.YORN.no.isVal());
        user.setPhoneStatus(Consts.YORN.no.isVal());
        user.setMAt(new Date());
        user.setStatus(Consts.STATUS.enable.getVal());
        user.setSignature(merInfo.getMerchantName());
        user.setSalt(BCrypt.gensalt());
        user.setPassword(BCrypt.hashpw(merInfo.getMobile(), user.getSalt()));
        String default_avart = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "image/avatar/dft-avatar.jpg";
        user.setAvatar(default_avart);
        user.save();

        //建立商户和操作员关系
        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setMerchantID(merInfo.getId());
        merchantUser.setUserID(user.getId().intValue());
        merchantUser.save();

        //增加默认角色，5为商户操作员
        List <Role> list = Role.dao.findByPropEQ("name","user");
        if(list !=null){
            UserRole ur = new UserRole();
            ur.setRId(list.get(0).getId().intValue());
            ur.setUId(user.getLong("id"));
            ur.save();
        }



        renderSuccessJSON("新增商户信息成功。", "");
    }

    @Before({MerchantInfoValidator.class, Tx.class})
    public void update() {
        MerchantInfo merInfo = getModel(MerchantInfo.class,"",true);

        merInfo.setMat(new Date());
        merInfo.setOperID(String.valueOf(currUser().getId()));
        merInfo.update();
        merchantInfoSrv.removeCacheMerchantInfo(merInfo.getId());
        renderSuccessJSON("更新商户信息成功。", "");
    }

    @Before({MerchantInfoValidator.class, Tx.class})
    public void updateAmount(){
        try{
            String merID = getPara("merID");
            MerchantInfo merInfo =MerchantInfo.dao.findById(new Integer(merID));
            String amount = getPara("amount");
            merInfo = tradeLogSrv.updateMerAmount(merInfo.getId(),new BigDecimal(amount),false);
            merInfo.setMat(new Date());
            merInfo.setOperID(String.valueOf(currUser().getId()));
            merInfo.update();

            //记录商户提现日志
            MerchantAmountLog mal = new MerchantAmountLog();
            mal.setMerID(merInfo.getId());
            mal.setMerNo(merInfo.getMerchantNo());
            mal.setMerName(merInfo.getMerchantName());
            mal.setAmount(new BigDecimal(amount));
            mal.setTAmount(merInfo.getMaxTradeAmount());
            mal.setAmountType("1");//1：提现
            mal.setCat(new Date());
            mal.setOperID(String.valueOf(currUser().getId()));
            mal.save();
            renderSuccessJSON("商户提现成功。", "");
        }catch (Exception e){
            e.printStackTrace();
            renderFailJSON("商户提现失败。", "");
        }


    }

    @Before({Tx.class})
    public void del(){
        int id=getParaToInt("id");
        MerchantInfo merInfo=MerchantInfo.dao.findById(BigInteger.valueOf(id));
        merInfo.setDat(new Date());
        merInfo.update();
        merchantInfoSrv.removeCacheMerchantInfo(merInfo.getId());
        //删除关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setDAt(new Date());
            user.update();
            CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),user.getId());
            CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),user.getId());
        }

        renderSuccessJSON("删除商户信息成功。");

    }


    /**
     * 用户禁用操作处理
     **/
    @Before(Tx.class)
    public void forbidden() {
        String merInfoId = getPara("id");
        int id = Integer.parseInt(merInfoId);
        MerchantInfo merInfo = new MerchantInfo();
        merInfo.setId(id);
        merInfo.setStatus(Consts.STATUS.forbidden.getVal());
        merInfo.update();
        //禁用关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setStatus(Consts.STATUS.forbidden.getVal());
            user.update();
            }

        renderSuccessJSON("禁用操作执行成功。", "");
    }

    /**
     * 恢复操作处理
     **/
    @Before(Tx.class)
    public void enable() {
        String merInfoId = getPara("id");
        int id = Integer.parseInt(merInfoId);
        MerchantInfo merInfo = new MerchantInfo();
        merInfo.setId(id);
        merInfo.setStatus(Consts.STATUS.enable.getVal());
        merInfo.update();
        //激活关联操作员用户
        List<MerchantUser> list = MerchantUser.dao.find("select * from merchant_user mu where mu.merchantID=? ",merInfo.getId());
        if(list !=null && list.size()>0){
            Integer userID = list.get(0).getUserID();
            User user =new User();
            user.setId(BigInteger.valueOf(userID));
            user.setStatus(Consts.STATUS.enable.getVal());
            user.update();
        }
        renderSuccessJSON("恢复操作执行成功。", "");
    }
    public void loginMerInfo(){
        //获取当前登录用户信息
        MerchantInfo merInfo = getAttr(Consts.CURR_USER_MER);
        renderJson(merInfo);
    }






}
