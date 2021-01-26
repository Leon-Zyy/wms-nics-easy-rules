/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.group;

import com.jd.wms.nics.rules.api.AdvRules;
import lombok.Getter;
import com.jd.wms.nics.rules.api.Rules;
import org.springframework.beans.factory.InitializingBean;

/**
 * 抽象组规则
 * @author Y.Y.Zhao
 * @version $Id: AbstractGroupRules.java, v 0.1 11/24/2018 3:42 PM Y.Y.Zhao Exp $
 */
@Getter
public abstract class AbstractGroupRules implements InitializingBean{

    private AdvRules rules = new AdvRules();

    @Override
    public void afterPropertiesSet() {
        this.registerRules(this.rules);
    }

    /**
     * 注册rule
     * @param rules
     */
    public abstract void registerRules(Rules rules);
}
