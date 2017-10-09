package in.shaaan.ga_onlineorders.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by shant on 12-04-2017.
 */

@IgnoreExtraProperties
public class OrderData {

    private String custName;
    private String date;
    private String email;
    private String expProducts;
    private String products;
    private String product;
    private String scheme;
    private String quantity;

    public OrderData() {

    }

    public OrderData(String expProducts, String quantity, String scheme, String product, String custName, String date, String products, String email) {
        this.email = email;
        this.quantity = quantity;
        this.custName = custName;
        this.date = date;
        this.products = products;
        this.expProducts = expProducts;
        this.product = product;
        this.scheme = scheme;

    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getExpProducts() {
        return expProducts;
    }

    public void setExpProducts(String expProducts) {
        this.expProducts = expProducts;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
