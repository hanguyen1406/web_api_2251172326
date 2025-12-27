package com.product;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.product.model.Customer;
import com.product.model.ERole;
import com.product.model.Order;
import com.product.model.OrderItem;
import com.product.model.Product;
import com.product.model.Role;
import com.product.repository.CustomerRepository;
import com.product.repository.OrderRepository;
import com.product.repository.ProductRepository;
import com.product.repository.RoleRepository;

@SpringBootApplication
public class App implements CommandLineRunner {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Initialize Roles
		if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
			roleRepository.save(new Role(null, ERole.ROLE_USER));
			roleRepository.save(new Role(null, ERole.ROLE_ADMINISTRATOR));
		}

		if (customerRepository.count() == 0) {
			System.out.println("Seeding data...");
			seedData();
		} else {
			System.out.println("Data already exists.");
		}
	}

	private void seedData() {
		Role roleUser = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
		Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMINISTRATOR).orElseThrow();

		// 1. Create 5 Customers
		Customer admin = new Customer("admin@gmail.com", encoder.encode("123456"), "Admin User", "0909000001",
				"123 Admin St", "Hanoi", "10000");
		admin.setRoles(new HashSet<>(Arrays.asList(roleAdmin, roleUser)));

		Customer c1 = new Customer("user1@gmail.com", encoder.encode("123456"), "Nguyen Van A", "0909000002",
				"123 Street A", "Hanoi", "10000");
		c1.setRoles(new HashSet<>(Arrays.asList(roleUser)));

		Customer c2 = new Customer("user2@gmail.com", encoder.encode("123456"), "Tran Thi B", "0909000003",
				"456 Street B", "HCM", "70000");
		c2.setRoles(new HashSet<>(Arrays.asList(roleUser)));

		Customer c3 = new Customer("user3@gmail.com", encoder.encode("123456"), "Le Van C", "0909000004",
				"789 Street C", "Danang", "50000");
		c3.setRoles(new HashSet<>(Arrays.asList(roleUser)));

		Customer c4 = new Customer("user4@gmail.com", encoder.encode("123456"), "Pham Thi D", "0909000005",
				"101 Street D", "Cantho", "90000");
		c4.setRoles(new HashSet<>(Arrays.asList(roleUser)));

		List<Customer> customers = customerRepository.saveAll(Arrays.asList(admin, c1, c2, c3, c4));

		// 2. Create 15 Products
		List<Product> products = Arrays.asList(
				new Product("iPhone 13", "Latest Apple phone", 20000000.0, "Electronics", "Apple", 50, "url1"),
				new Product("Samsung Galaxy S21", "Samsung flagship", 18000000.0, "Electronics", "Samsung", 40, "url2"),
				new Product("Sony WH-1000XM4", "Noise canceling headphones", 6000000.0, "Electronics", "Sony", 30,
						"url3"),
				new Product("MacBook Pro", "Apple laptop", 30000000.0, "Electronics", "Apple", 20, "url4"),
				new Product("Dell XPS 13", "Dell laptop", 28000000.0, "Electronics", "Dell", 25, "url5"),
				new Product("T-Shirt", "Cotton t-shirt", 200000.0, "Clothing", "Uniqlo", 100, "url6"),
				new Product("Jeans", "Blue jeans", 500000.0, "Clothing", "Levi's", 80, "url7"),
				new Product("Sneakers", "Running shoes", 1500000.0, "Clothing", "Nike", 60, "url8"),
				new Product("Jacket", "Winter jacket", 1200000.0, "Clothing", "NorthFace", 40, "url9"),
				new Product("Pizza", "Frozen pizza", 100000.0, "Food", "Dr.Oetker", 200, "url10"),
				new Product("Burger", "Ready to eat burger", 50000.0, "Food", "McDonalds", 150, "url11"),
				new Product("Harry Potter", "Fantasy book", 300000.0, "Books", "Bloomsbury", 50, "url12"),
				new Product("Clean Code", "Programming book", 400000.0, "Books", "Pearson", 30, "url13"),
				new Product("Lego Set", "Building blocks", 2000000.0, "Toys", "Lego", 40, "url14"),
				new Product("Barbie Doll", "Doll for kids", 500000.0, "Toys", "Mattel", 60, "url15"));
		productRepository.saveAll(products);

		// 3. Create 8 Orders
		Random random = new Random();
		String[] statuses = { "pending", "confirmed", "processing", "shipped", "delivered", "cancelled" };
		String[] paymentMethods = { "cod", "card" };

		// We will create orders for user1 and user2 mainly
		Customer[] orderCustomers = { c1, c2, c3 };

		for (int i = 0; i < 8; i++) {
			Customer cust = orderCustomers[i % 3];
			Order order = new Order();
			order.setCustomer(cust);
			order.setOrderNumber("ORD-" + System.currentTimeMillis() + "-" + i);
			order.setStatus(statuses[i % statuses.length]);
			order.setPaymentMethod(paymentMethods[i % paymentMethods.length]);
			order.setPaymentStatus(order.getStatus().equals("delivered") ? "paid" : "unpaid");
			order.setShippingAddress(cust.getAddress());
			order.setShippingFee(30000.0);
			order.setNotes("Note for order " + i);

			// Add items
			double subtotal = 0;
			int itemCount = random.nextInt(3) + 1;
			for (int j = 0; j < itemCount; j++) {
				Product p = products.get(random.nextInt(products.size()));
				int qty = random.nextInt(2) + 1;
				OrderItem item = new OrderItem(p, qty, p.getPrice());
				order.addOrderItem(item);
				subtotal += p.getPrice() * qty;
			}
			order.setSubtotal(subtotal);
			order.setTotal(subtotal + order.getShippingFee());

			orderRepository.save(order);

			// Update stock if order is not cancelled
			// Simple logic for seeding: just reduce stock
			if (!order.getStatus().equals("cancelled")) {
				for (OrderItem item : order.getOrderItems()) {
					Product p = item.getProduct();
					p.setStock(p.getStock() - item.getQuantity());
					productRepository.save(p);
				}
			}
		}
		System.out.println("Seeding completed.");
	}
}
