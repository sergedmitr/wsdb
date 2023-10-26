package ru.sergdm.wsdb.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sergdm.wsdb.client.model.Account;

@FeignClient(name = "PaymentClient", url="${PAYMENTS_URL}:8010")
public interface PaymentClient {
	@RequestMapping(method = RequestMethod.POST, value = "/accounts", consumes = "application/json")
	Account addAccount(Account account);
}
