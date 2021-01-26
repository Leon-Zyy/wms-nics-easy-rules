/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.definition;

import lombok.Data;

import java.util.List;

/**
 * @author Y.Y.Zhao
 * @version $Id: GroupDefinition.java, v 0.1 11/24/2018 7:03 PM Y.Y.Zhao Exp $
 */
@Data
public class GroupDefinition {

    private String name;

    private List<RuleDefinition> rules;
}
