package com.dtstack.taiga.common.util;

import com.dtstack.taiga.common.client.ClientOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuebai
 * @date 2021-09-08
 */
public class DtJobIdWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOperator.class);

    private static volatile DtJobIdWorker singleton;

    //the last one net work
    private Integer network;

    //auto increment sequence
    private long sequence;

    //the last build jobId timeStamp
    private long lastTimestamp = 0L;

    private DtJobIdWorker(Integer network, long sequence) {
        this.network = network;
        this.sequence = sequence;
    }

    public static DtJobIdWorker getInstance(Integer network, long sequence) {
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence can not less than 0");
        }
        if (null == network) {
            throw new IllegalArgumentException("net work can not null");
        }
        if (singleton == null) {
            synchronized (ClientOperator.class) {
                if (singleton == null) {
                    singleton = new DtJobIdWorker(network, sequence);
                    LOGGER.info("init DtJobIdUtils DtJobIdUtils{}", network);
                }
            }
        }
        return singleton;
    }

    final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v'
    };

    /**
     * change long to 32 digits
     *
     * @param val
     * @return
     */
    static String digits32(long val) {
        int shift = 5;
        int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
        int len = Math.max(((mag + (shift - 1)) / shift), 1);
        char[] buf = new char[len];
        do {
            buf[--len] = digits[((int) val) & 31];
            val >>>= shift;
        } while (val != 0 && len > 0);
        return new String(buf);
    }


    //next JobId
    public synchronized String nextJobId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            LOGGER.error("Clock moved backwards.  lastTimeStamp {} timeStamp {}", lastTimestamp, timestamp);
            timestamp = lastTimestamp;
        }

        //increment sequence
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1;
            if (sequence > 99999) {
                //out of sequence
                timestamp = tilNextMillis(lastTimestamp);
                sequence  = 0;
            }
        } else {
            sequence = 0;
        }
        //refresh
        lastTimestamp = timestamp;
        String jobId = String.valueOf(lastTimestamp).concat(String.format("%03d", network)).concat(String.format("%05d", sequence));
        return digits32(Long.parseLong(jobId));
    }

    //get seconds
    private long timeGen() {
        return System.currentTimeMillis() / 1000;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}
