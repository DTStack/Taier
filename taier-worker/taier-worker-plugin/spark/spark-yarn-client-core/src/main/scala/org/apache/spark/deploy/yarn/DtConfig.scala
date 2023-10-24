package org.apache.spark.deploy.yarn

import org.apache.spark.internal.config.ConfigBuilder

object DtConfig {
  val SPARK_UDFS_TO_DISTRIBUTE = ConfigBuilder("spark.udfs.jars")
    .stringConf
    .createOptional
  private[spark] val KRB5_CONF = ConfigBuilder("spark.kerberos.remotekrb5")
    .doc("Location of user's krb5.conf.")
    .stringConf.createOptional
  private[spark] var KRB5FILENAME: String = "krb5.conf"

}
