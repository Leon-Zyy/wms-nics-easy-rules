/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.facts;

import com.jd.wms.nics.rules.api.Facts;

import static com.jd.wms.nics.easy.rules.spring.util.Constants.*;

/**
 * 高端Facts
 * @author Y.Y.Zhao
 * @version $Id: AdvFacts.java, v 0.1 11/24/2018 4:55 PM Y.Y.Zhao Exp $
 */
public class AdvFacts extends Facts{

    /**
     * 放置参数
     * @param param
     * @param <T>
     */
    public <T> void putParam(T param)
    {
        put(FACTS_PARAM_KEY, param);
    }

    /**
     * 放置结束
     * @param flag true: end  false: not to end
     * @param <T>
     */
    public <T> void putEnd(boolean flag)
    {
        put(FACTS_END_KEY, flag);
    }
}
