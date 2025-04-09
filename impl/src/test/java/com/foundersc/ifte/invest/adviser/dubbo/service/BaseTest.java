package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import org.junit.Before;


/**
 * @author wangfuwei
 * @date 2022/10/18
 */
public class BaseTest {
    //61615497
    private static final String CLIENT_ID = "99700004";//注：资金账号和客户号可能不一样
    private static final String FUND_ACCOUNT = "99700004"; //资金账户
    private static final String INVESTOR_ACCOUNT = "99700004"; //投顾账户
//    private static final String CLIENT_ID = "61615497";//注：资金账号和客户号可能不一样
//    private static final String FUND_ACCOUNT = "61615497"; //资金账户
//    private static final String INVESTOR_ACCOUNT = "61615497"; //投顾账户
    private static final String CLIENT_PWD = "123123";
    private static final String OP_STATION = "MPN:15874167602;IP:192.168.0.100;MAC:020000000000@XF|VER:1.0.0|FROM:XF";

    protected SimpleAccount simpleAccount;
    protected static final String COMB_CODE = "zofzmby_a_1";

    @Before
    public void init() {
        SimpleAccount simpleAccount = new SimpleAccount(CLIENT_ID, CLIENT_PWD, OP_STATION);
        simpleAccount.setFundAccount(FUND_ACCOUNT);//资金账户
        simpleAccount.setInvestorAccount(INVESTOR_ACCOUNT);//投顾账号
        simpleAccount.setBranchNo(9999);
        simpleAccount.setMobile("13912345678");
        this.simpleAccount = simpleAccount;
    }
}
