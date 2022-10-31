/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.dtstack.taier.develop.vo.develop.result.job;

import java.util.List;

/**
 * @author vainhope
 */
public class TaskProperties {

    private RenderCondition renderCondition;
    private String renderKind;
    private List<String> formField;
    private ActionsCondition actionsCondition;
    private List<String> actions;
    private List<Integer> dataTypeCodes;
    private List<String> barItem;
    private BarItemCondition barItemCondition;

    public List<Integer> getDataTypeCodes() {
        return dataTypeCodes;
    }

    public void setDataTypeCodes(List<Integer> dataTypeCodes) {
        this.dataTypeCodes = dataTypeCodes;
    }

    public BarItemCondition getBarItemCondition() {
        return barItemCondition;
    }

    public void setBarItemCondition(BarItemCondition barItemCondition) {
        this.barItemCondition = barItemCondition;
    }

    public void setRenderCondition(RenderCondition renderCondition) {
        this.renderCondition = renderCondition;
    }

    public RenderCondition getRenderCondition() {
        return renderCondition;
    }

    public void setRenderKind(String renderKind) {
        this.renderKind = renderKind;
    }

    public String getRenderKind() {
        return renderKind;
    }

    public void setFormField(List<String> formField) {
        this.formField = formField;
    }

    public List<String> getFormField() {
        return formField;
    }

    public void setActionsCondition(ActionsCondition actionsCondition) {
        this.actionsCondition = actionsCondition;
    }

    public ActionsCondition getActionsCondition() {
        return actionsCondition;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setBarItem(List<String> barItem) {
        this.barItem = barItem;
    }

    public List<String> getBarItem() {
        return barItem;
    }

}