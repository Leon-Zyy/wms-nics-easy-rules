/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.rule;

import com.jd.wms.nics.rules.api.Prioritized;

/**
 * @author Y.Y.Zhao
 * @version $Id: AbstractRule.java, v 0.1 11/24/2018 4:30 PM Y.Y.Zhao Exp $
 */
public abstract class AbstractRule implements Prioritized{

    private Integer priority;

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
