package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.User;
import com.life.progress.entity.WorkDayRecord;
import com.life.progress.entity.WorkSetting;
import com.life.progress.service.UserService;
import com.life.progress.service.WorkDayRecordService;
import com.life.progress.service.WorkSettingService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/work")
public class WorkProgressController {

    @Autowired
    private WorkSettingService workSettingService;

    @Autowired
    private WorkDayRecordService workDayRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final int DEFAULT_RETIREMENT_AGE = 65;
    private static final int DEFAULT_WORK_DAYS_PER_WEEK = 5;

    @GetMapping("/setting")
    public Result<WorkSetting> getSetting(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        WorkSetting setting = workSettingService.findByUserId(userId);
        return Result.success(setting);
    }

    @PostMapping("/setting")
    public Result<WorkSetting> saveSetting(
            @RequestBody WorkSetting workSetting,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        workSetting.setUserId(userId);
        boolean success = workSettingService.saveOrUpdate(workSetting);

        if (success) {
            return Result.success("保存成功", workSetting);
        } else {
            return Result.error("保存失败");
        }
    }

    @GetMapping("/progress")
    public Result<Map<String, Object>> getWorkProgress(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        WorkSetting setting = workSettingService.findByUserId(userId);
        if (setting == null || setting.getJoinDate() == null) {
            return Result.success(new HashMap<>());
        }

        LocalDate now = LocalDate.now();
        LocalDate joinDate = setting.getJoinDate();
        int retirementAge = setting.getRetirementAge() != null ? setting.getRetirementAge() : DEFAULT_RETIREMENT_AGE;

        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        User user = userService.findById(userId);
        if (user != null && user.getBirthDate() != null) {
            birthDate = user.getBirthDate();
        }

        LocalDate retirementDate = birthDate.plusYears(retirementAge);

        long totalWorkDays = ChronoUnit.DAYS.between(joinDate, retirementDate);
        long workedDays = ChronoUnit.DAYS.between(joinDate, now);
        long remainingWorkDays = ChronoUnit.DAYS.between(now, retirementDate);

        double workProgress = totalWorkDays > 0 ? (double) workedDays / totalWorkDays * 100 : 0;
        double remainingPercentage = 100 - workProgress;

        long workedYears = ChronoUnit.YEARS.between(joinDate, now);
        long workedMonths = ChronoUnit.MONTHS.between(joinDate, now);
        long workedWeeks = ChronoUnit.WEEKS.between(joinDate, now);

        Map<String, Object> result = new HashMap<>();
        result.put("joinDate", joinDate.toString());
        result.put("retirementAge", retirementAge);
        result.put("retirementDate", retirementDate.toString());
        result.put("workProgress", String.format("%.2f", workProgress));
        result.put("remainingPercentage", String.format("%.2f", remainingPercentage));
        result.put("totalWorkDays", totalWorkDays);
        result.put("workedDays", Math.max(0, workedDays));
        result.put("remainingWorkDays", Math.max(0, remainingWorkDays));
        result.put("workedYears", workedYears);
        result.put("workedMonths", workedMonths);
        result.put("workedWeeks", workedWeeks);

        return Result.success(result);
    }

    @GetMapping("/retirement/countdown")
    public Result<Map<String, Object>> getRetirementCountdown(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        WorkSetting setting = workSettingService.findByUserId(userId);
        if (setting == null) {
            return Result.success(new HashMap<>());
        }

        int retirementAge = setting.getRetirementAge() != null ? setting.getRetirementAge() : DEFAULT_RETIREMENT_AGE;

        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        User user = userService.findById(userId);
        if (user != null && user.getBirthDate() != null) {
            birthDate = user.getBirthDate();
        }

        LocalDate retirementDate = birthDate.plusYears(retirementAge);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime retirementDateTime = retirementDate.atStartOfDay();

        long totalSeconds = ChronoUnit.SECONDS.between(now, retirementDateTime);
        boolean isRetired = totalSeconds < 0;
        totalSeconds = Math.abs(totalSeconds);

        long years = totalSeconds / (365L * 24 * 60 * 60);
        long remainingAfterYears = totalSeconds % (365L * 24 * 60 * 60);
        long months = remainingAfterYears / (30L * 24 * 60 * 60);
        long remainingAfterMonths = remainingAfterYears % (30L * 24 * 60 * 60);
        long days = remainingAfterMonths / (24 * 60 * 60);
        long hours = (remainingAfterMonths % (24 * 60 * 60)) / (60 * 60);
        long minutes = (remainingAfterMonths % (60 * 60)) / 60;
        long seconds = remainingAfterMonths % 60;

        Map<String, Object> result = new HashMap<>();
        result.put("birthDate", birthDate.toString());
        result.put("retirementAge", retirementAge);
        result.put("retirementDate", retirementDate.toString());
        result.put("isRetired", isRetired);
        result.put("years", years);
        result.put("months", months);
        result.put("days", days);
        result.put("hours", hours);
        result.put("minutes", minutes);
        result.put("seconds", seconds);
        result.put("totalSeconds", totalSeconds);

        return Result.success(result);
    }

