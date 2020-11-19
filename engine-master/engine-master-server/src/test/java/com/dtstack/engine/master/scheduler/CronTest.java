package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronHourParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleCronMinParser;
import com.dtstack.engine.master.scheduler.parser.ScheduleFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/11/14 2:09 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CronTest extends AbstractTest {

    private final DateTime dateTime;

    {
        dateTime = new DateTime();

    }

    @Test
    public void testCronMonth() throws Exception {
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getCronMonthTask();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleTaskShade.getScheduleConf());
        Assert.assertNotNull(scheduleCron);
        List<String> triggerTime = scheduleCron.getTriggerTime(dateTime.toString("yyyy-MM-dd"));
        Assert.assertNotNull(triggerTime);
    }

    @Test
    public void testCronWeek() throws Exception {
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getCronWeekTask();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleTaskShade.getScheduleConf());
        Assert.assertNotNull(scheduleCron);
        List<String> triggerTime = scheduleCron.getTriggerTime(dateTime.toString("yyyy-MM-dd"));
        Assert.assertNotNull(triggerTime);
    }

    @Test
    public void testCronDay() throws Exception {
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getCronDayTask();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleTaskShade.getScheduleConf());
        Assert.assertNotNull(scheduleCron);
        List<String> triggerTime = scheduleCron.getTriggerTime(dateTime.toString("yyyy-MM-dd"));
        Assert.assertNotNull(triggerTime);
    }

    @Test
    public void testCronHour() throws Exception {
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getCronWeekHour();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleTaskShade.getScheduleConf());
        Assert.assertNotNull(scheduleCron);
        List<String> triggerTime = scheduleCron.getTriggerTime(dateTime.toString("yyyy-MM-dd"));
        Assert.assertNotNull(triggerTime);
        ScheduleCronHourParser scheduleCronHourParser = (ScheduleCronHourParser) scheduleCron;
        Integer firstHour = scheduleCronHourParser.getFirstHour();
        Assert.assertNotNull(firstHour);
        Integer lastHour = scheduleCronHourParser.getLastHour();
        Assert.assertNotNull(lastHour);
    }

    @Test
    public void testCronMin() throws Exception {
        ScheduleTaskShade scheduleTaskShade = DataCollection.getData().getCronWeekMin();
        ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleTaskShade.getScheduleConf());
        Assert.assertNotNull(scheduleCron);
        List<String> triggerTime = scheduleCron.getTriggerTime(dateTime.toString("yyyy-MM-dd"));
        Assert.assertNotNull(triggerTime);

        ScheduleCronMinParser scheduleCronMinParser = (ScheduleCronMinParser) scheduleCron;
        scheduleCronMinParser.isDayFirstTrigger(10, 15);
        Integer lastTriggerMinutes = scheduleCronMinParser.getLastTriggerMinutes();
        Assert.assertNotNull(lastTriggerMinutes);
    }
}
