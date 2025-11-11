%%{init: {'version': '11.12.1'}}%%
erDiagram
    TRANSACTION {
        Long id
        Double amount
        String description
        TransactionActor origin
        TransactionActor destination
        Long wallet_id
        Long subject_id
        Long activity_id
        Long benefit_id
        LocalDateTime createdAt
        Long order_id
        Long fixed_term_deposit_id
        Long saving_account_id
        Long reserve_id
    }
