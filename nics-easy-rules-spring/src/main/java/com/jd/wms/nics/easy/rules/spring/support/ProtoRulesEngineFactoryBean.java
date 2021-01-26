/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.support;

import com.jd.wms.nics.rules.api.RulesEngine;
import com.jd.wms.nics.rules.core.DefaultRulesEngine;
import com.jd.wms.nics.easy.rules.spring.listener.EndRuleListener;
import com.jd.wms.nics.easy.rules.spring.listener.SkipRuleListener;
import org.springframework.beans.factory.FactoryBean;

/**
 * 多例规则引擎bean工厂
 * @author Y.Y.Zhao
 * @version $Id: ProtoRulesEngineFactoryBean.java, v 0.1 1/28/2019 8:12 PM Y.Y.Zhao Exp $
 */
public class ProtoRulesEngineFactoryBean implements FactoryBean<RulesEngine> {

    /**
     * 默认两个listener，一个是中止所有，一个是跳过指定规则
     * @return
     */
    public RulesEngine init() {
        RulesEngine rulesEngine = new DefaultRulesEngine();
        EndRuleListener endRuleListener = new EndRuleListener();
        rulesEngine.getRuleListeners().clear();
        rulesEngine.getRuleListeners().add(endRuleListener);
        //新增规则跳过listener
        rulesEngine.getRuleListeners().add(new SkipRuleListener());
        return rulesEngine;
    }

    @Override
    public RulesEngine getObject() throws Exception {
        return this.init();
    }

    @Override
    public Class<?> getObjectType() {
        return RulesEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
