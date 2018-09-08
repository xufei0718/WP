#sql("queryTradeLog")
select * from trade_log tt where 1=1 and tt.dat is null
#if(searchMerNo)
and tt.tradeMerNo=#para(searchMerNo)
#end
#if(search)
and (instr(tt.tradeNo,#para(search))>0 or instr(tt.tradeMerNo,#para(search))>0)
#end
 #if(searchWxAcct)
 and instr(tt.tradeWxAcct,#para(searchWxAcct))>0
 #end
  #if(searchAmount)
 and tt.tradeRealAmount=#para(searchAmount)
 #end
  #if(searchStartDate)
 and tt.tradeTime>=#para(searchStartDate)
 #end
   #if(searchEndDate)
 and tt.tradeTime<=#para(searchEndDate)
 #end
 ORDER BY tt.cat desc
#end





