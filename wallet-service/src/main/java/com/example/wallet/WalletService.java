package com.example.wallet;

import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    @Autowired
    walletRepository walletRep;

    Wallet findByCustId(Long custId) {
        return walletRep.findByCustId(custId);
    }

    Wallet save(Wallet wallet) {
        return walletRep.save(wallet);
    }
}
