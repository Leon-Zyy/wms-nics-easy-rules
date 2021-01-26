/**
 * The MIT License
 *
 *  Copyright (c) 2018, Y.Y.Zhao
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.jd.wms.nics.rules.mvel;

import com.jd.wms.nics.rules.api.Action;
import com.jd.wms.nics.rules.api.Facts;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * This class is an implementation of {@link Action} that uses <a href="https://github.com/mvel/mvel">MVEL</a> to execute the action.
 *
 * @author Y.Y.Zhao
 */
public class MVELAction implements Action {

    private static Logger LOGGER = LoggerFactory.getLogger(MVELAction.class);

    private String expression;
    private Serializable compiledExpression;

    /**
     * Create a new {@link MVELAction}.
     *
     * @param expression the action written in expression language
     */
    public MVELAction(String expression) {
        this.expression = expression;
        compiledExpression = MVEL.compileExpression(expression);
    }

    @Override
    public void execute(Facts facts) {
        try {
            MVEL.executeExpression(compiledExpression, facts.asMap());
        } catch (Exception e) {
            LOGGER.debug("Unable to evaluate expression: '" + expression + "' on facts: " + facts, e);
        }
    }
}
