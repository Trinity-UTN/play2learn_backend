%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    participant FTDS as FixedTermDepositAutomaticEndsService
    participant FTFSS as FixedTermDepositFindAllByStateService
    participant FTDR as IFixedTermDepositRepository
    participant TGS as TransactionGenerateService
    participant FTTS as FixedTermDepositTransactionService
    participant TR as ITransactionRepository
    participant WUBS as WalletUpdateInvestedBalanceService
    participant ICTIS as IInvestmentCalculateTotalInvestedService

    note over FTDS, ICTIS: Flujo automático ejecutado diariamente a las 1:30 AM

    FTDS->>FTFSS: execute(FixedTermState.IN_PROGRESS)
    FTFSS->>FTDR: findByFixedTermState(FixedTermState.IN_PROGRESS)
    FTDR-->>FTFSS: List<FixedTermDeposit>
    FTFSS-->>FTDS: List<FixedTermDeposit>

    loop Para cada FixedTermDeposit
        alt fixedTermDeposit.getEndDate().isAfter(LocalDate.now())
            note over FTDS: Continúa con el siguiente plazo fijo
        else
            FTDS->>FTDS: fixedTermDeposit.setFixedTermState(FixedTermState.FINISHED)
            FTDS->>FTDR: save(fixedTermDeposit)
            FTDR-->>FTDS: FixedTermDeposit

            FTDS->>TGS: generate(TypeTransaction.PLAZO_FIJO, fixedTermDeposit.getAmountReward(), "Inversion en plazo fijo, retorno.", TransactionActor.SISTEMA, TransactionActor.ESTUDIANTE, fixedTermDeposit.getWallet(), null, null, null, null, fixedTermDeposit, null)
            TGS->>TGS: Validar amount > 0
            TGS->>TGS: strategy = strategies.get("PLAZO_FIJO")
            TGS->>FTTS: execute(amount, description, origin, destination, wallet, subject, activity, benefit, order, fixedTermDeposit, savingAccount)

            FTTS->>FTTS: Verificar fixedTermDeposit.getFixedTermState() == FixedTermState.FINISHED
            FTTS->>TR: save(Transaction)
            TR-->>FTTS: Transaction
            FTTS->>FTTS: addAmountWalletService.execute(wallet, amount)
            FTTS->>FTTS: modifyReserveService.moveToCirculation(amount, reserve)

            FTTS-->>TGS: Transaction
            TGS-->>FTDS: Transaction

            FTDS->>WUBS: execute(fixedTermDeposit.getWallet())
            WUBS->>ICTIS: cu110calculateTotalInvested(wallet)
            ICTIS-->>WUBS: Double (total invertido)
            WUBS->>WUBS: wallet.setInvertedBalance(total)
            WUBS->>WUBS: walletRepository.save(wallet)
            WUBS-->>FTDS: void
        end
    end

    note over FTDS: finCU()
