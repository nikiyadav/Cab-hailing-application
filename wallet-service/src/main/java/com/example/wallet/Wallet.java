package com.example.wallet;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Getter
@Setter
@Entity
public class Wallet {
    @Id
    // cutomer ID, primary key
    Long custId;
    // amount in customer wallet
    Long amount;

    public Wallet(Long custId) {
        this.custId = custId;
        this.amount = 0L;
    }

    public Wallet(Long custId, Long amount) {
        this.custId = custId;
        this.amount = amount;
    }

    public Wallet() {};

}
