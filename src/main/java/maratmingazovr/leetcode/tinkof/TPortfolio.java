package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class TPortfolio {

    @NonNull
    private final List<TShare> shares = new ArrayList<>();

    @NonNull
    private final List<TOperation> operations = new ArrayList<>();

    @NonNull
    private Long buyOperationsCount = 0L;

    @NonNull
    private Long sellOperationsCount = 0L;

    @NonNull
    private Long takeProfitCount = 0L;

    @NonNull
    private Long stopLossCount = 0L;

    @NonNull
    private Double dollarBalance = 0.0;

    @NonNull
    private Double rubBalance = 0.0;

    @NonNull
    private String dollarBalanceFigi = "BBG0013HGFT4";
    @NonNull
    private String rubBalanceFigi = "RUB000UTSTOM";

    public TPortfolio() {
        shares.add(new TShare("AAPL","BBG000B9XRY4"));
        shares.add(new TShare("TSLA","BBG000N9MNX3"));
        shares.add(new TShare("SBER","BBG004730N88"));
        shares.add(new TShare("VTBR","BBG004730ZJ9"));
        shares.add(new TShare("GAZP","TCSS07661625"));
        shares.add(new TShare("ROSN","BBG004731354"));
        shares.add(new TShare("LKOH","TCS009024277"));
        shares.add(new TShare("BABA","BBG006G2JVL2"));
        shares.add(new TShare("YNDX","BBG006L8G4H1"));
        shares.add(new TShare("INTC","BBG000C0G1D1"));
        shares.add(new TShare("COIN","BBG00ZGF7771"));
        shares.add(new TShare("GTLB","BBG00DHTYPH8"));
        shares.add(new TShare("DOCU","BBG000N7KJX8"));
        shares.add(new TShare("AMD","BBG000BBQCY0"));
        shares.add(new TShare("ATVI","BBG000CVWGS6"));
        shares.add(new TShare("AMZN","BBG000BVPV84"));
        shares.add(new TShare("GOOGL","BBG009S39JX6"));
        shares.add(new TShare("IBM","BBG000BLNNH6"));
    }
}
