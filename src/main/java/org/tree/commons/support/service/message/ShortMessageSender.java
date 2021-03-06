package org.tree.commons.support.service.message;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2018/12/17（copy from ali's demo）
 */
@Lazy
@Component
public class ShortMessageSender {

    @Value("${ali.sms.accessKeyId:}")
    private String aliSMSAccessKeyId;
    @Value("${ali.sms.accessKeySecret:}")
    private String aliSMSAccessKeySecret;
    @Value("${ali.sms.signName:}")
    private String aliSMSSignName;
    @Value("${ali.sms.templateCode:}")
    private String aliSMSATemplateCode;

    public ShortMessageSender() {
    }

    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    public SendSmsResponse send(String phone, Map param) {
        return send(phone, JSON.toJSONString(param));
    }

    public SendSmsResponse send(String phone, String param) {
        SendSmsResponse sendSmsResponse = null;
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliSMSAccessKeyId, aliSMSAccessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(aliSMSSignName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(aliSMSATemplateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"%s\"}");
            request.setTemplateParam(param);

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.setOutId("yourOutId");

            //hint 此处可能会抛出异常，注意catch
            sendSmsResponse = acsClient.getAcsResponse(request);
        } finally {
            return sendSmsResponse;
        }
    }

    public QuerySendDetailsResponse querySendDetails(String bizId) {
        QuerySendDetailsResponse querySendDetailsResponse = null;
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliSMSAccessKeyId, aliSMSAccessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            //必填-号码
            request.setPhoneNumber("15000000000");
            //可选-流水号
            request.setBizId(bizId);
            //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            request.setSendDate(ft.format(new Date()));
            //必填-页大小
            request.setPageSize(10L);
            //必填-当前页码从1开始计数
            request.setCurrentPage(1L);

            //hint 此处可能会抛出异常，注意catch
            querySendDetailsResponse = acsClient.getAcsResponse(request);
        } finally {
            return querySendDetailsResponse;
        }
    }
}
