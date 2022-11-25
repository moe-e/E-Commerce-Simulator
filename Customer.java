import java.util.ArrayList;
/*
 *  class Customer defines a registered customer. It keeps track of the customer's name and address. 
 *  A unique id is generated when when a new customer is created. 
 */
public class Customer implements Comparable<Customer>
{
	private String id;  
	private String name;
	private String shippingAddress;
	private Cart cart; //Add the cart variable

	public Customer(String id)
	{
		this.id = id;
		this.name = "";
		this.shippingAddress = "";
		this.cart = new Cart(); //Create new cart for customer
	}

	public Customer(String id, String name, String address)
	{
		this.id = id;
		this.name = name;
		this.shippingAddress = address;
		this.cart = new Cart();
	}
	public Customer(String id, String name, String address, Cart cart)
	{
		this.id = id;
		this.name = name;
		this.shippingAddress = address;
		this.cart = cart;
	}

	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getShippingAddress()
	{
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress)
	{
		this.shippingAddress = shippingAddress;
	}
	//Return the customer's cart
	public Cart getCart()
	{
		return cart;
	}

	//Set the customer's cart as a specific cart
	public void setCart(Cart cart)
	{
		this.cart = cart;
	}

	public void print()
	{
		System.out.printf("\nName: %-20s ID: %3s Address: %-35s", name, id, shippingAddress);
	}

	public boolean equals(Object other)
	{
		Customer otherC = (Customer) other;
		return this.id.equals(otherC.id);
	}

	// Implement the Comparable interface. Compare Customers by name
	public int compareTo(Customer otherCust)
	{
		return this.name.compareTo(otherCust.name);
	}

	//Add a cartitem to the custoer's cart
	public void addItem(CartItem item){
		cart.addToArray(item);
	}

	//Remove a cartitem from a customer's cart
	public void removeItem(int i){
		cart.removeFromArray(i);
	}

	//Print a customer's cart
	public void printCart(){
		cart.print();
	}

	//Get a list of a customer's cartitems
	public ArrayList<CartItem> getItems(){
		return cart.cartItems();
	}

	//Empty a customer's cart
	public void emptyCart(){
		cart.emptyArray();

	}

}
