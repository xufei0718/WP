package com.mybank.pc.qrcode.wxacct;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mybank.pc.kits.ZipKit;
import com.mybank.pc.qrcode.model.QrcodeInfo;
import com.mybank.pc.qrcode.model.QrcodeWxacct;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class QrcodeWxSrv {
    /**
     * 解压缩方法
     *
     * @param zipFileName 压缩文件名
     * @param dstPath     解压目标路径
     * @return
     */

    public boolean unzip(String zipFileName, String dstPath, QrcodeWxacct qw) {

        System.out.println("zip uncompressing...");
        ZipInputStream zipInputStream = null;

        try {
            zipInputStream = new ZipInputStream(new FileInputStream(zipFileName), Charset.forName("GBK"));
            ZipEntry zipEntry = null;
            byte[] buffer = new byte[ZipKit.BUFFER];//缓冲器
            int readLength = 0;//每次读出来的长度

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                System.out.println(zipEntry.getName());
                if("__MACOSX".equals(zipEntry.getName().split("/")[0])){
                    continue;//跳出
                }

                if (zipEntry.isDirectory()) {//若是zip条目目录，则需创建这个目录
                    File dir = new File(dstPath + "/" + zipEntry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                        System.out.println("mkdirs: " + dir.getCanonicalPath());
                        continue;//跳出
                    }
                }


                File file = ZipKit.createFile(dstPath, zipEntry.getName());//若是文件，则需创建该文件
                //System.out.println("file created: " + file.getCanonicalPath());
                OutputStream outputStream = new FileOutputStream(file);

                while ((readLength = zipInputStream.read(buffer, 0, ZipKit.BUFFER)) != -1) {
                    outputStream.write(buffer, 0, readLength);
                }
                outputStream.close();
                //写入数据库
                saveQrCode(file.getName(),qw);
                //System.out.println("file uncompressed: " + file.getCanonicalPath());
            }    // end while
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("unzip fail!");
            return false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("unzip fail!");
            return false;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("unzip success!");
        return true;
    }

    public boolean saveQrCode(String fileName,QrcodeWxacct qw){
        if (StringUtils.isEmpty(fileName)){
          return false;
        }
        String name =fileName.split("\\.")[0];


        BigDecimal realAmount =new BigDecimal(name).divide(new BigDecimal(100),2,BigDecimal.ROUND_DOWN);
        System.out.println(realAmount);
        //计算交易金额
        BigDecimal amount = realAmount.setScale( 0, BigDecimal.ROUND_UP ); // 向上取整

        QrcodeInfo qitemp = QrcodeInfo.dao.findFirst("select * from qrcode_info qi where qi.realAmount= "+realAmount+" and qi.wxAcct='"+qw.getWxAcct()+"' and qi.dat is null");

        if (ObjectUtil.isNull(qitemp)){
            QrcodeInfo qi = new QrcodeInfo();
            qi.setWxAcctID(qw.getId());
            qi.setAmount(amount);
            qi.setRealAmount(realAmount);
            qi.setIsLock("0");
            qi.setIsVail("0");
            qi.setImgName(fileName);
            qi.setWxAcct(qw.getWxAcct());
            qi.setCat(new Date());
            qi.save();
        }else{
            qitemp.setImgName(fileName);
            qitemp.setMat(new Date());
            qitemp.update();
        }

        return true;
    }

    public static void main(String[] args) {
        String dir = "/Users/xufei/Desktop/img/";
        Integer name = 900;
        String h = ".jpeg";
        File imgSrc = new File(dir + name+h);
        Integer imgName= name;
        for (int i=0; i<100;i++){
            imgName+=1;
            File img = new File(dir + imgName+h);
            FileUtil.copy(imgSrc, img, true);
        }

    }
}
