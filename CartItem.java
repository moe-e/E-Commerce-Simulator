//Class CartItem which creates CartItems that will then be put in the Cart.
public class CartItem {
    Product item = new Product();
    private String product_options;

    //Initialize CartItem
    public CartItem(Product item, String productOption)
	{   
		this.item = item ;
		this.product_options = productOption;
	}

    public CartItem(Product item)
	{   
		this.item = item ;
	}

    //Return the CartItem
    public Product getProduct() {
        return item;
    }

    //Return the CartItem's product options.
    public String getProductOptions() {
        if (product_options == null){
            return "";
        }
        return product_options;
    }
}
