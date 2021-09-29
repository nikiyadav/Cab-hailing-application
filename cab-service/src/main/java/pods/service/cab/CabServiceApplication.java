package pods.service.cab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pods.service.cab.model.Cab;
import pods.service.cab.model.CabService;

@EnableTransactionManagement
@SpringBootApplication
public class CabServiceApplication implements CommandLineRunner {

    @Autowired
    private CabService cabService;
    public static void main(String[] args) {
      SpringApplication.run(CabServiceApplication.class, args);
    }

    /* This method reads the input file from the mount point and initializes the db */
    @Override
    public void run(String...args) {
        try {
            File myFile = new File("/mnt/mount-point/IDs.txt");
            Scanner myReader = new Scanner(myFile);
            myReader.nextLine();
            while(myReader.hasNext()) {
                String data = myReader.nextLine();
                System.out.println(data);
                if (data.equals("****"))
                    break;
                Long id = Long.parseLong(data);
                Cab cab = new Cab(id);

                cabService.save(cab);
            }
            myReader.close();
        } catch(Exception e) {
            e.getMessage();
        }
    }
}
