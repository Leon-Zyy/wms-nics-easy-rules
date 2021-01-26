/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.listener;

import com.jd.wms.nics.rules.api.Facts;
import com.jd.wms.nics.rules.api.Rule;
import com.jd.wms.nics.rules.core.DefaultRuleListener;

import static com.jd.wms.nics.easy.rules.spring.util.Constants.FACTS_END_KEY;

/**
 * 结束规则listener，往Facts里面塞FACTS_END_KEY为true参数，则该组规则接下来就会中断检验
 * @author Y.Y.Zhao
 * @version $Id: EndRuleListener.java, v 0.1 11/23/2018 11:41 AM Y.Y.Zhao Exp $
 */
public class EndRuleListener extends DefaultRuleListener {
    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        Object obj = facts.get(FACTS_END_KEY);
        if (obj != null && obj instanceof Boolean) {
            return !(Boolean)obj;
        }
        return true;
    }
}
