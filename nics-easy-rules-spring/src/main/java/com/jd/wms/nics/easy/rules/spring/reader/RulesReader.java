/**
 * hzmmy.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.jd.wms.nics.easy.rules.spring.reader;

import com.jd.wms.nics.easy.rules.spring.definition.GroupsDefinition;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

/**
 * @author Y.Y.Zhao
 * @version $Id: RuleReader.java, v 0.1 11/26/2018 9:16 AM Y.Y.Zhao Exp $
 */
public class RulesReader {

    private Yaml yaml = new Yaml();

    public GroupsDefinition read(File file) throws FileNotFoundException
    {
        return this.read(new FileReader(file));
    }

    public GroupsDefinition read(Reader reader)
    {
        return this.yaml.loadAs(reader, GroupsDefinition.class);
    }

    public GroupsDefinition read(InputStream ins)
    {
        return this.yaml.loadAs(ins, GroupsDefinition.class);
    }
}
