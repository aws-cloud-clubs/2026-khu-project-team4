package love_cupid_crew.khunghap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class KhunghapApplication {

	public static void main(String[] args) {
		SpringApplication.run(KhunghapApplication.class, args);
	}

}
