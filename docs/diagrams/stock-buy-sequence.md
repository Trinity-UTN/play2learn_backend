# Diagrama de Secuencia: Compra de Acciones

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor U as Estudiante (ROLE_STUDENT)
    participant JA as JwtSessionAspect
    participant JS as IJwtService
    participant UES as UserExistsByEmailService
    participant UExS as UserExistsService
    participant UR as IUserRepository
    participant C as StockBuyController
    participant SUAR as SessionUserArgumentResolver
    participant UGS as IUserGetByEmailService
    participant User as User
    participant Svc as StockBuyService
    participant stockDto as StockBuyRequestDto
    participant SGES as StudentGetByEmailService
    participant SR as IStudentRepository
    participant ST as Student
    participant Wallet as Wallet
    participant SFind as StockFindByIdService
    participant StockRepo as IStockRepository
    participant Stock as Stock
    participant OrderMapper as OrderMapper
    participant OrderRepo as IOrderRepository
    participant Order as Order
    participant TG as TransactionGenerateService
    participant STR as strategies<Map<String,ITransactionStrategyService>>
    participant STS as StockTransactionService
    participant RFind as ReserveFindLastService
    participant ReserveRepo as IReserveRepository
    participant Reserve as Reserve
    participant TM as TransactionMapper
    participant TR as ITransactionRepository
    participant WRem as WalletRemoveAmountService
    participant WRepo as IWalletRepository
    participant RMod as ReserveModifyService
    participant StockMove as StockMoveService
    participant StockUpdate as StockUpdateSpecificService
    participant SCV as StockCalculateVariationService
    participant SHLast as StockHistoryFindLastService
    participant STrend as StockHistoryCalculateTrendService
    participant SHRepo as IStockHistoryRepository
    participant StockHistory as StockHistory
    participant SHMapper as StockHistoryMapper
    participant OrderStop as OrderStopExecuteService
    participant SCBW as StockCalculateByWalletService
    participant WalletUpdate as WalletUpdateInvestedBalanceService
    participant InvCalc as InvestemtCalculateTotalInvestedService
    participant StockInv as StockCalculateAmountInvestedService
    participant FTInv as FixedTermDepositCalculateAmountInvestedService
    participant SAInv as SavingAccountCalculateAmountInvestedService
    participant SARepo as ISavingAccountRepository
    participant Resp as ResponseFactory
    participant SM as SuccessfulMessages
    participant DB as Base de Datos

    U->>C: POST /investment/stocks/buy<br/>Body: StockBuyRequestDto{stockId, quantity}<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @PostMapping("/buy")<br/>@SessionRequired(roles = {ROLE_STUDENT})
    
    Note over JA: Interceptor ejecutado antes del método
    JA->>JA: validateJwt(joinPoint, sessionRequired)
    JA->>JA: RequestContextHolder.getRequestAttributes()
    JA->>JA: attrs.getRequest()
    JA->>JA: request.getHeader("Authorization")
    alt Header ausente o no Bearer
        JA-->>U: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Header válido
        JA->>JA: jwt = authHeader.substring(7)
    end
    JA->>JS: isTokenExpired(jwt)
    alt Token expirado
        JS-->>JA: JwtException
        JA-->>U: 401 Unauthorized<br/>(TOKEN_EXPIRED)
    else Vigente
        JS-->>JA: false
    end
    JA->>JS: extractUsername(jwt)
    alt Firma inválida
        JS-->>JA: JwtException
        JA-->>U: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Firma válida
        JS-->>JA: email
    end
    JA->>UES: validateIfUserIsActive(email)
    UES->>UExS: validate(email)
    UExS->>UR: existsByEmailAndDeletedAtIsNull(email)
    UR-->>UExS: boolean
    alt Usuario inactivo/no existe
        UExS-->>UES: false
        UES-->>JA: UnauthorizedException(UNAUTHORIZED)
        JA-->>U: 401 Unauthorized
    else Usuario activo
        UExS-->>UES: true
        UES-->>JA: ok
    end
    JA->>JS: extractRole(jwt)
    JS-->>JA: role
    JA->>JA: requiredRoles.contains(role)
    alt Rol no permitido
        JA-->>U: 401 Unauthorized<br/>(requiredRoles)
    else Rol válido
        JA-->>C: validación exitosa
    end

    C->>SUAR: resolveArgument(@SessionUser User, request)
    SUAR->>SUAR: request.getHeader("Authorization")
    alt Header nulo o inválido
        SUAR-->>C: UnauthorizedException(INVALID_ACCESS_TOKEN)
    else Header válido
        SUAR->>SUAR: jwt = authHeader.substring(7)
    end
    SUAR->>JS: extractUsername(jwt)
    alt Error al parsear JWT
        JS-->>SUAR: Exception
        SUAR-->>C: UnauthorizedException(INVALID_ACCESS_TOKEN)
    else Username obtenido
        JS-->>SUAR: email
    end
    SUAR->>UGS: findUserByEmail(email)
    UGS->>UR: findByEmailAndDeletedAtIsNull(email)
    UR-->>UGS: Optional<User>
    alt Usuario no encontrado
        UGS-->>SUAR: UnauthorizedException(UNAUTHORIZED)
        SUAR-->>C: UnauthorizedException(UNAUTHORIZED)
    else Usuario encontrado
        UGS-->>SUAR: User
        SUAR-->>C: User
    end

    C->>Svc: cu84buystocks(stockDto, user)
    Note right of Svc: stockDto.stockId(Long), stockDto.quantity(BigInteger)
    
    Svc->>SGES: getByEmail(user.getEmail())
    SGES->>SR: findByUserEmailAndDeletedAtIsNull(email)
    SR-->>SGES: Optional<Student>
    alt Estudiante no existe
        SGES-->>Svc: NotFoundException
        Svc-->>C: NotFoundException
        C-->>U: 404 Not Found
    else Estudiante encontrado
        SGES-->>Svc: Student
        Svc->>ST: getWallet()
        ST-->>Svc: Wallet
        Svc-->>Wallet: referencia
    end

    Svc->>SFind: execute(stockDto.stockId)
    SFind->>StockRepo: findById(stockId)
    StockRepo-->>SFind: Optional<Stock>
    alt Stock no encontrado
        SFind-->>Svc: NotFoundException("Accion no encontrada")
        Svc-->>C: NotFoundException
        C-->>U: 404 Not Found
    else Stock encontrado
        SFind-->>Svc: Stock
        Svc-->>Stock: referencia
    end

    Svc->>Stock: getAvailableAmount()
    Stock-->>Svc: BigInteger
    Svc->>stockDto: getQuantity()
    stockDto-->>Svc: BigInteger
    alt availableAmount < quantity
        Svc-->>C: BadRequestException("No hay acciones disponibles suficientes para comprar.")
        C-->>U: 400 Bad Request
    end

    Svc->>Wallet: getBalance()
    Wallet-->>Svc: Double
    Svc->>Stock: getCurrentPrice()
    Stock-->>Svc: Double
    Svc->>stockDto: getQuantity()
    stockDto-->>Svc: BigInteger
    alt balance < currentPrice * quantity
        Svc-->>C: BadRequestException("No hay saldo suficiente en el wallet para realizar la compra.")
        C-->>U: 400 Bad Request
    end

    Svc->>OrderMapper: toEntity(OrderType.COMPRA, OrderState.EJECUTADA, stock, wallet, quantity)
    OrderMapper-->>Svc: Order nuevo
    Svc->>OrderRepo: save(order)
    OrderRepo->>DB: INSERT orders (...)
    DB-->>OrderRepo: Order persisted
    OrderRepo-->>Svc: Order
    Svc-->>Order: referencia

    Svc->>TG: generate(TypeTransaction.STOCK,<br/>amount = stock.currentPrice * quantity.doubleValue(),<br/>"Compra de acciones",<br/>TransactionActor.ESTUDIANTE,<br/>TransactionActor.SISTEMA,<br/>wallet,null,null,null,order,null,null)
    TG->>STR: get("STOCK")
    STR-->>TG: StockTransactionService
    TG->>STS: execute(amount,"Compra de acciones",<br/>TransactionActor.ESTUDIANTE, TransactionActor.SISTEMA,<br/>wallet,null,null,null,order,null,null)
    Note right of STS: Estrategia (Strategy Pattern)
    
    STS->>STS: order.getOrderType()
    alt orderType == COMPRA
        STS->>RFind: get()
        RFind->>ReserveRepo: findFirstByOrderByCreatedAtDesc()
        ReserveRepo-->>RFind: Optional<Reserve>
        alt Reserva ausente
            RFind-->>STS: NotFoundException
            STS-->>TG: NotFoundException
            TG-->>Svc: NotFoundException
            Svc-->>C: NotFoundException
            C-->>U: 404 Not Found
        else Reserva encontrada
            RFind-->>STS: Reserve
            STS-->>Reserve: referencia
        end
        alt amount > wallet.balance
            STS-->>TG: BadRequestException("El wallet no cuenta con el balance suficiente para realizar la compra de acciones")
            TG-->>Svc: BadRequestException
            Svc-->>C: BadRequestException
            C-->>U: 400 Bad Request
        else Saldo suficiente
            STS->>TM: toModel(amount,"Compra de acciones",ESTUDIANTE,SISTEMA,wallet,null,null,null,order,null,null,reserve)
            TM-->>STS: Transaction
            STS->>TR: save(transaction)
            TR->>DB: INSERT transactions (...)
            DB-->>TR: Transaction persisted
            TR-->>STS: Transaction
            STS->>WRem: execute(wallet, amount)
            WRem->>WRepo: save(wallet.balance - amount)
            WRepo->>DB: UPDATE wallet SET balance = balance - amount
            DB-->>WRepo: Wallet actualizado
            WRepo-->>WRem: Wallet
            WRem-->>STS: Wallet
            STS->>RMod: moveToReserve(amount, reserve)
            RMod->>ReserveRepo: save(reserve.circulationBalance - amount,<br/>reserveBalance + amount)
            ReserveRepo->>DB: UPDATE reserve ...
            DB-->>ReserveRepo: Reserve actualizado
            ReserveRepo-->>RMod: Reserve
            RMod-->>STS: Reserve
            STS-->>TG: Transaction
        end
    else orderType != COMPRA
        STS-->>TG: Lógica de venta (no aplica)
    end
    TG-->>Svc: Transaction

    Svc->>StockMove: toSold(stock, order.quantity)
    StockMove->>Stock: getAvailableAmount()
    Stock-->>StockMove: BigInteger
    alt availableAmount < quantity
        StockMove-->>Svc: BadRequestException("No hay acciones disponibles suficientes para mover a vendidas.")
        Svc-->>C: BadRequestException
        C-->>U: 400 Bad Request
    else Movimiento válido
        StockMove->>Stock: setAvailableAmount(available - quantity)
        StockMove->>Stock: setSoldAmount(sold + quantity)
        StockMove->>StockRepo: save(stock)
        StockRepo->>DB: UPDATE stock SET available_amount, sold_amount
        DB-->>StockRepo: Stock actualizado
        StockRepo-->>StockMove: Stock
        StockMove-->>Svc: void
    end

    Svc->>StockUpdate: execute(stock)
    StockUpdate->>SCV: execute(stock)
    SCV->>SHLast: execute(stock)
    SHLast->>SHRepo: findTopByStockOrderByCreatedAtDesc(stock)
    SHRepo-->>SHLast: Optional<StockHistory>
    alt Historial no disponible
        SHLast-->>SCV: NotFoundException
        SCV-->>StockUpdate: NotFoundException
        StockUpdate-->>Svc: NotFoundException
        Svc-->>C: NotFoundException
        C-->>U: 404 Not Found
    else Último historial encontrado
        SHLast-->>SCV: StockHistory
    end
    SCV->>Stock: getSoldAmount()
    Stock-->>SCV: BigInteger
    SCV->>StockHistory: getSoldAmount()
    StockHistory-->>SCV: BigInteger
    SCV->>Stock: getTotalAmount()
    Stock-->>SCV: BigInteger
    SCV->>STrend: execute(stock)
    STrend->>SHRepo: findTop10ByStockOrderByCreatedAtAsc(stock)
    SHRepo-->>STrend: List<StockHistory>
    STrend-->>SCV: boolean (tendenciaAlcista)
    SCV-->>StockUpdate: Double variation (%)
    StockUpdate->>Stock: getCurrentPrice()
    Stock-->>StockUpdate: Double
    StockUpdate->>Stock: getInitialPrice()
    Stock-->>StockUpdate: BigInteger
    StockUpdate->>Stock: setCurrentPrice(newPrice limitado)
    StockUpdate->>StockRepo: save(stock)
    StockRepo->>DB: UPDATE stock SET current_price
    DB-->>StockRepo: Stock actualizado
    StockRepo-->>StockUpdate: Stock stockUpdated
    StockUpdate->>SHMapper: toModel(stockUpdated, variation)
    SHMapper-->>StockUpdate: StockHistory nuevo
    StockUpdate->>SHRepo: save(stockHistory)
    SHRepo->>DB: INSERT stock_history (...)
    DB-->>SHRepo: StockHistory persistido
    SHRepo-->>StockUpdate: StockHistory
    StockUpdate->>OrderStop: execute(stockUpdated)

    OrderStop->>OrderRepo: findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE)
    OrderRepo-->>OrderStop: List<Order>
    alt No hay órdenes stop pendientes
        OrderStop-->>StockUpdate: void
    else Existen órdenes pendientes
        loop Por cada Order pendiente
            OrderStop->>SHLast: execute(stock)
            SHLast->>SHRepo: findTopByStockOrderByCreatedAtDesc(stock)
            SHRepo-->>SHLast: Optional<StockHistory>
            SHLast-->>OrderStop: StockHistory actual
            OrderStop->>Order: verificar condiciones LOSS/PROFIT con stockHistory.price
            alt Condición no cumplida
                OrderStop-->>OrderStop: Continuar
            else Condición cumplida
                OrderStop->>SCBW: execute(stock, order.wallet)
                SCBW->>OrderRepo: findByWalletAndStockAndOrderState(order.wallet, stock, OrderState.EJECUTADA)
                OrderRepo-->>SCBW: List<Order>
                SCBW-->>OrderStop: BigInteger cantidadActual
                alt cantidadActual < order.quantity
                    OrderStop->>Order: setOrderState(OrderState.CANCELADA)
                    OrderStop->>OrderRepo: save(order)
                    OrderRepo->>DB: UPDATE orders SET order_state = CANCELADA
                    DB-->>OrderRepo: ok
                    OrderRepo-->>OrderStop: Order
                else cantidad suficiente
                    OrderStop->>Order: setOrderState(OrderState.EJECUTADA)
                    OrderStop->>Order: setPricePerUnit(stockHistory.price)
                    OrderStop->>OrderRepo: save(order)
                    OrderRepo->>DB: UPDATE orders SET order_state = EJECUTADA, price_per_unit = stockHistory.price
                    DB-->>OrderRepo: Order actualizado
                    OrderRepo-->>OrderStop: Order
                    OrderStop->>TG: generate(TypeTransaction.STOCK,<br/>amount = stock.currentPrice * order.quantity.doubleValue(),<br/>"Venta de acciones",<br/>TransactionActor.SISTEMA,<br/>TransactionActor.ESTUDIANTE,<br/>order.wallet,null,null,null,order,null,null)
                    TG->>STR: get("STOCK")
                    STR-->>TG: StockTransactionService
                    TG->>STS: execute(amount,"Venta de acciones",<br/>TransactionActor.SISTEMA, TransactionActor.ESTUDIANTE,<br/>order.wallet,null,null,null,order,null,null)
                    STS-->>TG: Transaction
                    TG-->>OrderStop: Transaction
                    OrderStop->>StockMove: toAvailable(stock, order.quantity)
                    StockMove->>Stock: getSoldAmount()
                    Stock-->>StockMove: BigInteger
                    alt soldAmount < quantity
                        StockMove-->>OrderStop: BadRequestException
                        OrderStop-->>StockUpdate: BadRequestException
                        StockUpdate-->>Svc: BadRequestException
                        Svc-->>C: BadRequestException
                        C-->>U: 400 Bad Request
                    else Movimiento inverso válido
                        StockMove->>Stock: setSoldAmount(sold - quantity)
                        StockMove->>Stock: setAvailableAmount(available + quantity)
                        StockMove->>StockRepo: save(stock)
                        StockRepo->>DB: UPDATE stock SET sold_amount, available_amount
                        DB-->>StockRepo: ok
                        StockRepo-->>StockMove: Stock
                        StockMove-->>OrderStop: void
                    end
                    OrderStop->>WalletUpdate: execute(order.wallet)
                    WalletUpdate->>InvCalc: cu110calculateTotalInvested(order.wallet)
                    InvCalc->>StockInv: execute(order.wallet)
                    StockInv->>StockRepo: findAll()
                    StockRepo-->>StockInv: Iterable<Stock>
                    loop Por cada Stock
                        StockInv->>SCBW: execute(stock, order.wallet)
                        SCBW->>OrderRepo: findByWalletAndStockAndOrderState(order.wallet, stock, OrderState.EJECUTADA)
                        OrderRepo-->>SCBW: List<Order>
                        SCBW-->>StockInv: BigInteger
                    end
                    StockInv-->>InvCalc: Double totalStocks
                    InvCalc->>FTInv: execute(order.wallet)
                    FTInv->>DB: SELECT fixed_term_deposit WHERE state = IN_PROGRESS
                    DB-->>FTInv: List<FixedTermDeposit>
                    FTInv-->>InvCalc: Double totalFixed
                    InvCalc->>SAInv: execute(order.wallet)
                    SAInv->>SARepo: findAllByWalletAndDeletedAtIsNull(order.wallet)
                    SARepo-->>SAInv: List<SavingAccount>
                    SAInv-->>InvCalc: Double totalSaving
                    InvCalc-->>WalletUpdate: Double totalInvested
                    WalletUpdate->>order.wallet: setInvertedBalance(totalInvested)
                    WalletUpdate->>WRepo: save(order.wallet)
                    WRepo->>DB: UPDATE wallet SET inverted_balance = totalInvested
                    DB-->>WRepo: Wallet actualizado
                    WRepo-->>WalletUpdate: Wallet
                    WalletUpdate-->>OrderStop: void
                end
            end
        end
        OrderStop-->>StockUpdate: void
    end
    StockUpdate-->>Svc: void

    Svc->>WalletUpdate: execute(wallet)
    WalletUpdate->>InvCalc: cu110calculateTotalInvested(wallet)
    InvCalc->>StockInv: execute(wallet)
    StockInv->>StockRepo: findAll()
    StockRepo-->>StockInv: Iterable<Stock>
    loop Por cada Stock
        StockInv->>SCBW: execute(stock, wallet)
        SCBW->>OrderRepo: findByWalletAndStockAndOrderState(wallet, stock, OrderState.EJECUTADA)
        OrderRepo-->>SCBW: List<Order>
        SCBW-->>StockInv: BigInteger
    end
    StockInv-->>InvCalc: Double totalStocks
    InvCalc->>FTInv: execute(wallet)
    FTInv->>DB: SELECT fixed_term_deposit WHERE state = IN_PROGRESS
    DB-->>FTInv: List<FixedTermDeposit>
    FTInv-->>InvCalc: Double totalFixed
    InvCalc->>SAInv: execute(wallet)
    SAInv->>SARepo: findAllByWalletAndDeletedAtIsNull(wallet)
    SARepo-->>SAInv: List<SavingAccount>
    SAInv-->>InvCalc: Double totalSaving
    InvCalc-->>WalletUpdate: Double totalInvested
    WalletUpdate->>wallet: setInvertedBalance(totalInvested)
    WalletUpdate->>WRepo: save(wallet)
    WRepo->>DB: UPDATE wallet SET inverted_balance = totalInvested
    DB-->>WRepo: Wallet actualizado
    WRepo-->>WalletUpdate: Wallet
    WalletUpdate-->>Svc: void

    Svc->>OrderMapper: toBuyDto(order)
    OrderMapper-->>Svc: StockBuyResponseDto
    Svc-->>C: StockBuyResponseDto

    C->>SM: createdSuccessfully("Orden de compra de accion")
    SM-->>C: "Orden de compra de accion creado correctamente"
    C->>Resp: created(StockBuyResponseDto,<br/>"Orden de compra de accion creado correctamente")
    Resp->>Resp: BaseResponse.builder()...
    Resp->>Resp: ResponseEntity.status(201)
    Resp-->>C: ResponseEntity<BaseResponse<StockBuyResponseDto>>
    C-->>U: 201 Created<br/>BaseResponse{data: StockBuyResponseDto, message, errors:null, timestamp}
    
    note over Svc: finCU()
```

