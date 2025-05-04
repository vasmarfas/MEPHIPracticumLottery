package org.mephi_kotlin_band.lottery;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
@Import(JwtTestConfig.class)
class LotteryApplicationTests {

	@Test
	void contextLoads() {
	}
}
