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

package com.dtstack.taiga.scheduler.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2021/4/15 10:45 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SaveTaskTaskVO {

    private String msg;

    /**
     * isSave true: 保存成功  false: 保存失败
     *
     */
    private Boolean isSave;

    public static SaveTaskTaskVO save() {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setSave(Boolean.TRUE);
        return saveTaskTaskVO;
    }

    public static SaveTaskTaskVO noSave(String msg) {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setSave(Boolean.FALSE);
        saveTaskTaskVO.setMsg(msg);
        return saveTaskTaskVO;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSave() {
        return isSave;
    }

    public void setSave(Boolean save) {
        isSave = save;
    }
}
