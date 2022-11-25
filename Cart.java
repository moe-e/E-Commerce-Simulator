import java.util.ArrayList;

//Cart class which consists of a list of cartitems.
public class Cart {
    private ArrayList<CartItem> items;

    //initiliazice the cart
    public Cart()
    {
        this.items = new ArrayList<CartItem>();
    }

    //add a cartiem to cart
    public void addToArray(CartItem item)
    {
        items.add(item); 
    }

    //remove a specific index of the cart
    public void removeFromArray(int i)
    {
        items.remove(i);
    }

    //empty the cart
    public void emptyArray(){
        items.clear();
    }

    //print the items in the cart
    public void print(){
        if(items.size() == 0){
            System.out.println("Cart is empty.");
        }
        else{
            for (int i = 0 ; i < items.size();i++ ){
                items.get(i).getProduct().print();
        }
    }
    
    //return the cart
    }
    public ArrayList<CartItem> cartItems(){
        return items;

    }
    
}
