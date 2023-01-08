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
    private static final Double BB_MULTIPLICATOR = 2.0;

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
                val bollingerUp = candle.getSimpleMovingAverage() + stdDev * BB_MULTIPLICATOR;
                val bollingerDown = candle.getSimpleMovingAverage() - stdDev * BB_MULTIPLICATOR;
                candle.setBollingerUp(bollingerUp);
                candle.setBollingerDown(bollingerDown);
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
            if (share.size() < 4) {
                continue;
            }
            val shareId = share.get(0);
            val shareBuyPrice = Double.valueOf(share.get(1));
            val simpleMovingAverage = Double.valueOf(share.get(2));
            val bollingerUp = Double.valueOf(share.get(3));
            val bollingerDown = Double.valueOf(share.get(4));
            for (TShare portfolioShare : portfolio.getShares()) {
                if (portfolioShare.getId().equals(shareId)) {
                    val activeShareInfo = new TActiveShareInfo(shareId,
                                                                              shareBuyPrice,
                                                                              simpleMovingAverage,
                                                                              bollingerUp,
                                                                              bollingerDown,
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
