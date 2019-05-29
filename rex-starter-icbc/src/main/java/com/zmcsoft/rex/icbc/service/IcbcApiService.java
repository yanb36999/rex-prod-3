package com.zmcsoft.rex.icbc.service;

import com.zmcsoft.rex.entity.PayDetail;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.service.PayDetailService;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.utils.time.DateFormatter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhouhao
 * @since 1.0
 */
@Slf4j(topic = "business.icbc")
@Service
public class IcbcApiService {
    @Value("${icbc.socket.host:192.168.56.97}")
    String socketIp = "95.2.2.43";

    @Value("${icbc.socket.port:10016}")
    int port = 10016;

    @Autowired
    private PayDetailService payDetailService;

    @Autowired
    private MessageSenders messageSenders;

    public String callIcbc(String input) {
        input = input + "\n";
        // 1、创建客户端Socket，指定服务器地址和端口
        log.info("向socket {}:{} 发送报文:\n{}", socketIp, port, input);
        try (Socket socket = new Socket()) {
            //设置超时时间
            socket.connect(new InetSocketAddress(socketIp, port), 45000);
            try (PrintWriter pw = new PrintWriter(socket.getOutputStream());//2.得到socket读写流
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"))) {
                //3.利用流按照一定的操作，对socket进行读写操作
                pw.write(new String(input.getBytes(), "ISO-8859-1"));
                pw.flush();
                //接收服务器的响应
                String result = null;
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    result = tmp;
                    log.info("通过socket {}:{} 获取报文成功:\n{}", socketIp, port, result);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("通过socket {}:{} 获取失败!", socketIp, port, e);
        }
        return "-1|发送报文失败";
    }

    private String getStartNumber(String str) {
        String number = "";
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9') {
                number += c;
            } else {
                break;
            }
        }
        return number;
    }

    public String enrich(String[] arr, BigDecimal amount) {
        //-------------------0--------1--------------2-----------3-4--------5-------------6-----7----8-----9---------10-------------11------12-------13-------14-15
        String[] template = "02|G510132100048232|510132100048232|1|A|510132196802014214|杨际祥|04402|02680|00020|2017-11-10 22:16:23|0.01|5101320006|5101320006|G|4|^^".split("[|]");

        String data = "";

        String code1 = getStartNumber(arr[8]);//.substring(0, 10);
        String code2 = getStartNumber(arr[9]);//.substring(0, 10);

        template[1] = "G" + arr[1];
        template[2] = arr[1];
        template[5] = arr[4];
        template[6] = arr[5];
        template[10] = DateFormatter.toString(new Date(), "yyyy-MM-dd HH:mm:ss");

        String money = new DecimalFormat("#0.00")
                .format(amount == null ? new BigDecimal(arr[6]).add(new BigDecimal(arr[7])) : amount);

        template[11] = money;//new BigDecimal(arr[6]).add(new BigDecimal(arr[7])).toString();
        template[12] = code1;
        template[13] = code2;

        data = String.join("|", template);
        return data;
    }

    public void sendBook(String bookDate) {
        List<PayDetail> payDetails = payDetailService.selectByBookDate(bookDate);
        log.info("{}需要生成对账文件的条数为{}", bookDate, payDetails.size());
        String fileName = bookDate + ".yd";

        try (OutputStream out = new FileOutputStream(new File("/data/rex-books/" + fileName))) {
            for (PayDetail payDetail : payDetails) {
                String old = callIcbc("01|" + payDetail.getPaySerialId() + "|^^");
                if (old == null) {
                    continue;
                }
                String[] arr = old.split("[|]");
                //必须是已缴款的才生成对账文件
                if (!"1".equals(arr[12])) {
                    continue;
                }
                String data = enrich(arr, payDetail.getAmount()) + "\n";
                log.info("对账[{}]数据生成成功:{}", fileName, data);
                //将data一行一行的写入文件中
                out.write(data.getBytes("GBK"));
            }
            out.flush();
            out.close();
            try (InputStream file = new FileInputStream("/data/rex-books/" + fileName)) {
                messageSenders.ftp()
                        .upload(fileName, file)
                        .send();
            }

        } catch (Exception e) {
            log.error("发送对账文件[{}]失败", bookDate, e);
        }
    }


    @Scheduled(cron = "0 0 6 * * ?")
    public void autoSendBook() {
        String date = new DateTime().plusDays(-1).toString("yyyy-MM-dd");
        sendBook(date);
    }
}
