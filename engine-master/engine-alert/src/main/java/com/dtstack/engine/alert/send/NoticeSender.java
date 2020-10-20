package com.dtstack.engine.alert.send;


import com.dtstack.engine.alert.domian.Notice;
import com.dtstack.engine.alert.send.ding.NoticeDingSender;
import com.dtstack.engine.alert.send.mail.NoticeMailSender;
import com.dtstack.engine.alert.send.phone.TencentCloudPhoneSender;
import com.dtstack.engine.alert.send.sms.NoticePhoneMsgSender;
import com.dtstack.engine.alert.serivce.ISenderService;
import com.dtstack.engine.api.enums.SendStatus;
import com.dtstack.engine.api.enums.SenderType;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 2:37 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class NoticeSender implements Runnable, NoticeSenderApi, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(NoticeSender.class);
    private boolean runnable = true;
    private BlockingQueue<Notice> mailQueue;

    @Value("${notice.queue.size:1000}")
    private Integer queueSize;

    @Autowired
    private ISenderService recordService;

    @Autowired
    private NoticeMailSender noticeMailSender;

    @Autowired
    private NoticePhoneMsgSender noticePhoneMsgSender;

    @Autowired
    private NoticeDingSender noticeDingSender;

    @Autowired
    private TencentCloudPhoneSender tencentCloudPhoneSender;


    @PostConstruct
    public void init() {
        mailQueue = Queues.newLinkedBlockingQueue(queueSize);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("noticeExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(5 * 60);
        executor.initialize();
        executor.submit(this);
    }

    @Override
    public boolean sendNoticeNow(Notice notice) {
        boolean result = false;
        long id = recordService.addSenderRecord(notice.getContentId(), notice.getNotifyRecordId(), notice.getUserDTO().getUserId(),
                notice.getSenderType(), notice.getAppType(), notice.getProjectId(), notice.getTenantId());
        if (SenderType.MAIL.getType() == notice.getSenderType()) {
            result = noticeMailSender.send(notice);
        } else if (SenderType.SMS.getType() == notice.getSenderType()) {
            result = noticePhoneMsgSender.send(notice);
        } else if (SenderType.DINGDING.getType() == notice.getSenderType()) {
            result = noticeDingSender.send(notice);
        } else if (SenderType.PHONE.getType() == notice.getSenderType()) {
            result = tencentCloudPhoneSender.send(notice);
        }
        int status = result ? SendStatus.SENDSUCCESS.getStatus() : SendStatus.SENDFAILURE.getStatus();
        recordService.updateSenderRecord(id, status);
        return result;
    }

    @Override
    public void sendNoticeAsync(Notice notice) {
        mailQueue.offer(notice);
    }

    @Override
    public void run() {
        while (runnable) {
            try {
                Notice notice = mailQueue.poll(30, TimeUnit.SECONDS);
                if (notice != null) {
                    sendNoticeNow(notice);
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        this.runnable = false;
    }
}
