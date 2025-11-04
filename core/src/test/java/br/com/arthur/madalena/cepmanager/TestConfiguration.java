package br.com.arthur.madalena.cepmanager;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {
        LiquibaseAutoConfiguration.class,
        MailSenderAutoConfiguration.class
})
@ComponentScan(
        basePackages = "br.com.arthur.madalena.cepmanager",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "br\\.com\\.arthur\\.madalena\\.cepmanager\\.service\\..*ServiceImpl"
        )
)
public class TestConfiguration {
    // Classe de configuração para testes do módulo core
    // Liquibase e MailSender desabilitados para testes
    // EmailServiceImpl excluído do scan pois depende de JavaMailSender
}

