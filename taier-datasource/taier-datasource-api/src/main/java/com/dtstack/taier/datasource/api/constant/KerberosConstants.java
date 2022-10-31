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

package com.dtstack.taier.datasource.api.constant;

/**
 * kerberos constants
 *
 * @author ：wangchuan
 * date：Created in 21:27 2022/9/28
 * company: www.dtstack.com
 */
public class KerberosConstants {

    /**
     * principal
     */
    public static final String PRINCIPAL = "principal";

    /**
     * hbase master kerberos principal
     */
    public static final String HBASE_MASTER_PRINCIPAL = "hbase.master.kerberos.principal";

    /**
     * hbase region server kerberos principal
     */
    public static final String HBASE_REGION_PRINCIPAL = "hbase.master.kerberos.principal";

    /**
     * local kerberos dir
     */
    public static final String LOCAL_KERBEROS_DIR = "localKerberosDir";
}
