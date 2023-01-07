package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public static void saveLastShares(@NonNull TPortfolio portfolio) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            List<String> savedShares = new ArrayList<>();
            val shares = portfolio.getShares();
            for (final TShare share : shares) {
                val lastActiveLongShareInformationOptional = share.getLastLongShareInformation();
                if (lastActiveLongShareInformationOptional.isPresent()) {
                    val information = lastActiveLongShareInformationOptional.get();
                    bw.write(share.getId());
                    bw.write(",");
                    bw.write(information.toStringForSave());
                    bw.newLine();
                    savedShares.add(share.getId() + ": " + information.toStringForSave() + " / ");
                }
            }
            log.info("savedShares: " + savedShares);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static String formatDouble(@Nullable Double value) {
        return value == null
                ? "-"
                : String.format("%.2f", value);
    }
}