    @GetMapping("/yearly/stats")
    public Result<Map<String, Object>> getYearlyStats(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer year) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        int workDays = workDayRecordService.countByStatus(userId, year, 1);
        int fishDays = workDayRecordService.countByStatus(userId, year, 2);
        int vacationDays = workDayRecordService.countByStatus(userId, year, 3);

        int totalRecordedDays = workDays + fishDays + vacationDays;

        WorkSetting setting = workSettingService.findByUserId(userId);
        int workDaysPerWeek = setting != null && setting.getWorkDaysPerWeek() != null 
            ? setting.getWorkDaysPerWeek() : DEFAULT_WORK_DAYS_PER_WEEK;

        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        long totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear) + 1;

        long totalWeeks = totalDays / 7;
        long estimatedWorkDays = totalWeeks * workDaysPerWeek;

        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("workDays", workDays);
        result.put("fishDays", fishDays);
        result.put("vacationDays", vacationDays);
        result.put("totalRecordedDays", totalRecordedDays);
        result.put("totalDays", totalDays);
        result.put("estimatedWorkDays", estimatedWorkDays);
        result.put("workDaysPerWeek", workDaysPerWeek);

        return Result.success(result);
    }

    @GetMapping("/earnings")
    public Result<Map<String, Object>> getEarnings(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        WorkSetting setting = workSettingService.findByUserId(userId);
        if (setting == null || setting.getJoinDate() == null || setting.getMonthlySalary() == null) {
            return Result.success(new HashMap<>());
        }

        LocalDate now = LocalDate.now();
        LocalDate joinDate = setting.getJoinDate();
        BigDecimal monthlySalary = setting.getMonthlySalary();

        long workedMonths = ChronoUnit.MONTHS.between(joinDate, now);
        long workedDays = ChronoUnit.DAYS.between(joinDate, now);

        BigDecimal totalEarnings = monthlySalary.multiply(BigDecimal.valueOf(workedMonths));
        
        int year = now.getYear();
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        long monthsThisYear = ChronoUnit.MONTHS.between(startOfYear, now);
        BigDecimal earningsThisYear = monthlySalary.multiply(BigDecimal.valueOf(monthsThisYear));

        BigDecimal dailySalary = monthlySalary.divide(BigDecimal.valueOf(22), 2, RoundingMode.HALF_UP);
        BigDecimal earningsToday = dailySalary;

        Map<String, Object> result = new HashMap<>();
        result.put("monthlySalary", monthlySalary);
        result.put("dailySalary", dailySalary);
        result.put("workedMonths", workedMonths);
        result.put("workedDays", workedDays);
        result.put("totalEarnings", totalEarnings);
        result.put("earningsThisYear", earningsThisYear);
        result.put("monthsThisYear", monthsThisYear);
        result.put("earningsToday", earningsToday);

        return Result.success(result);
    }

    @GetMapping("/day/{date}")
    public Result<WorkDayRecord> getDayRecord(
            @PathVariable String date,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        LocalDate workDate = LocalDate.parse(date);
        WorkDayRecord record = workDayRecordService.findByUserIdAndDate(userId, workDate);
        return Result.success(record);
    }

    @PostMapping("/day")
    public Result<WorkDayRecord> saveDayRecord(
            @RequestBody WorkDayRecord workDayRecord,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        workDayRecord.setUserId(userId);
        boolean success = workDayRecordService.saveOrUpdate(workDayRecord);

        if (success) {
            return Result.success("保存成功", workDayRecord);
        } else {
            return Result.error("保存失败");
        }
    }

    @GetMapping("/month")
    public Result<List<WorkDayRecord>> getMonthRecords(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        List<WorkDayRecord> records = workDayRecordService.findByUserIdAndMonth(userId, year, month);
        return Result.success(records);
    }

    @GetMapping("/all")
    public Result<Map<String, Object>> getAllWorkProgress(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未授权");
        }

        Map<String, Object> result = new HashMap<>();

        WorkSetting setting = workSettingService.findByUserId(userId);
        result.put("setting", setting);

        if (setting != null && setting.getJoinDate() != null) {
            result.put("progress", getWorkProgress(token).getData());
            result.put("retirement", getRetirementCountdown(token).getData());
            result.put("earnings", getEarnings(token).getData());
        }

        int currentYear = LocalDate.now().getYear();
        result.put("yearlyStats", getYearlyStats(token, currentYear).getData());

        return Result.success(result);
    }

    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        String jwtToken = token.substring(7);
        if (jwtUtil.validateToken(jwtToken)) {
            return jwtUtil.getUserIdFromToken(jwtToken);
        }
        return null;
    }
}
