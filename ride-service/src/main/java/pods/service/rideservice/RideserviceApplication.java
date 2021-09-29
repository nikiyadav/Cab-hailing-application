package pods.service.rideservice;

import java.io.File;
import java.util.Scanner;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pods.service.rideservice.model.Cab;
import pods.service.rideservice.model.CabService;

@EnableTransactionManagement
@SpringBootApplication
public class RideserviceApplication implements CommandLineRunner {

    @Autowired
    private CabService cabService;

    public static void main(String[] args) {
        SpringApplication.run(RideserviceApplication.class, args);
    }
    
    @Override
    /* This method reads the input file from the mount point and initializes the db */
    public void run(String...args) {
        System.out.println("I am running spring boot run application!!!");
        /*try {
            File myFile = new File("/mnt/mount-point/IDs.txt");
            Scanner myReader = new Scanner(myFile);
            myReader.nextLine();
            while (myReader.hasNext()) {
                String data = myReader.nextLine();

                System.out.println(data);
                if (data.equals("****"))
                    break;
                Long id = Long.parseLong(data);
                Cab cab = new Cab(id);

                cabService.save(cab);
            }
        } catch (Exception e) {

            e.getMessage();
        }*/
    
    }
}
