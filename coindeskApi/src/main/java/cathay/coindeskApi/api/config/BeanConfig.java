package cathay.coindeskApi.api.config;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

	@Bean
	public DecimalFormat currencyFormat() {
		return new DecimalFormat("#,##0.0000");
	}
}
