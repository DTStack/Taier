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

//package com.dtstack.engine.flink;
//
//import com.dtstack.engine.base.util.KerberosUtils;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.security.UserGroupInformation;
//import org.apache.kerby.kerberos.kerb.keytab.Keytab;
//import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PowerMockIgnore;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
///**
// * Date: 2020/7/8
// * Company: www.dtstack.com
// * @author xiuzhu
// */
//
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SFTPHandler.class, Keytab.class, KerberosUtils.class, UserGroupInformation.class})
//@PowerMockIgnore("javax.net.ssl.*")
//public class KerberosUtilsTest {
//
//	@Test
//	public void testLogin() throws Exception {
//		File file = PowerMockito.mock(File.class);
//		PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
//		when(file.exists()).thenReturn(true);
//
//		PowerMockito.mockStatic(SFTPHandler.class);
//		SFTPHandler sftpHandler = PowerMockito.mock(SFTPHandler.class);
//		when(SFTPHandler.getInstance(any())).thenReturn(sftpHandler);
//		when(sftpHandler.loadFromSftp(any(String.class), any(String.class), any(String.class), any(boolean.class)))
//				.thenReturn("test/keytab/user.keytab");
//
//		Keytab keytab = PowerMockito.mock(Keytab.class);
//		List<PrincipalName> principals = new ArrayList<>();
//		PrincipalName principalName = new PrincipalName();
//		List<String> nameStrings = new ArrayList<>();
//		nameStrings.add("test");
//		principalName.setNameStrings(nameStrings);
//		principals.add(principalName);
//		when(keytab.getPrincipals()).thenReturn(principals);
//		PowerMockito.mockStatic(Keytab.class);
//		when(Keytab.loadKeytab(any(File.class))).thenReturn(keytab);
//
//		FlinkConfig flinkConfig = new FlinkConfig();
//		flinkConfig.setPrincipalFile("user");
//		flinkConfig.setRemoteDir("remoteDir");
//		flinkConfig.setOpenKerberos(true);
//
//		PowerMockito.mockStatic(UserGroupInformation.class);
//		when(UserGroupInformation.loginUserFromKeytabAndReturnUGI(any(), any()))
//				.thenReturn(PowerMockito.mock(UserGroupInformation.class));
//
//		KerberosUtils.login(flinkConfig, () -> {
//			return null;
//		}, new Configuration());
//	}
//
//}
