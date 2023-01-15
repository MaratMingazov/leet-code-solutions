package maratmingazovr.leetcode.tinkof;

import com.google.protobuf.Timestamp;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShareInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

public class TUtils {

    private static final Integer SIMPLE_MOVING_AVERAGE_SIZE = 3;

    private static final Integer RSI_PERIOD = 20;


    public static final Double TAKE_PROFIT_PERCENT = 0.003;
    public static final Double STOP_LOSS_PERCENT = 0.01;

    private static final String FILENAME = "src/main/java/maratmingazovr/leetcode/tinkof/data.txt";

    private static Logger log = LoggerFactory.getLogger(TUtils.class);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.yyyy:hh:mm")
            .withZone(ZoneId.systemDefault());


    public static void calculateSimpleMovingAverage(@NonNull TShare share,
                                                    @NonNull CandleInterval interval) {
        val candles = share.getCandlesMap().get(interval);
        if (candles.size() < SIMPLE_MOVING_AVERAGE_SIZE) {
            return;
        }

        int firstIndex = 0;
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getSimpleMovingAverage() != null) {
            // we need to recalculate last candle only
            firstIndex = candles.size() - SIMPLE_MOVING_AVERAGE_SIZE;
        }

        for (int i = firstIndex; i < candles.size(); i++) {
            if (i + SIMPLE_MOVING_AVERAGE_SIZE <= candles.size()) {
                double sum = 0.0;
                for (int j = 0; j < SIMPLE_MOVING_AVERAGE_SIZE; j++) {
                    val candle = candles.get(i+j);
                    sum += candle.getClose();
                }
                val average = sum / SIMPLE_MOVING_AVERAGE_SIZE;
                val сandle = candles.get(i + SIMPLE_MOVING_AVERAGE_SIZE - 1);
                сandle.setSimpleMovingAverage(average);
            }
        }
    }

    public static void calculateBollingerUpAndDown(@NonNull TShare share,
                                                   @NonNull CandleInterval interval) {
        val candles = share.getCandlesMap().get(interval);
        if (candles.size() < SIMPLE_MOVING_AVERAGE_SIZE) {
            return;
        }

        int firstIndex = 0;
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getSimpleMovingAverage() != null) {
            // we need to recalculate last candle only
            firstIndex = candles.size() - SIMPLE_MOVING_AVERAGE_SIZE;
        }

        for (int i = firstIndex; i < candles.size(); i++) {
            if (candles.get(i).getSimpleMovingAverage() == null) {
                // we can not calculate because we have not SMA value
                continue;
            }
            if (i + SIMPLE_MOVING_AVERAGE_SIZE <= candles.size()) {
                double sum = 0.0;
                for (int j = 0; j < SIMPLE_MOVING_AVERAGE_SIZE; j++) {
                    val candle = candles.get(i+j);
                    sum += Math.pow(candle.getClose()-candle.getSimpleMovingAverage(), 2);
                }
                val stdDev = Math.sqrt(sum / SIMPLE_MOVING_AVERAGE_SIZE);
                val candle = candles.get(i + SIMPLE_MOVING_AVERAGE_SIZE - 1);
                val bollingerUp = candle.getSimpleMovingAverage() + stdDev * share.getBbMultiplicatorUp();
                val bollingerDown = candle.getSimpleMovingAverage() - stdDev * share.getBbMultiplicatorDown();
                candle.setBollingerUp(bollingerUp);
                candle.setBollingerDown(bollingerDown);
                candle.setStdDev(stdDev);
            }
        }
    }

    public static void calculateRSI(@NonNull List<TCandle> candles1Day,
                                    @NonNull List<TCandle> candles) {
        for (TCandle candle : candles) {
            val candleInstant = candle.getInstant().truncatedTo(ChronoUnit.DAYS);
            for (TCandle candle1Day : candles1Day) {
                val candle1DayInstant = candle1Day.getInstant().truncatedTo(ChronoUnit.DAYS);
                if (candle1DayInstant.equals(candleInstant)) {
                    candle.setTodayRSI(candle1Day.getTodayRSI());
                    candle.setYesterdayRSI(candle1Day.getYesterdayRSI());
                    candle.setLastRSI(candle1Day.getLastRSI());
                    candle.setTodayRSIInstant(candle1Day.getTodayRSIInstant());
                    candle.setYesterdayRSIInstant(candle1Day.getYesterdayRSIInstant());
                    candle.setLastRSIInstant(candle1Day.getLastRSIInstant());
                }
            }
        }
    }

    public static void calculateRSI(@NonNull TShare share,
                                    @NonNull CandleInterval interval) {
        val candles = share.getCandlesMap().get(interval);
        if (candles.size() < RSI_PERIOD) {
            return;
        }

        int firstIndex = 1;
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getTodayRSI() != null) {
            // we need to recalculate last candle only
            firstIndex = candles.size() - RSI_PERIOD;
            if (firstIndex == 0) {
                firstIndex = 1;
            }
        }
        for (int i = firstIndex; i < candles.size(); i++) {
            val previousCandle = candles.get(i-1);
            val candle = candles.get(i);
            val change = candle.getClose() - previousCandle.getClose();
            val upWardMove = change > 0 ? change : 0.0;
            val downWardMove = change < 0 ? Math.abs(change) : 0.0;
            candle.setUpWardMove(upWardMove);
            candle.setDownWardMove(downWardMove);
        }

        for (int i = firstIndex; i < candles.size(); i++) {
            if (i + RSI_PERIOD <= candles.size()) {
                double sumUpWard = 0.0;
                double sumDownWard = 0.0;
                for (int j = 0; j < RSI_PERIOD; j++) {
                    val candle = candles.get(i+j);
                    sumUpWard += candle.getUpWardMove();
                    sumDownWard += candle.getDownWardMove();
                }
                val upWardAverage = sumUpWard / RSI_PERIOD;
                val downWardAverage = sumDownWard / RSI_PERIOD;
                val candle = candles.get(i + RSI_PERIOD - 1);
                candle.setUpWardMoveAverage(upWardAverage);
                candle.setDownWardMoveAverage(downWardAverage);
            }
        }
        for (int i = firstIndex; i < candles.size(); i++) {
            val previousCandle = candles.get(i-1);
            val candle = candles.get(i);
            if (previousCandle.getUpWardMoveAverage() == null) {
                // we can not calculate because we have not value
                continue;
            }
            val k = 2/((double)RSI_PERIOD + 1);
            val previousUpWardMoveAverage = previousCandle.getUpWardMoveAverage();
            val previousDownWardMoveAverage = previousCandle.getDownWardMoveAverage();
            val upWardMoveAverage = (candle.getUpWardMove() - previousUpWardMoveAverage) * k + previousUpWardMoveAverage;
            val downWardMoveAverage = (candle.getDownWardMove() - previousDownWardMoveAverage) * k + previousDownWardMoveAverage;
            val relativeStrenght = upWardMoveAverage / downWardMoveAverage;
            val rsi = 100 - 100 / (relativeStrenght + 1);

            candle.setUpWardMoveAverage(upWardMoveAverage);
            candle.setDownWardMoveAverage(downWardMoveAverage);
            candle.setTodayRSI(rsi);
            candle.setTodayRSIInstant(candle.getInstant());
            candle.setYesterdayRSI(previousCandle.getTodayRSI());
            candle.setYesterdayRSIInstant(previousCandle.getTodayRSIInstant());

            if (rsi > 70.0 || rsi < 30.0) {
                candle.setLastRSI(rsi);
                candle.setLastRSIInstant(candle.getInstant());
            } else {
                candle.setLastRSI(previousCandle.getLastRSI());
                candle.setLastRSIInstant(previousCandle.getLastRSIInstant());
            }
        }
    }

    @NonNull
    public static Double moneyValueToDouble(@NonNull MoneyValue value) {
        val quotation = Quotation.newBuilder().setUnits(value.getUnits()).setNano(value.getNano()).build();
        return quotationToBigDecimal(quotation).doubleValue();
    }

    @NonNull
    public static Double QuotationToDouble(@NonNull Quotation value) {
        val quotation = Quotation.newBuilder().setUnits(value.getUnits()).setNano(value.getNano()).build();
        return quotationToBigDecimal(quotation).doubleValue();
    }

    @NonNull
    public static Quotation DoubleToQuotation(@NonNull Double value) {
        val valueDecimal = BigDecimal.valueOf(value);
        return Quotation.newBuilder()
                        .setUnits(valueDecimal.longValue() )
                        .setNano(valueDecimal.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000)).intValue())
                        .build();
    }



    @NonNull
    public static Instant timeStampToInstant(@NonNull Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static void saveLastActiveLongShares(@NonNull TPortfolio portfolio) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            List<String> savedShares = new ArrayList<>();
            val shares = portfolio.getShares();
            for (final TShare share : shares) {
                val activeShareInfo = share.getActiveShareInfo();
                if (activeShareInfo.getBuyPrice().equals(0.0) && activeShareInfo.getSellPrice().equals(0.0)) {
                    continue;
                }
                bw.write(share.getId());
                bw.write(",");
                bw.write(activeShareInfo.toStringForSave());
                bw.newLine();
                savedShares.add(activeShareInfo.toStringForSave());
            }
            log.info("savedShares: " + savedShares);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadLastActiveLongShares(@NonNull TPortfolio portfolio) {
        val shares = Util.loadCSV(FILENAME);
        int count = 0;
        for (List<String> share : shares) {
            if (share.size() < 3) {
                continue;
            }
            val shareId = share.get(0);
            val shareBuyPrice = Double.valueOf(share.get(1));
            val shareSellPrice = Double.valueOf(share.get(2));
            for (TShare portfolioShare : portfolio.getShares()) {
                if (portfolioShare.getId().equals(shareId)) {
                    val activeShareInfo = new TActiveShareInfo(shareBuyPrice,
                                                               shareSellPrice,
                                                               0.0,
                                                               0.0,
                                                               0.0,
                                                               0.0,0.0,0.0, Instant.now(),Instant.now(),Instant.now(),
                                                               CandleInterval.CANDLE_INTERVAL_UNSPECIFIED);
                    portfolioShare.setActiveShareInfo(activeShareInfo);
                    count++;
                }
            }
        }
        log.info("load shares = " + count);
    }

    @NonNull
    public static String formatDouble(@Nullable Double value) {
        return value == null
                ? "-"
                : String.format("%.2f", value);
    }

    @NonNull
    public static Instant getTruncatedTo5Min(@NonNull Instant instant) {
        int hourMinute = instant.atZone(ZoneOffset.UTC).getMinute();
        val result = instant.truncatedTo(ChronoUnit.HOURS);
        if (hourMinute < 5) {
            return result;
        } else if(hourMinute < 10) {
            return result.plus(5L, ChronoUnit.MINUTES);
        } else if(hourMinute < 15) {
            return result.plus(10L, ChronoUnit.MINUTES);
        } else if(hourMinute < 20) {
            return result.plus(15L, ChronoUnit.MINUTES);
        } else if(hourMinute < 25) {
            return result.plus(20L, ChronoUnit.MINUTES);
        } else if(hourMinute < 30) {
            return result.plus(25L, ChronoUnit.MINUTES);
        } else if(hourMinute < 35) {
            return result.plus(30L, ChronoUnit.MINUTES);
        } else if(hourMinute < 40) {
            return result.plus(35L, ChronoUnit.MINUTES);
        } else if(hourMinute < 45) {
            return result.plus(40L, ChronoUnit.MINUTES);
        } else if(hourMinute < 50) {
            return result.plus(45L, ChronoUnit.MINUTES);
        } else if(hourMinute < 55) {
            return result.plus(50L, ChronoUnit.MINUTES);
        } else {
            return result.plus(55L, ChronoUnit.MINUTES);
        }
    }

    @NonNull
    public static Instant getTruncatedTo15Min(@NonNull Instant instant) {
        int hourMinute = instant.atZone(ZoneOffset.UTC).getMinute();
        val result = instant.truncatedTo(ChronoUnit.HOURS);
        if (hourMinute < 15) {
            return result;
        } else if(hourMinute < 30) {
            return result.plus(15L, ChronoUnit.MINUTES);
        } else if(hourMinute < 45) {
            return result.plus(30L, ChronoUnit.MINUTES);
        } else {
            return result.plus(45L, ChronoUnit.MINUTES);
        }
    }

    public static String formatInstant(@Nullable Instant instant) {
        return instant == null
                ? ""
                : TIME_FORMATTER.format(instant);
    }
}
