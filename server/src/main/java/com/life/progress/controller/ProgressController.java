package com.life.progress.controller;

import com.life.progress.common.Result;
import com.life.progress.entity.User;
import com.life.progress.service.UserService;
import com.life.progress.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${system.life-expectancy:80}")
    private Integer lifeExpectancy;

    @GetMapping("/life")
    public Result<Map<String, Object>> getLifeProgress(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        LocalDate birthDate = null;

        if (userId != null) {
            User user = userService.findById(userId);
            if (user != null && user.getBirthDate() != null) {
                birthDate = user.getBirthDate();
            }
        }

        if (birthDate == null && token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            if (jwtUtil.validateToken(jwtToken)) {
                Long uid = jwtUtil.getUserIdFromToken(jwtToken);
                User user = userService.findById(uid);
                if (user != null && user.getBirthDate() != null) {
                    birthDate = user.getBirthDate();
                }
            }
        }

        if (birthDate == null) {
            birthDate = LocalDate.of(1990, 1, 1);
        }

        LocalDate now = LocalDate.now();
        LocalDate deathDate = birthDate.plusYears(lifeExpectancy);

        long totalDays = ChronoUnit.DAYS.between(birthDate, deathDate);
        long livedDays = ChronoUnit.DAYS.between(birthDate, now);
        long remainingDays = ChronoUnit.DAYS.between(now, deathDate);

        double progress = (double) livedDays / totalDays * 100;
        double remainingPercentage = 100 - progress;

        long livedYears = ChronoUnit.YEARS.between(birthDate, now);
        long livedMonths = ChronoUnit.MONTHS.between(birthDate, now);
        long livedWeeks = ChronoUnit.WEEKS.between(birthDate, now);

        Map<String, Object> result = new HashMap<>();
        result.put("birthDate", birthDate.toString());
        result.put("lifeExpectancy", lifeExpectancy);
        result.put("progress", String.format("%.2f", progress));
        result.put("remainingPercentage", String.format("%.2f", remainingPercentage));
        result.put("livedDays", livedDays);
        result.put("remainingDays", remainingDays);
        result.put("totalDays", totalDays);
        result.put("livedYears", livedYears);
        result.put("livedMonths", livedMonths);
        result.put("livedWeeks", livedWeeks);

        return Result.success(result);
    }

    @GetMapping("/year")
    public Result<Map<String, Object>> getYearProgress() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        long totalDays = ChronoUnit.DAYS.between(startOfYear, endOfYear) + 1;
        long pastDays = ChronoUnit.DAYS.between(startOfYear, now) + 1;
        long remainingDays = totalDays - pastDays;

        double progress = (double) pastDays / totalDays * 100;
        double remainingPercentage = 100 - progress;

        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int weekOfYear = now.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int dayOfWeek = now.getDayOfWeek().getValue();

        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        long daysInMonth = ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth) + 1;
        long daysPassedInMonth = ChronoUnit.DAYS.between(firstDayOfMonth, now) + 1;
        double monthProgress = (double) daysPassedInMonth / daysInMonth * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("day", day);
        result.put("weekOfYear", weekOfYear);
        result.put("dayOfWeek", dayOfWeek);
        result.put("totalDays", totalDays);
        result.put("pastDays", pastDays);
        result.put("remainingDays", remainingDays);
        result.put("progress", String.format("%.2f", progress));
        result.put("remainingPercentage", String.format("%.2f", remainingPercentage));
        result.put("daysInMonth", daysInMonth);
        result.put("daysPassedInMonth", daysPassedInMonth);
        result.put("monthProgress", String.format("%.2f", monthProgress));

        return Result.success(result);
    }

    @GetMapping("/season")
    public Result<Map<String, Object>> getSeasonProgress() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();

        String season;
        LocalDate seasonStart;
        LocalDate seasonEnd;

        if (month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY) {
            season = "冬季";
            if (month == Month.DECEMBER) {
                seasonStart = LocalDate.of(year, 12, 1);
                seasonEnd = LocalDate.of(year + 1, 2, 28);
                if (LocalDate.of(year + 1, 2, 29).isLeapYear()) {
                    seasonEnd = LocalDate.of(year + 1, 2, 29);
                }
            } else {
                seasonStart = LocalDate.of(year, 12, 1).minusYears(1);
                seasonEnd = LocalDate.of(year, 2, 28);
                if (LocalDate.of(year, 2, 29).isLeapYear()) {
                    seasonEnd = LocalDate.of(year, 2, 29);
                }
            }
        } else if (month == Month.MARCH || month == Month.APRIL || month == Month.MAY) {
            season = "春季";
            seasonStart = LocalDate.of(year, 3, 1);
            seasonEnd = LocalDate.of(year, 5, 31);
        } else if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
            season = "夏季";
            seasonStart = LocalDate.of(year, 6, 1);
            seasonEnd = LocalDate.of(year, 8, 31);
        } else {
            season = "秋季";
            seasonStart = LocalDate.of(year, 9, 1);
            seasonEnd = LocalDate.of(year, 11, 30);
        }

        long totalDays = ChronoUnit.DAYS.between(seasonStart, seasonEnd) + 1;
        long pastDays = ChronoUnit.DAYS.between(seasonStart, now) + 1;
        long remainingDays = totalDays - pastDays;

        double progress = (double) pastDays / totalDays * 100;
        double remainingPercentage = 100 - progress;

        Map<String, Object> nextSeason = getNextSeasonInfo(year, month);

        Map<String, Object> result = new HashMap<>();
        result.put("season", season);
        result.put("seasonStart", seasonStart.toString());
        result.put("seasonEnd", seasonEnd.toString());
        result.put("totalDays", totalDays);
        result.put("pastDays", pastDays);
        result.put("remainingDays", remainingDays);
        result.put("progress", String.format("%.2f", progress));
        result.put("remainingPercentage", String.format("%.2f", remainingPercentage));
        result.put("nextSeason", nextSeason);

        return Result.success(result);
    }

    private Map<String, Object> getNextSeasonInfo(int year, Month month) {
        Map<String, Object> nextSeason = new HashMap<>();

        if (month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY) {
            nextSeason.put("name", "春季");
            nextSeason.put("start", LocalDate.of(year, 3, 1).toString());
        } else if (month == Month.MARCH || month == Month.APRIL || month == Month.MAY) {
            nextSeason.put("name", "夏季");
            nextSeason.put("start", LocalDate.of(year, 6, 1).toString());
        } else if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
            nextSeason.put("name", "秋季");
            nextSeason.put("start", LocalDate.of(year, 9, 1).toString());
        } else {
            nextSeason.put("name", "冬季");
            nextSeason.put("start", LocalDate.of(year, 12, 1).toString());
        }

        return nextSeason;
    }

    @GetMapping("/week")
    public Result<Map<String, Object>> getWeekProgress() {
        LocalDate now = LocalDate.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        int dayOfWeekValue = dayOfWeek.getValue();
        LocalDate startOfWeek = now.minusDays(dayOfWeekValue - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        long totalDays = 7;
        long pastDays = dayOfWeekValue;
        long remainingDays = totalDays - pastDays;

        double progress = (double) pastDays / totalDays * 100;
        double remainingPercentage = 100 - progress;

        Map<String, Object> result = new HashMap<>();
        result.put("startOfWeek", startOfWeek.toString());
        result.put("endOfWeek", endOfWeek.toString());
        result.put("dayOfWeek", dayOfWeekValue);
        result.put("dayOfWeekName", dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINA));
        result.put("totalDays", totalDays);
        result.put("pastDays", pastDays);
        result.put("remainingDays", remainingDays);
        result.put("progress", String.format("%.2f", progress));
        result.put("remainingPercentage", String.format("%.2f", remainingPercentage));

        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<Map<String, Object>> getAllProgress(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> result = new HashMap<>();

        result.put("life", getLifeProgress(userId, token).getData());
        result.put("year", getYearProgress().getData());
        result.put("season", getSeasonProgress().getData());
        result.put("week", getWeekProgress().getData());

        return Result.success(result);
    }
}
