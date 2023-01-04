package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    private static Properties getConfig(String fileConf) {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(fileConf)) {
            config.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return config;
    }

    public static void main(String[] args) {
        try {
            Properties conf = getConfig("rabbit.properties");
            Connection connection = DriverManager.getConnection(conf.getProperty("url"), conf.getProperty("username"), conf.getProperty("password"));
            int interval = Integer.parseInt(conf.getProperty("rabbit.interval"));
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception ex) {
        throw new RuntimeException(ex);
        }
    }

        public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connect");
            try (Statement statement = connection.createStatement()) {
                String sql = String.format(
                        "insert into created_date(created_date) values('%s'::date);",
                        LocalDateTime.now()
                );
                statement.execute(sql);
        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}