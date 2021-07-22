const baseSystemParams = (
  <p>
    <span>${`{bdp.system.bizdate}`} --业务日期，格式：yyyyMMdd</span>
    <br />
    <span>${`{bdp.system.bizdate2}`} --业务日期，格式：yyyy-MM-dd</span>
    <br />
    <span>${`{bdp.system.cyctime}`} --计划时间，格式：yyyyMMddHHmmss</span>
    <br />
    <span>
      ${`{bdp.system.premonth}`} --上个月（以计划时间为基准），格式：yyyyMM
    </span>
    <br />
    <span>
      ${`{bdp.system.currmonth}`} --当前月（以计划时间为基准），格式：yyyyMM
    </span>
    <br />
    <span>
      ${`{bdp.system.runtime}`}{" "}
      --当前时间，即任务实际运行的时间，格式：yyyyMMddHHmmss
    </span>
  </p>
);

export const customSystemParams = (
  <div>
    <p>常用系统变量:</p>
    {baseSystemParams}
  </div>
);

export const customParams = (
  <div>
    <p>
      在代码中输入的格式为：${`{key1}`}，key1 为变量名，在当前面板中为 key1 赋值
    </p>
    <p>支持常量或变量赋值，常量直接输入字符串或数字</p>
    <p>变量有 $[yyyyMMdd] 和${`{yyyyMMdd}`}格式，二者时间基点不同</p>
    <p>
      $[]格式：变量基于 bdp.system.cyctime 取值，格式为：key1=$[yyyy]，其中的
      yyyy 是取 bdp.system.cyctime 的年的部分
    </p>
    <p>
      ${`{}`}格式：变量基于 bdp.system.bizdate 取值，格式为：key1=${`{yyyy}`}
      ，其中的 yyyy 是取 bdp.system.bizdate 的年的部分
    </p>
    <p>详细说明请参考:[TODO]</p>
  </div>
);
