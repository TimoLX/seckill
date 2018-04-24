package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2018/4/14 0014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        /**
         * 第一次 insertcount : 1
         * 第二次 insertcount : 0 在SuccessKilledDao.xml中用ignore来处理主键报错，并且创建sql时用的联合主键约束
         */
        int insertcount = successKilledDao.insertSuccessKilled(1005L,13679137100L);
        System.out.println("insertcount : "+insertcount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        /**
         * SuccessKilled{seckillId=1001,
         * userPhone=18591960021,
         * state=-1,
         * createTime=Sat Apr 14 19:09:38 CST 2018}
         Seckill{seckillId=1001,
         name='500元秒杀ipad2',
         number=200,
         startTime=Sun Nov 01 00:00:00 CST 2015,
         endTime=Mon Nov 02 00:00:00 CST 2015,
         createTime=Sat Apr 14 14:39:25 CST 2018}
         */
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1005L,13679137100L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}