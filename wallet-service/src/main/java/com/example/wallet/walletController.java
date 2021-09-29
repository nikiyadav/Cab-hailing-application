package com.example.wallet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.Lock;

import java.io.File;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;


@Transactional(isolation = Isolation.REPEATABLE_READ)
@EnableAutoConfiguration
@RestController
public class walletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/")
    public String index() {
        return "Hello wallet";
    }

    @GetMapping("/reset")
    // This method is used to reset the wallet
    public void reset() {
        System.out.println("Resetting customer wallet balance.");
        try {
            File myFile = new File("/mnt/mount-point/IDs.txt");
            Scanner myReader = new Scanner(myFile);
            ArrayList<Long> custIdArray = new ArrayList<Long>();
            ArrayList<Long> amountArray = new ArrayList<Long>();
            Long count = 0L;
            String data = myReader.nextLine(); // reads ****
            // Ignore all the cabIds
            while (myReader.hasNext()) {
                data = myReader.nextLine();
                System.out.println(data);
                if (data.equals("****"))
                    break;
            }
            // Read custIds
            while (myReader.hasNext()) {
                data = myReader.nextLine();
                System.out.println(data);
                if (data.equals("****"))
                    break;
                Long custId = Long.parseLong(data);
                custIdArray.add(custId);
            }
            // Read wallet balances
            while (myReader.hasNext()) {
                data = myReader.nextLine();
                System.out.println(data);
                if (data.equals("****"))
                    break;
                Long amt = Long.parseLong(data);
                amountArray.add(amt);
            }

            myReader.close();
            for (int i = 0; i < custIdArray.size(); i++) {
                Wallet wallet = walletService.findByCustId(custIdArray.get(i));
                System.out.println("adding to wallet of cust " + wallet.getCustId() + " amount " + amountArray.get(i));
                wallet.setAmount(amountArray.get(i));
                walletService.save(wallet);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @GetMapping("/getBalance")
    // This method returns the current wallet balance for the given customer
    public Long getBalance(@RequestParam Long custId) {
        Wallet wallet = walletService.findByCustId(custId);
        // If customer is valid then return the amount
        if (wallet != null) {
            return wallet.getAmount();
        }
        return -1L;
    }

    @GetMapping("/deductAmount")
    // This method deducts the given amount from the account of given customer
    public Boolean deductAmount(@RequestParam Long custId, @RequestParam Long amount) {
        // Checking that negative amount cannot be deducted
        if (amount < 0)
            return false;

        Wallet wallet = walletService.findByCustId(custId);
        Long currentBalance = -1L;
        if (wallet != null) {
            currentBalance = wallet.getAmount();
        }
        // verify balance is greater than equal to amount
        if (currentBalance.compareTo(-1L) != 0 && currentBalance.compareTo(amount) >= 0) {
            // update wallet amount as currentBalance - amount
            Long amt = currentBalance - amount;
            wallet.setAmount(amt);
            walletService.save(wallet);
            return true;
        }
        return false;
    }

    @GetMapping("/addAmount")
    // This method adds the given amount to the account of given customer
    public Boolean addAmount(@RequestParam Long custId, @RequestParam Long amount) {
        // checking that negative amount cannot be added
        if (amount < 0)
            return false;
            
        Wallet wallet = walletService.findByCustId(custId);
        if (wallet != null) {
            wallet.setAmount(wallet.getAmount() + amount);
            walletService.save(wallet);
            return true;
        }
        return false;
    }
}
