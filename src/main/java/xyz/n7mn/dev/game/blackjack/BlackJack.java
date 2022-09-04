package xyz.n7mn.dev.game.blackjack;

import lombok.Getter;
import lombok.Setter;
import xyz.n7mn.dev.game.blackjack.data.BlackJackData;
import xyz.n7mn.dev.game.blackjack.data.ResultEnum;
import xyz.n7mn.dev.util.MathUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class BlackJack {
    /**
     * ここら辺のコードは難読化されてるレベルで見にくいです
     */

    private List<BlackJackData> tableList = new ArrayList<>();
    private List<BlackJackData> dealerList = new ArrayList<>();
    private List<BlackJackData> playerList = new ArrayList<>();
    int dealer = 0;
    int player = 0;
    long coin;

    public BlackJack(long coin) {
        List<Integer> cardN = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        List<String> cardS = Arrays.asList(":spades:", ":clubs:", ":hearts:", ":diamonds:");

        cardS.forEach(str -> cardN.forEach(number -> tableList.add(new BlackJackData(str, number))));

        this.coin = coin;
    }

    public BlackJack(long coin, List<String> dealerCard, List<String> playerCard) {
        List<Integer> cardN = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
        List<String> cardS = Arrays.asList(":spades:", ":clubs:", ":hearts:", ":diamonds:");

        cardS.forEach(str -> cardN.forEach(number -> {
            BlackJackData data = new BlackJackData(str, number);
            if (checkRemoveI(str, number, dealerCard)) {
                if (checkRemoveI(str, number, playerCard)) {
                    tableList.add(data);
                } else {
                    playerList.add(data);
                }
            } else {
                dealerList.add(data);
            }
        }));

        calculate(dealerList, true);
        calculate(playerList, false);

        this.coin = coin;
        //this.dealer = dealer;
        //this.player = player;
    }

    private boolean checkRemoveI(String cardS, int cardN, List<String> check) {
        return check.stream().noneMatch(stream -> stream.replaceAll("[0-9]", "").equals(cardS) && cardN == Integer.parseInt(stream.replaceAll("[^\\d]", "")));
    }

    public BlackJackData getRandomBJ(boolean dealer) {
        Collections.shuffle(tableList);
        int pickup = new SecureRandom().nextInt(tableList.size());

        BlackJackData data = tableList.remove(pickup);
        if (dealer) {
            dealerList.add(data);
        } else {
            playerList.add(data);
        }

        calculate(data, dealer);
        return data;
    }

    public void calculate(List<BlackJackData> list, boolean dealer) {
        for (BlackJackData data : list) {
            calculate(data, dealer);
        }
    }

    public void calculate(BlackJackData data, boolean dealer) {
        int num = Math.min(data.getNumber(), 10);

        if (num == 1) {
            addNumber(11, dealer);
        } else {
            addNumber(num, dealer);
        }

        int temp = dealer ? this.dealer : this.player;

        if (temp > 21) {
            List<BlackJackData> dataList = dealer ? this.dealerList : this.playerList;

            for (BlackJackData d : dataList) {
                int r = dealer ? this.dealer : this.player;
                if (r <= 21) break;
                if (d.getNumber() == 1 && !d.isUsed()) {
                    d.setUsed(true);
                    addNumber(-10, dealer);
                }
            }
        }
    }

    public void addNumber(int num, boolean dealer) {
        if (dealer) {
            this.dealer += num;
        } else {
            this.player += num;
        }
    }

    public ResultEnum resultCheck() {

        boolean isBlackJackDealer = isBlackJack(true);
        boolean isBlackJackPlayer = isBlackJack(false);

        if (isBlackJackPlayer && isBlackJackDealer) return ResultEnum.DRAW;

        if (isBlackJackDealer) return ResultEnum.DEALER_BLACKJACK;
        if (isBlackJackPlayer) return ResultEnum.PLAYER_BLACKJACK;

        if (dealer == player && dealer <= 21) return ResultEnum.DRAW;

        if (player > 21) return ResultEnum.DEALER;
        if (dealer > 21) return ResultEnum.PLAYER;

        if (player > dealer) {
            return ResultEnum.PLAYER;
        } else {
            return ResultEnum.DEALER;
        }
    }

    public long resultCoin(double multiply) {
        return MathUtil.multiply(coin , multiply);
    }

    public boolean isBlackJack(boolean dealer) {
        List<BlackJackData> dataList = dealer ? this.dealerList : this.playerList;
        if (dataList.size() == 2) {
            int i1 = Math.min(dataList.get(0).getNumber(), 10);
            int i2 = Math.min(dataList.get(1).getNumber(), 10);

            return (i1 == 1 && i2 == 10) || (i2 == 1 && i1 == 10);
        }
        return false;
    }

    public static boolean isBlackJack(BlackJackData data1, BlackJackData data2) {
        int i1 = Math.min(data1.getNumber(), 10);
        int i2 = Math.min(data2.getNumber(), 10);

        return (i1 == 1 && i2 == 10) || (i2 == 1 && i1 == 10);
    }
}