package com.walter.orchestration.test.activity;

import com.walter.orchestration.base.*;
import com.walter.orchestration.test.activity.bo.DemoActivityChainReq;
import com.walter.orchestration.test.activity.bo.DemoActivityChainRes;
import com.walter.orchestration.test.common.DemoChainContextKey;
import com.walter.orchestration.test.gift.DemoVipGiftNode;
import com.walter.orchestration.test.gift.bo.DemoVipGiftReq;
import com.walter.orchestration.test.gift.bo.DemoVipGiftRes;
import com.walter.orchestration.util.CastUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 前10位参与某活动的用户，获得vip资格，且vip可获得小礼品
 * @author tyx
 * @date 2021/6/29
 *
 */
@Slf4j
@Component
public class DemoActivityChain extends AbstractChain {
    @Autowired
    @ChainOrder(1)
    private DemoVipGiftNode demoVipGiftNode;

    @Override
    protected void registChainNodeParamResolver(Map<Class<? extends AbstractChainNode>, NodeParamResolver> chainNodeParamResolverMap) {
        // 对每个链节点的入参添加NodeParamResolver，用于给各自的入参进行初始化

        chainNodeParamResolverMap.put(DemoVipGiftNode.class, context -> {
            DemoActivityChainReq demoActivityChainReq = CastUtil.cast(context.get(DemoChainContextKey.DemoActivityChain.REQ));
            DemoActivityTopEnterNodeRes demoChainVipGiftRes = CastUtil.cast(context.get(DemoChainContextKey.DemoActivityChain.PRE_PROCESS_CHAIN_RES));

            DemoVipGiftReq req = new DemoVipGiftReq();
            req.setUid(demoActivityChainReq.getUid()).setVip(demoChainVipGiftRes.isVip());
            context.put(demoVipGiftNode.getRequestContextKey(), req);
        });
    }

    /**
     * 前10位参与某活动的用户，获得vip资格
     * @param context
     */
    @Override
    protected void preProcessChain(final ChainContext context) {
        final int ID_THRESHOLD = 10;
        DemoActivityChainReq demoActivityChainReq = CastUtil.cast(context.get(DemoChainContextKey.DemoActivityChain.REQ));
        DemoActivityTopEnterNodeRes res = new DemoActivityTopEnterNodeRes();

        if(demoActivityChainReq.getId() <= ID_THRESHOLD){
            res.setVip(true);
        }else{
            res.setVip(false);
        }

        log.info(">>>>>> DemoActivityChain.preProcessChain(), req={}, res:{}", demoActivityChainReq, res);
        context.put(DemoChainContextKey.DemoActivityChain.PRE_PROCESS_CHAIN_RES, res);
    }

    @Override
    public void postProcessChain(ChainContext context) {
        DemoActivityTopEnterNodeRes demoActivityChainTopEnterNodeRes = CastUtil.cast(context.get(DemoChainContextKey.DemoActivityChain.PRE_PROCESS_CHAIN_RES));
        DemoVipGiftRes demoChainVipGiftRes = CastUtil.cast(context.get(demoVipGiftNode.getResponseContextKey()));

        DemoActivityChainRes res = new DemoActivityChainRes()
                .setVip(demoActivityChainTopEnterNodeRes.isVip())
                .setHasGift(demoChainVipGiftRes.isHasGift());
        context.put(DemoChainContextKey.DemoActivityChain.RES, res);
    }

    @Data
    @Accessors(chain = true)
    public class DemoActivityTopEnterNodeRes {
        private boolean isVip;
    }
}
