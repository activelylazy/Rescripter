package com.example.supermarket;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class TillServiceTest {

	private Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	
	@Test(expected=UserNotAllowedException.class) public void
	unauthenticated_user_cannot_start_shopping() throws UserNotAllowedException {
		final AuthenticationService authenticationService = context.mock(AuthenticationService.class);
		final Customer customer = new Customer(123, "customer");
		
		context.checking(new Expectations() {{
			oneOf(authenticationService).authenticateCustomer(customer); will(returnValue(false));
		}});
		
		TillService tillService = new TillService(authenticationService, null, null);
		tillService.startShopping(customer);
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	authenticated_user_starts_shopping() throws UserNotAllowedException {
		final AuthenticationService authenticationService = context.mock(AuthenticationService.class);
		final OrderService orderService = context.mock(OrderService.class);
		
		final Customer customer = new Customer(123, "customer");
		
		
		context.checking(new Expectations() {{
			oneOf(authenticationService).authenticateCustomer(customer); will(returnValue(true));
			oneOf(orderService).createOrder(customer);
		}});
		
		TillService tillService = new TillService(authenticationService, null, orderService);
		tillService.startShopping(customer);
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	scanned_item_is_added() throws UserNotAllowedException {
		final AuthenticationService authenticationService = context.mock(AuthenticationService.class);
		final ProductService productService = context.mock(ProductService.class);
		final OrderService orderService = context.mock(OrderService.class);
		final Order order = context.mock(Order.class);
		
		final Customer customer = new Customer(123, "customer");
		final String barcode = "123456";
		final Product product = new Product("1234", "product", new BigDecimal("1.34"));
		
		context.checking(new Expectations() {{
			allowing(authenticationService).authenticateCustomer(with(any(Customer.class)));
				will(returnValue(true));
			allowing(orderService).createOrder(customer); will(returnValue(order));
				
			oneOf(productService).lookupProductByBarcode(barcode);
				will(returnValue(product));
			oneOf(order).addItem(product);
		}});
		
		TillService tillService = new TillService(authenticationService, productService, orderService);
		tillService.startShopping(customer);
		tillService.scanItem(barcode);
		
		context.assertIsSatisfied();
	}
}
