package library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@Theme("my-theme")
public class EntryPoint implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(EntryPoint.class, args);
    }
}
