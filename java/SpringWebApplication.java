import kz.kdlolymp.termocontainers.entity.Department;
import kz.kdlolymp.termocontainers.service.DepartmentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"kz.kdlolymp.termocontainers"})
@EntityScan("kz.kdlolymp.termocontainers.entity")
@EnableJpaRepositories("kz.kdlolymp.termocontainers.repositories")
public class SpringWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(SpringWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebApplication.class, args);
    }

}
