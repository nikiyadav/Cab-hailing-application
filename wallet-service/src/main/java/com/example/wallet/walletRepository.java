package com.example.wallet;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface walletRepository extends JpaRepository<Wallet, Long> {
    // Using pessimistic lock to lock the table. 
    // It guaranteed that table is locked immediately when a transaction begins.
    // and transaction releases the lock either by committing or rolling back the transaction
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Wallet findByCustId(Long custId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Wallet save(Wallet wallet);
}
