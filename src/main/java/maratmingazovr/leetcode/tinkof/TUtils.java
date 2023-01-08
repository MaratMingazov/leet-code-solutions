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

    private static final Integer SIMPLE_MOVING_AVERAGE_SIZE = 20;

    private static final Integer RSI_PERIOD = 20;


    public static final Double TAKE_PROFIT_PERCENT = 0.02;
    public static final Double STOP_LOSS_PERCENT = 0.02;

    private static final String FILENAME = "src/main/java/maratmingazovr/leetcode/tinkof/data.txt";

    private static Logger log = LoggerFactory.getLogger(TUtils.class);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.yyyy:hh:mm")
            .withZone(ZoneId.systemDefault());


    public static void calculateSimpleMovingAverage(@NonNull TShare share,
                                                    @NonNull CandleInterval interval) {
        val candles = share.getCandlesMap().get(interval);
        if (candles.size() < SIMPLE_MOVING_AVERAGE_SIZE) {
            return;
        }
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getSimpleMovingAverage() != null) {
            return;
        }

        for (int i = 0; i < candles.size(); i++) {
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
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getBollingerUp() != null) {
            // we already calculated all values
            return;
        }

        for (int i = 0; i < candles.size(); i++) {
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
                    candle.setRsi(candle1Day.getRsi());
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
        val lastCandle = candles.get(candles.size()-1);
        if (lastCandle.getRsi() != null) {
            // we already calculated all values
            return;
        }
        for (int i = 1; i < candles.size(); i++) {
            val previousCandle = candles.get(i-1);
            val candle = candles.get(i);
            val change = candle.getClose() - previousCandle.getClose();
            val upWardMove = change > 0 ? change : 0.0;
            val downWardMove = change < 0 ? Math.abs(change) : 0.0;
            candle.setUpWardMove(upWardMove);
            candle.setDownWardMove(downWardMove);
        }

        for (int i = 1; i < candles.size(); i++) {
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
                val сandle = candles.get(i + RSI_PERIOD - 1);
                сandle.setUpWardMoveAverage(upWardAverage);
                сandle.setDownWardMoveAverage(downWardAverage);
            }
        }
        for (int i = 1; i < candles.size(); i++) {
            val previousCandle = candles.get(i-1);
            val candle = candles.get(i);
            if (previousCandle.getUpWardMoveAverage() == null) {
                // we can not calculate because we have not value
                continue;
            }
            val k = 2/(SIMPLE_MOVING_AVERAGE_SIZE + 1);
            val previousUpWardMoveAverage = previousCandle.getUpWardMoveAverage();
            val previousDownWardMoveAverage = previousCandle.getDownWardMoveAverage();
            val upWardMoveAverage = (candle.getUpWardMove() - previousUpWardMoveAverage) * k + previousUpWardMoveAverage;
            val downWardMoveAverage = (candle.getDownWardMove() - previousDownWardMoveAverage) * k + previousDownWardMoveAverage;
            val relativeStrenght = upWardMoveAverage + downWardMoveAverage;
            val rsi = 100 - 100 / (relativeStrenght + 1);

            candle.setUpWardMoveAverage(upWardMoveAverage);
            candle.setDownWardMoveAverage(downWardMoveAverage);
            candle.setRsi(rsi);

            if (rsi > 70.0 || rsi < 30.0) {
                candle.setPreviousExtremumRSI(rsi);
            } else {
                candle.setPreviousExtremumRSI(previousCandle.getPreviousExtremumRSI());
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
            if (share.size() < 8) {
                continue;
            }
            val shareId = share.get(0);
            val shareBuyPrice = Double.valueOf(share.get(1));
            val shareSellPrice = Double.valueOf(share.get(2));
            val simpleMovingAverage = Double.valueOf(share.get(3));
            val bollingerUp = Double.valueOf(share.get(4));
            val bollingerDown = Double.valueOf(share.get(5));
            val rsi = Double.valueOf(share.get(6));
            val rsiPrev = Double.valueOf(share.get(7));
            for (TShare portfolioShare : portfolio.getShares()) {
                if (portfolioShare.getId().equals(shareId)) {
                    val activeShareInfo = new TActiveShareInfo(shareBuyPrice,
                                                               shareSellPrice,
                                                               simpleMovingAverage,
                                                               bollingerUp,
                                                               bollingerDown,
                                                               rsi,
                                                               rsiPrev,
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
        if (hourMinute < 5) {
            return instant;
        } else if(hourMinute < 10) {
            return instant.plus(5L, ChronoUnit.MINUTES);
        } else if(hourMinute < 15) {
            return instant.plus(10L, ChronoUnit.MINUTES);
        } else if(hourMinute < 20) {
            return instant.plus(15L, ChronoUnit.MINUTES);
        } else if(hourMinute < 25) {
            return instant.plus(20L, ChronoUnit.MINUTES);
        } else if(hourMinute < 30) {
            return instant.plus(25L, ChronoUnit.MINUTES);
        } else if(hourMinute < 35) {
            return instant.plus(30L, ChronoUnit.MINUTES);
        } else if(hourMinute < 40) {
            return instant.plus(35L, ChronoUnit.MINUTES);
        } else if(hourMinute < 45) {
            return instant.plus(40L, ChronoUnit.MINUTES);
        } else if(hourMinute < 50) {
            return instant.plus(45L, ChronoUnit.MINUTES);
        } else if(hourMinute < 55) {
            return instant.plus(50L, ChronoUnit.MINUTES);
        } else {
            return instant.plus(55L, ChronoUnit.MINUTES);
        }
    }

    @NonNull
    public static Instant getTruncatedTo15Min(@NonNull Instant instant) {
        int hourMinute = instant.atZone(ZoneOffset.UTC).getMinute();
        if (hourMinute < 15) {
            return instant;
        } else if(hourMinute < 30) {
            return instant.plus(15L, ChronoUnit.MINUTES);
        } else if(hourMinute < 45) {
            return instant.plus(30L, ChronoUnit.MINUTES);
        } else {
            return instant.plus(45L, ChronoUnit.MINUTES);
        }
    }
}
