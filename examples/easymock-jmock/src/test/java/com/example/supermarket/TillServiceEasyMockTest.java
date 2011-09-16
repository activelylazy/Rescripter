package com.example.supermarket;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.junit.Test;

public class TillServiceEasyMockTest {

	@Test(expected=UserNotAllowedException.class) public void
	unauthenticated_user_cannot_start_shopping() throws UserNotAllowedException {
		AuthenticationService authenticationService = createMock(AuthenticationService.class);
		Customer customer = new Customer(123, "customer");
		expect(authenticationService.authenticateCustomer(customer)).andReturn(false);
		replay(authenticationService);
		
		TillService tillService = new TillService(authenticationService, null, null);
		tillService.startShopping(customer);
	}
}
