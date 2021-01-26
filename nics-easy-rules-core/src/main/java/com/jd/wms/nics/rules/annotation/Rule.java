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
package com.jd.wms.nics.rules.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark a class as a rule.
 *
 * @author Y.Y.Zhao
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Rule {

    /**
     * The rule name which must be unique within an rules registry.
     * @return The rule name
     */
    String name() default com.jd.wms.nics.rules.api.Rule.DEFAULT_NAME;

    /**
     * The rule description.
     * @return The rule description
     */
    String description() default  com.jd.wms.nics.rules.api.Rule.DEFAULT_DESCRIPTION;

    /**
     * The rule priority.
     * @return The rule priority
     */
    int priority() default com.jd.wms.nics.rules.api.Rule.DEFAULT_PRIORITY;

}
