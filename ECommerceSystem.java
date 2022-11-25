import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	
	ArrayList<Customer> customers = new ArrayList<Customer>();	
	ArrayList<Product>  products = new ArrayList<Product>();
	
	ArrayList<ProductOrder> orders = new ArrayList<ProductOrder>();
	ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	// These variables are used to generate order numbers, customer id's, product id's 
	int orderNumber = 500;
	int customerId = 900;
	int productId = 700;

	// General variable used to store an error message when something is invalid (e.g. customer id does not exist)  
	String errMsg = null;

	// Random number generator
	Random random = new Random();

	public ECommerceSystem() 
	{
		// Read the products txt, and use the lines in the file to create Product objects.
		try{
		
			String line1 = "";
			String line2 = "";
			String line3 = "";
			String line4 = "";
			String line5 = "";
	
			int PaperBackStock;
			int HardcoverStock;
			String title = "";
			String author = "";
			int year;
	
			File current_file = new File("products.txt");
			Scanner in = new Scanner(current_file);
			
			
				while(in.hasNextLine()){ //Loop through the file lines
					line1 = in.nextLine(); //Assigne each line variable to its corresponding line in the file
					line2 = in.nextLine();
					line3 = in.nextLine();
					line4 = in.nextLine();
					line5 = in.nextLine();
	
					if (line1.equals("BOOKS")){ //If the product is a book

						//split the 4th line to get the stock for PaperBack and HardCover
						String[] split_line4 = line4.split(" ");
						PaperBackStock = Integer.parseInt(split_line4[0]);
						HardcoverStock = Integer.parseInt(split_line4[1]);
	
						//splot the 5th line to get the title,author, and year
						String[] split_line5 = line5.split(":");
						title = split_line5[0];
						author = split_line5[1];
						year = Integer.parseInt(split_line5[2]);
	
						//create the product and add it to the products array
						products.add(new Book(line2,generateProductId(),Double.parseDouble(line3),PaperBackStock, HardcoverStock, title, author, year));
	
					}
					else{ //If the product is not a book
						//create the product and add it to the products array
						products.add(new Product(line2,generateProductId(),Double.parseDouble(line3),Integer.parseInt(line4), Product.Category.valueOf(line1)));
	
					}
				}
		
		}
		
		catch(IOException e){
			System.out.println(e.getMessage());
			System.exit(1);
		}

		// Create some customers
		customers.add(new Customer(generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin"));
		customers.add(new Customer(generateCustomerId(),"Prince Humperdinck", "The Castle, Florin"));
		customers.add(new Customer(generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine"));
		customers.add(new Customer(generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach"));
	}
	
	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public String getErrorMessage()
	{
		return errMsg;
	}

	public void printAllProducts()
	{
		for (Product p : products)
			p.print();
	}

	public void printAllBooks()
	{
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
				p.print();
		}
	}

	public ArrayList<Book> booksByAuthor(String author)
	{
		ArrayList<Book> books = new ArrayList<Book>();
		for (Product p : products)
		{
			if (p.getCategory() == Product.Category.BOOKS)
			{
				Book book = (Book) p;
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}
	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public void printOrderHistory(String customerId)
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
			
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		
	}

	public void orderProduct(String productId, String customerId, String productOptions)
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		Customer customer = customers.get(index);

		// Get product 
		index = products.indexOf(new Product(productId));
		if (index == -1)
		{
			throw new UnknownProductException("Product " + productId + " Not Found");
		}
		Product product = products.get(index);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			throw new InvalidProductOptionsException("Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions);
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			throw new ProductOutStockException("Product " + product.getName() + " ProductId " + productId + " Out of Stock");
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		orders.add(order);
		System.out.println("Order #" + order.getOrderNumber());
		// return order.getOrderNumber();
	}

	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public void createCustomer(String name, String address)
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			throw new InvalidNameException("Invalid Customer Name " + name);
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			throw new InvalidAddressException("Invalid Customer Address " + address);
		}
		Customer customer = new Customer(generateCustomerId(), name, address);
		customers.add(customer);
	}

	public void shipOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		order.print();
		
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public void cancelOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			throw new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
	}

	// Sort products by increasing price
	public void sortByPrice()
	{
		Collections.sort(products, new PriceComparator());
	}

	private class PriceComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			if (a.getPrice() > b.getPrice()) return 1;
			if (a.getPrice() < b.getPrice()) return -1;	
			return 0;
		}
	}

	// Sort products alphabetically by product name
	public void sortByName()
	{
		Collections.sort(products, new NameComparator());
	}

	private class NameComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
	}

	public void addToCart(String productId,String customerId,String productOptions)
	{ boolean valid_cust = false;
      Customer current_customer = new Customer(customerId);

	  // First check to see if customer object with customerId exists in array list customers
      // if it does not, throw appropriate exception

      for (int i = 0; i<customers.size();i++){
        if (customers.get(i).getId().equals(customerId)){
          current_customer = customers.get(i);
          valid_cust = true;
          break;
        }
      }
      if (valid_cust == false){
		  throw new UnknownCustomerException("Customer " + customerId + " Not Found");
      }
    	
    	// Check to see if product object with productId exists in array list of products
    	// if it does not, throw appropriate exception
      boolean valid_product = false;
      Product current_product = new Product(productId);

      for (int i = 0; i<products.size();i++){
        if (products.get(i).getId().equals(productId)){
          current_product = products.get(i);
          valid_product = true;
          break;
        }
      }
      if (valid_product == false){
		  throw new UnknownProductException("Product " + productId + " Not Found");
      }
    	
    	// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
    	// If not, throw appropriate exception
      if (current_product.validOptions(productOptions) == false){
		  throw new InvalidProductOptionsException("Product " + current_product.getName() + "ProductId " + productId + " Invalid Options: " + productOptions);
     
      }
    	
    	// Check if the product has stock available (i.e. not 0)
    	// If no stock, throw appropriate exception
      if (current_product.getStockCount(productOptions) == 0){
		  throw new UnknownProductException("Product " + productId + " Not Found");
      }

	  // Create new CartItem by using the current_product
	  // Add the item to the customer's cart
		
		CartItem item = new CartItem(current_product); 
		current_customer.addItem(item);
		
	}

	public void print_options(String productId)
	{
		Product current_product = new Product(productId);

		// Set current_product as the product in the products array 
		// which has the corresponding productid.
		for (int i = 0; i<products.size();i++){
		  if (products.get(i).getId().equals(productId)){
			current_product = products.get(i);
			break;
		  }
		}
		// Conditions to customize product options printing depending on product category.

		if (current_product.getCategory() == Product.Category.BOOKS){
			System.out.println("Enter one of: PaperBack OR EBook OR Hardcover");
		}
		else if (current_product.getCategory() == Product.Category.SHOES){
			System.out.println("Enter size from 6 to 10 and the colour black or brown, for example 6 Black");
			}
		else{
			System.out.println("Since it is not a book or shoe, no option is required, please leave blank");
		}
	}

	public void removeFromCart(String productId,String customerId)
	{
		boolean valid_cust = false;
      Customer current_customer = new Customer(customerId);

	  // First check to see if customer object with customerId exists in array list customers
      // if it does not, throw appropriate exception.

      for (int i = 0; i<customers.size();i++){
        if (customers.get(i).getId().equals(customerId)){
          current_customer = customers.get(i);
          valid_cust = true;
          break;
        }
      }
      if (valid_cust == false){
		  throw new UnknownCustomerException("Customer " + customerId + " Not Found");
        
      }
    	
    	// Check to see if product object with productId exists in array list of products
    	// if it does not, throw appropriate exception.
      boolean valid_product = false;
      Product current_product = new Product(productId);

      for (int i = 0; i<products.size();i++){
        if (products.get(i).getId().equals(productId)){
          current_product = products.get(i);
          valid_product = true;
          break;
        }
      }
      if (valid_product == false){
		  throw new UnknownProductException("Product " + productId + " Not Found");
      }
	  // Find the customer and remove the item to their cart.
		CartItem item = new CartItem(current_product); 
	  	Boolean non_valid = true;

		//Go through the customer's items and if they have the item we are looking for, then remove it.
	for (int i = 0 ; i < current_customer.getItems().size();i++){
		if (current_customer.getItems().get(i).getProduct() == item.getProduct()){
			current_customer.removeItem(i);
			non_valid = false;
		}
	}
		if (non_valid){
			System.out.println("Customer " + current_customer.getName() + "does not have that product in their cart.");
		}
	

	
	}
	
	public void printCartItems(String customerId)
	{
		 boolean valid_cust = false;
			Customer current_customer = new Customer(customerId);
	  
			// First check to see if customer object with customerId exists in array list customers
			// if it does not, throw appropriate exception
	  
			for (int i = 0; i<customers.size();i++){
			  if (customers.get(i).getId().equals(customerId)){
				current_customer = customers.get(i);
				valid_cust = true;
				break;
			  }
			}
			if (valid_cust == false){
				throw new UnknownCustomerException("Customer " + customerId + " Not Found");
			}
			//print the customer's cart by using the helper method.
			current_customer.printCart();
		
}

	public void productOrder(String customerId)
	{
		boolean valid_cust = false;
		Customer current_customer = new Customer(customerId);
  
		// First check to see if customer object with customerId exists in array list customers
		// if it does not, throw exception
  
		for (int i = 0; i<customers.size();i++){
		  if (customers.get(i).getId().equals(customerId)){
			current_customer = customers.get(i);
			valid_cust = true;
			break;
		  }
		}
		if (valid_cust == false){
			throw new UnknownCustomerException("Customer " + customerId + " Not Found");
		}
		
		//Go through the customers to find the current customer
		for (int i = 0; i<customers.size();i++){
			if (customers.get(i).getId().equals(customerId)){
				current_customer = customers.get(i);

				//Create an arraylist which holds that customer's items
				ArrayList<CartItem> cust_items = new ArrayList<CartItem>();

				//Add the items to the customer CartItems
				for (int f = 0 ; f < customers.get(i).getItems().size();f++){
					cust_items.add(customers.get(i).getItems().get(f));
				}

				//Create new orders for each item and add it to the orders array.
				for (int y = 0; y < cust_items.size();y++){
					ProductOrder current_ProductOrder = new ProductOrder (generateOrderNumber(), cust_items.get(y).getProduct(),current_customer, cust_items.get(y).getProductOptions());
					
					orders.add(current_ProductOrder);
					cust_items.get(y).getProduct().reduceStockCount(cust_items.get(y).getProductOptions());
					
				}
					
				}
				//empty the custmer's cart after creating the orders.
				current_customer.emptyCart();
				
	}

}

