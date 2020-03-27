package com.haier.psi;

import com.haier.psi.service.ConfigPropertyPlaceholder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App
{


    public static void main( String[] args )
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPropertyPlaceholder.zookeeperManager.startZk();
            }
        }).start();
        SpringApplication.run(App.class);

    }
}
