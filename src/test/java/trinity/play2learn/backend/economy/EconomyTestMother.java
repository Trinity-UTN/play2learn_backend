package trinity.play2learn.backend.economy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.economy.transaction.dtos.TransactionStatisticsResponseDto;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletResponseDto;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EconomyTestMother {

    // Constantes de configuración
    private static final Double DEFAULT_BALANCE = 1000.0;
    private static final Double DEFAULT_INVERTED_BALANCE = 500.0;
    private static final Double DEFAULT_AMOUNT = 100.0;
    public static final Double DEFAULT_RESERVE_BALANCE = 5000.0;
    public static final Double DEFAULT_CIRCULATION_BALANCE = 10000.0;
    private static final Double DEFAULT_INITIAL_BALANCE = 15000.0;

    // Constantes de prueba comunes
    public static final Long DEFAULT_WALLET_ID = 1001L;
    public static final Long DEFAULT_TRANSACTION_ID = 2001L;
    public static final Long DEFAULT_RESERVE_ID = 3001L;
    public static final Long DEFAULT_STUDENT_ID = 401L;
    public static final Long DEFAULT_SUBJECT_ID = 201L;
    public static final Long DEFAULT_ACTIVITY_ID = 501L;
    public static final Long DEFAULT_BENEFIT_ID = 601L;
    public static final Long DEFAULT_ORDER_ID = 701L;
    public static final Long DEFAULT_FIXED_TERM_DEPOSIT_ID = 801L;
    public static final Long DEFAULT_SAVING_ACCOUNT_ID = 901L;

    public static final String DEFAULT_STUDENT_EMAIL = "student@example.com";
    public static final Double DEFAULT_SUBJECT_INITIAL_BALANCE = 10000.0;
    public static final Double DEFAULT_SUBJECT_ACTUAL_BALANCE = 8000.0;
    public static final Double DEFAULT_ACTIVITY_BALANCE = 2000.0;
    public static final Double DEFAULT_SAVING_ACCOUNT_AMOUNT = 3000.0;

    // Constantes de montos comunes para tests
    public static final Double AMOUNT_LARGE = 1000.0;
    public static final Double AMOUNT_MEDIUM = 100.0;
    public static final Double AMOUNT_SMALL = 50.0;
    public static final Double AMOUNT_MINIMUM = 0.01;
    public static final Double AMOUNT_ZERO = 0.0;
    public static final Double AMOUNT_NEGATIVE = -100.0;

    // Builders para Wallet
    public static Wallet.WalletBuilder walletBuilder(Long id, Double balance, Double invertedBalance) {
        return Wallet.builder()
            .id(id)
            .balance(balance)
            .invertedBalance(invertedBalance);
    }

    public static Wallet wallet(Long id, Double balance, Double invertedBalance) {
        return walletBuilder(id, balance, invertedBalance).build();
    }

    public static Wallet walletWithBalance(Long id, Double balance) {
        return walletBuilder(id, balance, DEFAULT_INVERTED_BALANCE).build();
    }

    public static Wallet defaultWallet() {
        return wallet(DEFAULT_WALLET_ID, DEFAULT_BALANCE, DEFAULT_INVERTED_BALANCE);
    }

    public static Wallet walletWithInsufficientBalance(Long id, Double balance, Double amount) {
        return wallet(id, balance, DEFAULT_INVERTED_BALANCE);
    }

    // Builders para Transaction
    public static Transaction.TransactionBuilder transactionBuilder(
        Long id,
        Double amount,
        String description,
        TypeTransaction type,
        Wallet wallet,
        Reserve reserve
    ) {
        TransactionActor origin = type == TypeTransaction.ASIGNACION || type == TypeTransaction.RECOMPENSA 
            ? TransactionActor.SISTEMA 
            : TransactionActor.ESTUDIANTE;
        TransactionActor destination = type == TypeTransaction.ASIGNACION || type == TypeTransaction.RECOMPENSA 
            ? TransactionActor.ESTUDIANTE 
            : TransactionActor.SISTEMA;

        return Transaction.builder()
            .id(id)
            .amount(amount)
            .description(description)
            .origin(origin)
            .destination(destination)
            .wallet(wallet)
            .reserve(reserve)
            .createdAt(LocalDateTime.now());
    }

    public static Transaction transaction(Long id, Double amount, String description, TypeTransaction type, Wallet wallet, Reserve reserve) {
        return transactionBuilder(id, amount, description, type, wallet, reserve).build();
    }

    public static Transaction transactionWithDescription(Long id, String description, Double amount) {
        return transactionBuilder(
            id,
            amount,
            description,
            TypeTransaction.COMPRA,
            defaultWallet(),
            defaultReserve()
        ).build();
    }

    public static Transaction defaultTransaction() {
        return transaction(
            DEFAULT_TRANSACTION_ID,
            DEFAULT_AMOUNT,
            "Compra de beneficio",
            TypeTransaction.COMPRA,
            defaultWallet(),
            defaultReserve()
        );
    }

    public static List<Transaction> transactionList(List<String> descriptions, List<Double> amounts) {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < descriptions.size(); i++) {
            transactions.add(transactionWithDescription(DEFAULT_TRANSACTION_ID + i, descriptions.get(i), amounts.get(i)));
        }
        return transactions;
    }

    // Builders para Reserve
    public static Reserve.ReserveBuilder reserveBuilder(
        Long id,
        Double reserveBalance,
        Double circulationBalance,
        Double initialBalance
    ) {
        return Reserve.builder()
            .id(id)
            .reserveBalance(reserveBalance)
            .circulationBalance(circulationBalance)
            .initialBalance(initialBalance)
            .transactions(new ArrayList<>());
    }

    public static Reserve reserve(Long id, Double reserveBalance, Double circulationBalance, Double initialBalance) {
        return reserveBuilder(id, reserveBalance, circulationBalance, initialBalance).build();
    }

    public static Reserve reserveWithBalances(Long id, Double reserveBalance, Double circulationBalance) {
        return reserve(id, reserveBalance, circulationBalance, DEFAULT_INITIAL_BALANCE);
    }

    public static Reserve defaultReserve() {
        return reserve(DEFAULT_RESERVE_ID, DEFAULT_RESERVE_BALANCE, DEFAULT_CIRCULATION_BALANCE, DEFAULT_INITIAL_BALANCE);
    }

    public static Reserve reserveWithInsufficientBalance(Long id, Double reserveBalance, Double amount) {
        return reserveWithBalances(id, reserveBalance, DEFAULT_CIRCULATION_BALANCE);
    }

    // Builders para DTOs
    public static WalletResponseDto.WalletResponseDtoBuilder walletResponseDtoBuilder(
        Long id,
        Double balance,
        Double invertedBalance,
        Double totalBalance
    ) {
        return WalletResponseDto.builder()
            .id(id)
            .balance(balance)
            .invertedBalance(invertedBalance)
            .totalBalance(totalBalance);
    }

    public static WalletResponseDto walletResponseDto(Long id, Double balance, Double invertedBalance, Double totalBalance) {
        return walletResponseDtoBuilder(id, balance, invertedBalance, totalBalance).build();
    }

    public static WalletCompleteResponseDto.WalletCompleteResponseDtoBuilder walletCompleteResponseDtoBuilder(
        Long id,
        Double balance,
        Double invertedBalance,
        Double totalBalance,
        List<TransactionResponseDto> transactions
    ) {
        return WalletCompleteResponseDto.builder()
            .id(id)
            .balance(balance)
            .invertedBalance(invertedBalance)
            .totalBalance(totalBalance)
            .transactions(transactions);
    }

    public static WalletCompleteResponseDto walletCompleteResponseDto(
        Long id,
        Double balance,
        Double invertedBalance,
        Double totalBalance,
        List<TransactionResponseDto> transactions
    ) {
        return walletCompleteResponseDtoBuilder(id, balance, invertedBalance, totalBalance, transactions).build();
    }

    public static TransactionResponseDto.TransactionResponseDtoBuilder transactionResponseDtoBuilder(
        Double amount,
        LocalDateTime createdAt,
        String description,
        String type
    ) {
        return TransactionResponseDto.builder()
            .amount(amount)
            .createdAt(createdAt)
            .description(description)
            .type(type);
    }

    public static TransactionResponseDto transactionResponseDto(Double amount, LocalDateTime createdAt, String description, String type) {
        return transactionResponseDtoBuilder(amount, createdAt, description, type).build();
    }

    public static TransactionStatisticsResponseDto.TransactionStatisticsResponseDtoBuilder transactionStatisticsResponseDtoBuilder(
        LocalDateTime date,
        Double total,
        Double totalCirculation,
        Double totalReserve,
        Double totalSubject,
        Double totalStudent,
        Double totalActivity
    ) {
        return TransactionStatisticsResponseDto.builder()
            .date(date)
            .total(total)
            .totalCirculation(totalCirculation)
            .totalReserve(totalReserve)
            .totalSubject(totalSubject)
            .totalStudent(totalStudent)
            .totalActivity(totalActivity);
    }

    public static TransactionStatisticsResponseDto transactionStatisticsResponseDto(
        LocalDateTime date,
        Double total,
        Double totalCirculation,
        Double totalReserve,
        Double totalSubject,
        Double totalStudent,
        Double totalActivity
    ) {
        return transactionStatisticsResponseDtoBuilder(date, total, totalCirculation, totalReserve, totalSubject, totalStudent, totalActivity).build();
    }

    // Helpers para entidades relacionadas
    public static Student student(Long id, String email) {
        Wallet wallet = walletWithBalance(DEFAULT_WALLET_ID, DEFAULT_BALANCE);
        User user = studentUser(email);
        Student student = Student.builder()
            .id(id)
            .name("Ana")
            .lastname("Gómez")
            .dni("87654321")
            .user(user)
            .wallet(wallet)
            .profile(Profile.builder()
                .id(900L + id)
                .build())
            .build();
        wallet.setStudent(student);
        return student;
    }

    public static User studentUser(String email) {
        return User.builder()
            .id(700L)
            .email(email)
            .password("hashed")
            .role(Role.ROLE_STUDENT)
            .build();
    }

    public static Subject subject(Long id, Double initialBalance, Double actualBalance) {
        return Subject.builder()
            .id(id)
            .name("Matemática")
            .initialBalance(initialBalance)
            .actualBalance(actualBalance)
            .optional(false)
            .students(new ArrayList<>())
            .build();
    }

    public static Subject defaultSubject() {
        return subject(DEFAULT_SUBJECT_ID, DEFAULT_SUBJECT_INITIAL_BALANCE, DEFAULT_SUBJECT_ACTUAL_BALANCE);
    }

    public static Activity activity(Long id, Double actualBalance) {
        Preguntados preguntados = Preguntados.builder()
            .id(id)
            .actualBalance(actualBalance)
            .build();
        preguntados.setId(id);
        preguntados.setActualBalance(actualBalance);
        return preguntados;
    }

    public static Activity defaultActivity() {
        return activity(DEFAULT_ACTIVITY_ID, DEFAULT_ACTIVITY_BALANCE);
    }

    public static Benefit benefit(Long id) {
        return Benefit.builder()
            .id(id)
            .name("Beneficio de prueba")
            .build();
    }

    public static SavingAccount savingAccount(Long id, Double currentAmount) {
        return SavingAccount.builder()
            .id(id)
            .currentAmount(currentAmount)
            .build();
    }

    public static SavingAccount defaultSavingAccount() {
        return savingAccount(DEFAULT_SAVING_ACCOUNT_ID, DEFAULT_SAVING_ACCOUNT_AMOUNT);
    }

    public static FixedTermDeposit fixedTermDeposit(Long id, FixedTermState state) {
        return FixedTermDeposit.builder()
            .id(id)
            .fixedTermState(state)
            .build();
    }

    public static FixedTermDeposit fixedTermDepositInProgress() {
        return fixedTermDeposit(DEFAULT_FIXED_TERM_DEPOSIT_ID, FixedTermState.IN_PROGRESS);
    }

    public static FixedTermDeposit fixedTermDepositFinished() {
        return fixedTermDeposit(DEFAULT_FIXED_TERM_DEPOSIT_ID, FixedTermState.FINISHED);
    }

    public static Order order(Long id, OrderType orderType) {
        return Order.builder()
            .id(id)
            .orderType(orderType)
            .build();
    }

    public static Order orderCompra() {
        return order(DEFAULT_ORDER_ID, OrderType.COMPRA);
    }

    public static Order orderVenta() {
        return order(DEFAULT_ORDER_ID, OrderType.VENTA);
    }
}

