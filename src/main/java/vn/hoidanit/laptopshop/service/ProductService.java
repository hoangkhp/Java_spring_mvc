package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    
    

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Product createProduct(Product pr) {
        return this.productRepository.save(pr);
    }

    public List<Product> fetchProducts() {
        return this.productRepository.findAll();
    }

    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session){
        User user = this.userService.getUserByEmail(email);
        if(user !=null){
            Cart cart = this.cartRepository.findByUser(user);
            if(cart ==null){
                //tao moi card
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);
                cart = this.cartRepository.save(otherCart);
            }
            //find Product by id
            Optional<Product> productOptional = this.productRepository.findById(productId);
            if(productOptional.isPresent()){
                Product realProduct = productOptional.get();

                //Check san pham da co trong cart hay chua
                CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
                if(oldDetail == null){
                    CartDetail cd = new CartDetail();
                
                    cd.setCart(cart);
                    cd.setProduct(realProduct);
                    cd.setPrice(realProduct.getPrice());
                    cd.setQuantity(1);
                    this.cartDetailRepository.save(cd);

                    //update sum, update session
                    int s = cart.getSum() + 1;
                    cart.setSum(cart.getSum() + 1);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                }
                else{
                    oldDetail.setQuantity(oldDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oldDetail);
                }
                
            }
            
        }
    }
    
    public Cart fetchByUser(User user){
        return this.cartRepository.findByUser(user);
    }

    public void handleRemoveCartDetail(long CartDetailId,HttpSession session ){
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(CartDetailId);
        if(cartDetailOptional.isPresent()){
            CartDetail cartDetail = cartDetailOptional.get();
            Cart currenCart = cartDetail.getCart();

            //deltete CartDetail
            this.cartDetailRepository.deleteById(CartDetailId);

            //update Cart
            if(currenCart.getSum() > 1){
                int s = currenCart.getSum() - 1;
                currenCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currenCart);
            }
            else{
                this.cartRepository.deleteById(currenCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }
    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName, String receiverAddress, String receiverPhone){
        //create order
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        //luu order ID
        order = this.orderRepository.save(order);
        //create order detail
        
        //step 1: get cart by user
        Cart cart = this.cartRepository.findByUser(user);
        if(cart != null){
            List<CartDetail> cartDetails = cart.getCartDetails();
            if(cartDetails != null){
                for(CartDetail cd : cartDetails){
                    OrderDetail orderDetail = new OrderDetail();
                    //trong database se luu order id
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());
                    this.orderDetailRepository.save(orderDetail);
                }
            }
            //step 2: delete cart detail
            for(CartDetail cd : cartDetails){
                this.cartDetailRepository.deleteById(cd.getId());
            }
            this.cartRepository.deleteById(cart.getId());

            //step 3: update session
            session.setAttribute("sum", 0);
        }
        
        
    }

}