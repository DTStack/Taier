//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.dtstack.engine.common.util;

import dt.insight.plat.lang.exception.base.DtCenterDefException;
import dt.insight.plat.lang.exception.biz.BizException;
import dt.insight.plat.lang.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class APITemplate<T> {
    public static final long THRESHOLD = 10000L;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected APITemplate() {
    }

    protected void checkParams() throws IllegalArgumentException {
    }

    protected abstract T process() throws BizException;

    protected void afterProcess() {
    }

    protected void onSuccess() {
    }

    protected void onError(Throwable e) {
        if (this.log.isErrorEnabled()) {
            this.log.error("API Error while execute:{}", e);
        }

    }

    public R<T> execute() {
        try {
            this.checkParams();
        } catch (IllegalArgumentException var18) {
            if (this.log.isInfoEnabled()) {
                this.log.info("param check error:{}", var18);
            }

            throw var18;
        } catch (BizException var19) {
            this.onError(var19);
            return R.fail(var19.getCode(), var19.getMessage());
        } catch (DtCenterDefException var20) {
            this.onError(var20);
            return R.fail(var20.getCode(), var20.getMessage());
        }

        long sTime = System.currentTimeMillis();
        boolean var17 = false;

        R var4;
        long costTime;
        label146: {
            label147: {
                try {
                    var17 = true;
                    T r = this.process();
                    this.onSuccess();
                    var4 = R.ok(r);
                    var17 = false;
                    break label146;
                } catch (BizException var21) {
                    this.onError(var21);
                    var4 = R.fail(var21.getCode(), var21.getMessage());
                    var17 = false;
                } catch (DtCenterDefException var22) {
                    this.onError(var22);
                    var4 = R.fail(var22.getCode(), var22.getMessage());
                    var17 = false;
                    break label147;
                } catch (Throwable var23) {
                    this.onError(var23);
                    if (var23 instanceof RuntimeException) {
                        throw (RuntimeException)var23;
                    }

                    throw new RuntimeException(var23);
                } finally {
                    if (var17) {
                        this.afterProcess();
                        long costTime1 = System.currentTimeMillis() - sTime;
                        if (costTime1 > 10000L && this.log.isWarnEnabled()) {
                            this.log.warn("this api method execute cost too long time, please optimize it:{} ms", costTime1);
                        }

                    }
                }

                this.afterProcess();
                costTime = System.currentTimeMillis() - sTime;
                if (costTime > 10000L && this.log.isWarnEnabled()) {
                    this.log.warn("this api method execute cost too long time, please optimize it:{} ms", costTime);
                }

                return var4;
            }

            this.afterProcess();
            costTime = System.currentTimeMillis() - sTime;
            if (costTime > 10000L && this.log.isWarnEnabled()) {
                this.log.warn("this api method execute cost too long time, please optimize it:{} ms", costTime);
            }

            return var4;
        }

        this.afterProcess();
        costTime = System.currentTimeMillis() - sTime;
        if (costTime > 10000L && this.log.isWarnEnabled()) {
            this.log.warn("this api method execute cost too long time, please optimize it:{} ms", costTime);
        }

        return var4;
    }
}
