package com.example.wallet;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAutoConfiguration
@SpringBootApplication
public class WalletApplication implements CommandLineRunner {

    @Autowired
    private WalletService walletService;

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

    @Override
    /* This method reads the input file from the mount point and initializes the db */
    public void run(String... args) {
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
            for (int i = 0; i < custIdArray.size(); i++) {
                Wallet wallet1 = new Wallet(custIdArray.get(i), amountArray.get(i));
                walletService.save(wallet1);
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }
}
