%%{init: {'version': '11.12.1'}}%%
erDiagram
    FIXED_TERM_DEPOSIT {
        Long id
        Double amountInvested
        Double amountReward
        FixedTermDays fixedTermDays
        LocalDate startDate
        LocalDate endDate
        FixedTermState fixedTermState
        Long wallet_id
    }
