package trinity.play2learn.backend.investment;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountDepositRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountRegisterRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.request.SavingAccountWithdrawalRequestDto;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.request.StockOrderStopRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvestmentTestMother {

    // Constantes de IDs
    public static final Long DEFAULT_FIXED_TERM_DEPOSIT_ID = 801L;
    public static final Long DEFAULT_SAVING_ACCOUNT_ID = 901L;
    public static final Long DEFAULT_STOCK_ID = 1001L;
    public static final Long DEFAULT_ORDER_ID = 1101L;
    public static final Long DEFAULT_STOCK_HISTORY_ID = 1201L;
    public static final Long DEFAULT_WALLET_ID = 1001L;
    public static final Long DEFAULT_STUDENT_ID = 401L;

    // Constantes de valores comunes
    public static final String DEFAULT_STUDENT_EMAIL = "student@example.com";
    public static final Double DEFAULT_AMOUNT = 1000.0;
    public static final Double DEFAULT_INITIAL_AMOUNT = 5000.0;
    public static final Double DEFAULT_CURRENT_AMOUNT = 5500.0;
    public static final Double DEFAULT_INTEREST = 500.0;
    public static final Double DEFAULT_INITIAL_PRICE = 100.0;
    public static final Double DEFAULT_CURRENT_PRICE = 110.0;
    public static final BigInteger DEFAULT_QUANTITY = BigInteger.valueOf(10);
    public static final BigInteger DEFAULT_TOTAL_AMOUNT = BigInteger.valueOf(1000);
    public static final BigInteger DEFAULT_AVAILABLE_AMOUNT = BigInteger.valueOf(500);
    public static final BigInteger DEFAULT_SOLD_AMOUNT = BigInteger.valueOf(500);
    public static final String DEFAULT_SAVING_ACCOUNT_NAME = "Caja de Ahorro Principal";
    public static final String DEFAULT_STOCK_NAME = "Acción Ejemplo";
    public static final String DEFAULT_STOCK_ABBREVIATION = "AE";

    // Constantes de montos comunes para tests
    public static final Double AMOUNT_LARGE = 10000.0;
    public static final Double AMOUNT_MEDIUM = 1000.0;
    public static final Double AMOUNT_SMALL = 100.0;
    public static final Double AMOUNT_MINIMUM = 0.01;
    public static final Double AMOUNT_ZERO = 0.0;
    public static final Double AMOUNT_NEGATIVE = -100.0;

    // Constantes de intereses para tests
    public static final Double INTEREST_MEDIUM = 150.0;
    public static final Double INTEREST_SMALL = 50.0;
    public static final Double INTEREST_ACCUMULATED = 100.0;

    // Builders para FixedTermDeposit
    public static FixedTermDeposit.FixedTermDepositBuilder fixedTermDepositBuilder(
            Long id, Double amountInvested, Double amountReward, FixedTermDays fixedTermDays,
            FixedTermState state, Wallet wallet) {
        return FixedTermDeposit.builder()
            .id(id)
            .amountInvested(amountInvested)
            .amountReward(amountReward)
            .fixedTermDays(fixedTermDays)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(fixedTermDays.getValor()))
            .fixedTermState(state)
            .wallet(wallet);
    }

    public static FixedTermDeposit fixedTermDeposit(Long id, Double amountInvested, Double amountReward,
            FixedTermDays fixedTermDays, FixedTermState state, Wallet wallet) {
        return fixedTermDepositBuilder(id, amountInvested, amountReward, fixedTermDays, state, wallet).build();
    }

    public static FixedTermDeposit fixedTermDepositInProgress(Long id, Double amountInvested, LocalDate endDate) {
        Wallet wallet = defaultWallet();
        return FixedTermDeposit.builder()
            .id(id)
            .amountInvested(amountInvested)
            .amountReward(amountInvested * 1.5)
            .fixedTermDays(FixedTermDays.MENSUAL)
            .startDate(LocalDate.now().minusDays(10))
            .endDate(endDate)
            .fixedTermState(FixedTermState.IN_PROGRESS)
            .wallet(wallet)
            .build();
    }

    public static FixedTermDeposit fixedTermDepositFinished(Long id, Double amountReward) {
        Wallet wallet = defaultWallet();
        return FixedTermDeposit.builder()
            .id(id)
            .amountInvested(DEFAULT_AMOUNT)
            .amountReward(amountReward)
            .fixedTermDays(FixedTermDays.MENSUAL)
            .startDate(LocalDate.now().minusDays(30))
            .endDate(LocalDate.now().minusDays(1))
            .fixedTermState(FixedTermState.FINISHED)
            .wallet(wallet)
            .build();
    }

    public static FixedTermDeposit defaultFixedTermDeposit() {
        return fixedTermDeposit(DEFAULT_FIXED_TERM_DEPOSIT_ID, DEFAULT_AMOUNT, DEFAULT_AMOUNT * 1.5,
                FixedTermDays.MENSUAL, FixedTermState.IN_PROGRESS, defaultWallet());
    }

    // Builders para SavingAccount
    public static SavingAccount.SavingAccountBuilder savingAccountBuilder(Long id, String name,
            Double initialAmount, Double currentAmount, Double accumulatedInterest, Wallet wallet) {
        return SavingAccount.builder()
            .id(id)
            .name(name)
            .initialAmount(initialAmount)
            .currentAmount(currentAmount)
            .accumulatedInterest(accumulatedInterest)
            .startDate(LocalDate.now())
            .lastUpdate(LocalDate.now())
            .wallet(wallet)
            .deletedAt(null);
    }

    public static SavingAccount savingAccount(Long id, String name, Double initialAmount,
            Double currentAmount, Double accumulatedInterest, Wallet wallet) {
        return savingAccountBuilder(id, name, initialAmount, currentAmount, accumulatedInterest, wallet).build();
    }

    public static SavingAccount savingAccountWithBalance(Long id, String name, Double currentAmount) {
        return savingAccountBuilder(id, name, DEFAULT_INITIAL_AMOUNT, currentAmount, DEFAULT_INTEREST, defaultWallet())
            .build();
    }

    public static SavingAccount savingAccountDeleted(Long id, LocalDateTime deletedAt) {
        return SavingAccount.builder()
            .id(id)
            .name(DEFAULT_SAVING_ACCOUNT_NAME)
            .initialAmount(DEFAULT_INITIAL_AMOUNT)
            .currentAmount(0.0)
            .accumulatedInterest(DEFAULT_INTEREST)
            .startDate(LocalDate.now().minusDays(30))
            .lastUpdate(LocalDate.now())
            .wallet(defaultWallet())
            .deletedAt(deletedAt)
            .build();
    }

    public static SavingAccount defaultSavingAccount() {
        return savingAccount(DEFAULT_SAVING_ACCOUNT_ID, DEFAULT_SAVING_ACCOUNT_NAME, DEFAULT_INITIAL_AMOUNT,
                DEFAULT_CURRENT_AMOUNT, DEFAULT_INTEREST, defaultWallet());
    }

    // Builders para Stock
    public static Stock.StockBuilder stockBuilder(Long id, String name, String abbreviation,
            Double initialPrice, Double currentPrice, BigInteger totalAmount,
            BigInteger availableAmount, BigInteger soldAmount, RiskLevel riskLevel) {
        return Stock.builder()
            .id(id)
            .name(name)
            .abbreviation(abbreviation)
            .initialPrice(initialPrice)
            .currentPrice(currentPrice)
            .totalAmount(totalAmount)
            .availableAmount(availableAmount)
            .soldAmount(soldAmount)
            .riskLevel(riskLevel);
    }

    public static Stock stock(Long id, String name, String abbreviation, Double initialPrice,
            Double currentPrice, BigInteger totalAmount, BigInteger availableAmount,
            BigInteger soldAmount, RiskLevel riskLevel) {
        return stockBuilder(id, name, abbreviation, initialPrice, currentPrice, totalAmount,
                availableAmount, soldAmount, riskLevel).build();
    }

    public static Stock stockWithPrice(Long id, Double currentPrice, Double initialPrice) {
        return stockBuilder(id, DEFAULT_STOCK_NAME, DEFAULT_STOCK_ABBREVIATION, initialPrice, currentPrice,
                DEFAULT_TOTAL_AMOUNT, DEFAULT_AVAILABLE_AMOUNT, DEFAULT_SOLD_AMOUNT, RiskLevel.MEDIO).build();
    }

    public static Stock stockWithAvailability(Long id, BigInteger availableAmount, BigInteger soldAmount) {
        return stockBuilder(id, DEFAULT_STOCK_NAME, DEFAULT_STOCK_ABBREVIATION, DEFAULT_INITIAL_PRICE,
                DEFAULT_CURRENT_PRICE, DEFAULT_TOTAL_AMOUNT, availableAmount, soldAmount, RiskLevel.MEDIO).build();
    }

    public static Stock defaultStock() {
        return stock(DEFAULT_STOCK_ID, DEFAULT_STOCK_NAME, DEFAULT_STOCK_ABBREVIATION, DEFAULT_INITIAL_PRICE,
                DEFAULT_CURRENT_PRICE, DEFAULT_TOTAL_AMOUNT, DEFAULT_AVAILABLE_AMOUNT, DEFAULT_SOLD_AMOUNT,
                RiskLevel.MEDIO);
    }

    // Builders para Order
    public static Order.OrderBuilder orderBuilder(Long id, Stock stock, Wallet wallet,
            OrderType orderType, OrderState orderState, BigInteger quantity, Double pricePerUnit, OrderStop orderStop) {
        return Order.builder()
            .id(id)
            .stock(stock)
            .wallet(wallet)
            .orderType(orderType)
            .orderState(orderState)
            .quantity(quantity)
            .pricePerUnit(pricePerUnit)
            .orderStop(orderStop)
            .createdAt(LocalDateTime.now());
    }

    public static Order order(Long id, Stock stock, Wallet wallet, OrderType orderType,
            OrderState orderState, BigInteger quantity, Double pricePerUnit, OrderStop orderStop) {
        return orderBuilder(id, stock, wallet, orderType, orderState, quantity, pricePerUnit, orderStop).build();
    }

    public static Order orderExecuted(Long id, OrderType orderType, BigInteger quantity) {
        return orderBuilder(id, defaultStock(), defaultWallet(), orderType, OrderState.EJECUTADA, quantity,
                DEFAULT_CURRENT_PRICE, null).build();
    }

    public static Order orderPending(Long id, OrderStop orderStop, Double pricePerUnit) {
        return orderBuilder(id, defaultStock(), defaultWallet(), OrderType.VENTA, OrderState.PENDIENTE,
                DEFAULT_QUANTITY, pricePerUnit, orderStop).build();
    }

    public static Order defaultOrder() {
        return order(DEFAULT_ORDER_ID, defaultStock(), defaultWallet(), OrderType.COMPRA, OrderState.EJECUTADA,
                DEFAULT_QUANTITY, DEFAULT_CURRENT_PRICE, null);
    }

    // Builders para StockHistory
    public static StockHistory.StockHistoryBuilder stockHistoryBuilder(Long id, Stock stock, Double price,
            Double variation, BigInteger soldAmount, BigInteger availableAmount, LocalDateTime createdAt) {
        return StockHistory.builder()
            .id(id)
            .stock(stock)
            .price(price)
            .variation(variation)
            .soldAmount(soldAmount)
            .availableAmount(availableAmount)
            .createdAt(createdAt);
    }

    public static StockHistory stockHistory(Long id, Stock stock, Double price, Double variation,
            BigInteger soldAmount, BigInteger availableAmount, LocalDateTime createdAt) {
        return stockHistoryBuilder(id, stock, price, variation, soldAmount, availableAmount, createdAt).build();
    }

    public static StockHistory stockHistoryWithVariation(Long id, Double variation, Double price) {
        return stockHistoryBuilder(id, defaultStock(), price, variation, DEFAULT_SOLD_AMOUNT,
                DEFAULT_AVAILABLE_AMOUNT, LocalDateTime.now()).build();
    }

    public static StockHistory defaultStockHistory() {
        return stockHistory(DEFAULT_STOCK_HISTORY_ID, defaultStock(), DEFAULT_CURRENT_PRICE, 0.1,
                DEFAULT_SOLD_AMOUNT, DEFAULT_AVAILABLE_AMOUNT, LocalDateTime.now());
    }

    // Builders para DTOs Request
    public static FixedTermDepositRegisterRequestDto fixedTermDepositRegisterRequestDto(Double amountInvested,
            FixedTermDays fixedTermDays) {
        return FixedTermDepositRegisterRequestDto.builder()
            .amountInvested(amountInvested)
            .fixedTermDays(fixedTermDays)
            .build();
    }

    public static SavingAccountRegisterRequestDto savingAccountRegisterRequestDto(String name, Double initialAmount) {
        return SavingAccountRegisterRequestDto.builder()
            .name(name)
            .initialAmount(initialAmount)
            .build();
    }

    public static StockBuyRequestDto stockBuyRequestDto(Long stockId, BigInteger quantity) {
        return StockBuyRequestDto.builder()
            .stockId(stockId)
            .quantity(quantity)
            .build();
    }

    public static SavingAccountDepositRequestDto savingAccountDepositRequestDto(Long id, Double amount) {
        return SavingAccountDepositRequestDto.builder()
            .id(id)
            .amount(amount)
            .build();
    }

    public static SavingAccountWithdrawalRequestDto savingAccountWithdrawalRequestDto(Long id, Double amount) {
        return SavingAccountWithdrawalRequestDto.builder()
            .id(id)
            .amount(amount)
            .build();
    }

    public static StockRegisterRequestDto stockRegisterRequestDto(String name, String abbreviation,
            Double initialPrice, BigInteger totalAmount, RiskLevel riskLevel) {
        return StockRegisterRequestDto.builder()
            .name(name)
            .abbreviation(abbreviation)
            .initialPrice(initialPrice)
            .totalAmount(totalAmount)
            .riskLevel(riskLevel)
            .build();
    }

    public static StockOrderStopRequestDto stockOrderStopRequestDto(Long stockId, BigInteger quantity,
            Double pricePerUnit, OrderStop orderStop) {
        return StockOrderStopRequestDto.builder()
            .stockId(stockId)
            .quantity(quantity)
            .pricePerUnit(pricePerUnit)
            .orderStop(orderStop)
            .build();
    }

    // Builders para DTOs Response
    public static FixedTermDepositResponseDto fixedTermDepositResponseDto(Long id, Double amountInvested,
            Double amountReward, FixedTermDays fixedTermDays, FixedTermState state) {
        return FixedTermDepositResponseDto.builder()
            .id(id)
            .amountInvested(amountInvested)
            .amountReward(amountReward)
            .fixedTermDays(fixedTermDays)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(fixedTermDays.getValor()))
            .fixedTermState(state)
            .build();
    }

    public static SavingAccountResponseDto savingAccountResponseDto(Long id, String name, Double initialAmount,
            Double currentAmount, Double accumulatedInterest) {
        return SavingAccountResponseDto.builder()
            .id(id)
            .name(name)
            .initialAmount(initialAmount)
            .currentAmount(currentAmount)
            .accumulatedInterest(accumulatedInterest)
            .startDate(LocalDate.now())
            .lastUpdate(LocalDate.now())
            .build();
    }

    public static StockResponseDto stockResponseDto(Long id, String name, String abbreviation,
            Double currentPrice, BigInteger quantityBought, List<StockSellResponseDto> pendingOrders) {
        return StockResponseDto.builder()
            .id(id)
            .name(name)
            .abbreviation(abbreviation)
            .currentPrice(currentPrice)
            .initialPrice(DEFAULT_INITIAL_PRICE)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .availableAmount(DEFAULT_AVAILABLE_AMOUNT)
            .soldAmount(DEFAULT_SOLD_AMOUNT)
            .riskLevel(RiskLevel.MEDIO)
            .quantityBought(quantityBought)
            .pendingOrders(pendingOrders != null ? pendingOrders : new ArrayList<>())
            .build();
    }

    public static StockBuyResponseDto stockBuyResponseDto(Long id, Double pricePerUnit, BigInteger quantity,
            Double total) {
        return StockBuyResponseDto.builder()
            .id(id)
            .pricePerUnit(pricePerUnit)
            .quantity(quantity.intValue())
            .total(total)
            .createdAt(LocalDateTime.now().toString())
            .build();
    }

    public static StockSellResponseDto stockSellResponseDto(BigInteger quantity, Double pricePerUnit, Double total) {
        return StockSellResponseDto.builder()
            .quantity(quantity)
            .pricePerUnit(pricePerUnit)
            .total(total)
            .build();
    }

    public static StockHistoryResponseDto stockHistoryResponseDto(Long id, Double price, Double variation,
            LocalDateTime createdAt) {
        return StockHistoryResponseDto.builder()
            .id(id)
            .price(price)
            .variation(variation)
            .soldAmount(DEFAULT_SOLD_AMOUNT)
            .availableAmount(DEFAULT_AVAILABLE_AMOUNT)
            .timestamp(createdAt.toString())
            .createdAt(createdAt)
            .build();
    }

    public static CandleStickChartValueResponseDto candleStickChartValueResponseDto(LocalDate date, Double open,
            Double close, Double high, Double low) {
        return CandleStickChartValueResponseDto.builder()
            .date(date.atStartOfDay())
            .open(open)
            .close(close)
            .high(high)
            .low(low)
            .build();
    }

    // Helpers para entidades relacionadas
    public static Wallet wallet(Long id, Double balance, Double invertedBalance) {
        return Wallet.builder()
            .id(id)
            .balance(balance)
            .invertedBalance(invertedBalance)
            .build();
    }

    public static Wallet defaultWallet() {
        return wallet(DEFAULT_WALLET_ID, AMOUNT_LARGE, AMOUNT_MEDIUM);
    }

    public static Wallet walletWithInsufficientBalance(Long id, Double balance, Double amount) {
        return wallet(id, balance, AMOUNT_MEDIUM);
    }

    public static Student student(Long id, String email, Wallet wallet) {
        User studentUser = user(email, trinity.play2learn.backend.user.models.Role.ROLE_STUDENT);
        return Student.builder()
            .id(id)
            .name("Test")
            .lastname("Student")
            .dni("12345678")
            .user(studentUser)
            .wallet(wallet)
            .build();
    }

    public static Student defaultStudent() {
        return student(DEFAULT_STUDENT_ID, DEFAULT_STUDENT_EMAIL, defaultWallet());
    }

    public static User user(String email, Role role) {
        return User.builder()
            .email(email)
            .role(role)
            .build();
    }

    public static User defaultUser() {
        return user(DEFAULT_STUDENT_EMAIL, Role.ROLE_STUDENT);
    }

    // Helpers para casos especiales
    public static Stock stockWithInsufficientAvailability(Long id, BigInteger availableAmount, BigInteger quantity) {
        return stockWithAvailability(id, availableAmount, DEFAULT_SOLD_AMOUNT);
    }

    public static SavingAccount savingAccountWithInsufficientBalance(Long id, Double currentAmount, Double amount) {
        return savingAccountWithBalance(id, DEFAULT_SAVING_ACCOUNT_NAME, currentAmount);
    }

    public static List<FixedTermDeposit> fixedTermDepositList(List<FixedTermDeposit> deposits) {
        return deposits != null ? deposits : new ArrayList<>();
    }

    public static List<StockHistory> stockHistoryList(List<StockHistory> histories) {
        return histories != null ? histories : new ArrayList<>();
    }

    // Helpers default para DTOs Response
    public static FixedTermDepositResponseDto defaultFixedTermDepositResponseDto() {
        return fixedTermDepositResponseDto(DEFAULT_FIXED_TERM_DEPOSIT_ID, AMOUNT_MEDIUM,
                AMOUNT_MEDIUM + INTEREST_MEDIUM, FixedTermDays.MENSUAL, FixedTermState.IN_PROGRESS);
    }

    public static SavingAccountResponseDto defaultSavingAccountResponseDto() {
        return savingAccountResponseDto(DEFAULT_SAVING_ACCOUNT_ID, DEFAULT_SAVING_ACCOUNT_NAME,
                AMOUNT_MEDIUM, AMOUNT_MEDIUM, INTEREST_ACCUMULATED);
    }

    public static StockResponseDto defaultStockResponseDto() {
        return stockResponseDto(DEFAULT_STOCK_ID, DEFAULT_STOCK_NAME, DEFAULT_STOCK_ABBREVIATION,
                DEFAULT_CURRENT_PRICE, DEFAULT_QUANTITY, new ArrayList<>());
    }
}

