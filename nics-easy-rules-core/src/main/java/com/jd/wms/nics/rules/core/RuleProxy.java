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
package com.jd.wms.nics.rules.core;

import com.jd.wms.nics.rules.annotation.Fact;
import com.jd.wms.nics.rules.api.Prioritized;
import com.jd.wms.nics.rules.annotation.Action;
import com.jd.wms.nics.rules.annotation.Condition;
import com.jd.wms.nics.rules.annotation.Fact;
import com.jd.wms.nics.rules.annotation.Priority;
import com.jd.wms.nics.rules.api.Facts;
import com.jd.wms.nics.rules.api.Prioritized;
import com.jd.wms.nics.rules.api.Rule;
import com.jd.wms.nics.rules.annotation.Action;
import com.jd.wms.nics.rules.annotation.Condition;
import com.jd.wms.nics.rules.annotation.Fact;
import com.jd.wms.nics.rules.api.Facts;
import com.jd.wms.nics.rules.api.Prioritized;
import com.jd.wms.nics.rules.api.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.format;

/**
 * Main class to create rule proxies from annotated objects.
 *
 * @author Y.Y.Zhao
 */
public class RuleProxy implements InvocationHandler {

    private Object target;

    private static RuleDefinitionValidator ruleDefinitionValidator = new RuleDefinitionValidator();

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleProxy.class);

    private RuleProxy(final Object target) {
        this.target = target;
    }

    /**
     * Makes the rule object implement the {@link Rule} interface.
     *
     * @param rule the annotated rule object.
     * @return a proxy that implements the {@link Rule} interface.
     */
    public static Rule asRule(final Object rule) {
        Rule result;
        if (rule instanceof Rule) {
            result = (Rule) rule;
        } else {
            ruleDefinitionValidator.validateRuleDefinition(rule);
            result = (Rule) Proxy.newProxyInstance(
                    Rule.class.getClassLoader(),
                    new Class[]{Rule.class, Comparable.class},
                    new RuleProxy(rule));
        }
        return result;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        String methodName = method.getName();
        switch (methodName) {
            case "getName":
                return getRuleName();
            case "getDescription":
                return getRuleDescription();
            case "getPriority":
                return getRulePriority(proxy);
            case "compareTo":
                return compareToMethod(proxy, args);
            case "evaluate":
                return evaluateMethod(args);
            case "execute":
                return executeMethod(args);
            case "equals":
                return equalsMethod(proxy, args);
            case "hashCode":
                return hashCodeMethod(proxy);
            case "toString":
                return toStringMethod();
            default:
                return null;
        }
    }

    private Object evaluateMethod(final Object[] args) throws IllegalAccessException, InvocationTargetException {
        Facts facts = (Facts) args[0];
        Method conditionMethod = getConditionMethod();
        try {
            List<Object> actualParameters = getActualParameters(conditionMethod, facts);
            return conditionMethod.invoke(target, actualParameters.toArray()); // validated upfront
        } catch (NoSuchFactException e) {
            LOGGER.info("Rule '{}' has been evaluated to false due to a declared but missing fact '{}' in {}",
                    getTargetClass().getName(), e.getMissingFact(), facts);
            return false;
        } catch (IllegalArgumentException e) {
            String error = "Types of injected facts in method '%s' in rule '%s' do not match parameters types";
            throw new RuntimeException(format(error, conditionMethod.getName(), getTargetClass().getName()), e);
        }
    }

    private Object executeMethod(final Object[] args) throws IllegalAccessException, InvocationTargetException {
        Facts facts = (Facts) args[0];
        for (ActionMethodOrderBean actionMethodBean : getActionMethodBeans()) {
            Method actionMethod = actionMethodBean.getMethod();
            List<Object> actualParameters = getActualParameters(actionMethod, facts);
            actionMethod.invoke(target, actualParameters.toArray());
        }
        return null;
    }
    private Object compareToMethod(Object proxy, final Object[] args) throws Exception {
        Method compareToMethod = getCompareToMethod();
        if (compareToMethod != null) {
            return compareToMethod.invoke(target, args);
        } else {
            Rule otherRule = (Rule) args[0];
            return compareTo(proxy, otherRule);
        }
    }
    private List<Object> getActualParameters(Method method, Facts facts) {
        List<Object> actualParameters = new ArrayList<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotations : parameterAnnotations) {
            if (annotations.length == 1) {
                String factName = ((Fact) (annotations[0])).value(); //validated upfront.
                Object fact = facts.get(factName);
                if (fact == null && !facts.asMap().containsKey(factName)) {
                    throw new NoSuchFactException(format("No fact named '%s' found in known facts: \n%s", factName, facts), factName);
                }
                actualParameters.add(fact);
            } else {
                actualParameters.add(facts); //validated upfront, there may be only one parameter not annotated and which is of type Facts.class
            }
        }
        return actualParameters;
    }

    private boolean equalsMethod(Object proxy, final Object[] args) throws Exception {
        if (!(args[0] instanceof Rule)) {
            return false;
        }
        Rule otherRule = (Rule) args[0];
        int otherPriority = otherRule.getPriority();
        int priority = getRulePriority(proxy);
        if (priority != otherPriority) {
            return false;
        }
        String otherName = otherRule.getName();
        String name = getRuleName();
        if (!name.equals(otherName)) {
            return false;
        }
        String otherDescription = otherRule.getDescription();
        String description =  getRuleDescription();
        return !(description != null ? !description.equals(otherDescription) : otherDescription != null);
    }

    private int hashCodeMethod(Object proxy) throws Exception {
        int result   = getRuleName().hashCode();
        int priority = getRulePriority(proxy);
        String description = getRuleDescription();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + priority;
        return result;
    }

    private String toStringMethod() throws Exception {
        Method[] methods = getMethods();
        for (Method method : methods) {
            if ("toString".equals(method.getName())) {
                return (String) method.invoke(target);
            }
        }
       return getRuleName();
    }

    private int compareTo(Object proxy, final Rule otherRule) throws Exception {
        int otherPriority = otherRule.getPriority();
        int priority = getRulePriority(proxy);
        if (priority < otherPriority) {
            return -1;
        } else if (priority > otherPriority) {
            return 1;
        } else {
            String otherName = otherRule.getName();
            String name = getRuleName();
            return name.compareTo(otherName);
        }
    }

    /**
     * 从代理对象里获取
     * @param proxy
     * @return
     * @throws Exception
     */
    private Integer getRulePriorityFromProxy(Object proxy) throws Exception
    {
        Field hField = proxy.getClass().getSuperclass().getDeclaredField("h");
        hField.setAccessible(true);

        RuleProxy ruleProxy = (RuleProxy) hField.get(proxy);

        Field field = ruleProxy.getClass().getDeclaredField("target");
        field.setAccessible(true);
        Object target = field.get(ruleProxy);
        if (target != null) {
            return this.getRulePriority(target);
        }
        return null;
    }

    private int getRulePriority(Object proxy) throws Exception {
        int priority = Rule.DEFAULT_PRIORITY;

        if (proxy instanceof Proxy) {
            Integer prior = getRulePriorityFromProxy(proxy);
            if (prior != null) {
                return prior;
            }
        }

        if (proxy instanceof Prioritized) {
            Integer prior = ((Prioritized) proxy).getPriority();
            if (prior != null) {
                return prior;
            }
        }

        com.jd.wms.nics.rules.annotation.Rule rule = getRuleAnnotation();
        if (rule.priority() != Rule.DEFAULT_PRIORITY) {
            priority = rule.priority();
        }

        //效率太低，舍弃 by Y.Y.Zhao
        /*Method[] methods = getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Priority.class)) {
                priority = (int) method.invoke(target);
                break;
            }
        }*/
        return priority;
    }

    private Method getConditionMethod() {
        Method[] methods = getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Condition.class)) {
                return method;
            }
        }
        return null;
    }

    private Set<ActionMethodOrderBean> getActionMethodBeans() {
        Method[] methods = getMethods();
        Set<ActionMethodOrderBean> actionMethodBeans = new TreeSet<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Action.class)) {
                Action actionAnnotation = method.getAnnotation(Action.class);
                int order = actionAnnotation.order();
                actionMethodBeans.add(new ActionMethodOrderBean(method, order));
            }
        }
        return actionMethodBeans;
    }

    private Method getCompareToMethod() {
        Method[] methods = getMethods();
        for (Method method : methods) {
            if (method.getName().equals("compareTo")) {
                return method;
            }
        }
        return null;
    }

    private Method[] getMethods() {
        return getTargetClass().getMethods();
    }

    private com.jd.wms.nics.rules.annotation.Rule getRuleAnnotation() {
        return Utils.findAnnotation(com.jd.wms.nics.rules.annotation.Rule.class, getTargetClass());
    }

    private String getRuleName() {
        com.jd.wms.nics.rules.annotation.Rule rule = getRuleAnnotation();
        return rule.name().equals(Rule.DEFAULT_NAME) ? getTargetClass().getSimpleName() : rule.name();
    }

    private String getRuleDescription() {
        // Default description = "when " + conditionMethodName + " then " + comma separated actionMethodsNames
        StringBuilder description = new StringBuilder();
        appendConditionMethodName(description);
        appendActionMethodsNames(description);

        com.jd.wms.nics.rules.annotation.Rule rule = getRuleAnnotation();
        return rule.description().equals(Rule.DEFAULT_DESCRIPTION) ? description.toString() : rule.description();
    }

    private void appendConditionMethodName(StringBuilder description) {
        Method method = getConditionMethod();
        if (method != null) {
            description.append("when ");
            description.append(method.getName());
            description.append(" then ");
        }
    }

    private void appendActionMethodsNames(StringBuilder description) {
        Iterator<ActionMethodOrderBean> iterator = getActionMethodBeans().iterator();
        while (iterator.hasNext()) {
            description.append(iterator.next().getMethod().getName());
            if (iterator.hasNext()) {
                description.append(",");
            }
        }
    }

    private Class<?> getTargetClass() {
        return target.getClass();
    }

}
