package org.seckill.exception;

/**
 * 秒杀业务相关异常
 * Created by Administrator on 2018/4/15 0015.
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