public void prod_stats(){

	Product prod = new Product();
	Map<Product,Integer> all_prods = new HashMap<Product,Integer>();

	// Go through the products and create prod Products.
	// Check if the current product is inside the orders list in order to increase count by 1
	for (int i = 0;i<products.size();i++){
		int count = 0;
		for (int y = 0; y<orders.size();y++){
			if (products.get(i) == orders.get(y).getProduct()){
				count++; 
			}
			
		}

	prod = products.get(i);

	// Add a product as key and the #of times that product was ordered as a value to the map
	all_prods.put(prod, count);
}
	// Use Java steams expressions to first sort the map by descdending order of the map values
	// Then create the print statement that is wanted and finally print each statement on a new line.
	all_prods.entrySet().stream()
        .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
		.map(e ->"Product Name: "+ e.getKey().getName() + "                 Product ID:" + 
		e.getKey().getId() + "              Number of times ordered:" + e.getValue())
		.forEach(System.out::println);
		;

	}
 
}

// All the Exception classes that are required...

class UnknownCustomerException
	extends RuntimeException
{
	public UnknownCustomerException(){}

	public UnknownCustomerException(String message)
	{
		super(message);
	}
}

class UnknownProductException
	extends RuntimeException
{
	public UnknownProductException(){}

	public UnknownProductException(String message)
	{
		super(message);
	}
}

class InvalidProductOptionsException
	extends RuntimeException
{
	public InvalidProductOptionsException(){}

	public InvalidProductOptionsException(String message)
	{
		super(message);
	}
}

class ProductOutStockException
	extends RuntimeException
{
	public ProductOutStockException(){}

	public ProductOutStockException(String message)
	{
		super(message);
	}
}

class InvalidNameException
	extends RuntimeException
{
	public InvalidNameException(){}

	public InvalidNameException(String message)
	{
		super(message);
	}
}

class InvalidAddressException
	extends RuntimeException
{
	public InvalidAddressException(){}

	public InvalidAddressException(String message)
	{
		super(message);
	}
}

class InvalidOrderNumberException
	extends RuntimeException
{
	public InvalidOrderNumberException(){}

	public InvalidOrderNumberException(String message)
	{
		super(message);
	}
}
