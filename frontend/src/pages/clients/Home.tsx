import React, { useEffect, useState } from "react";
import { DeleteIcon, ShoppingCart } from "lucide-react"; 
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

interface Product {
    id: number;
    productName: string;
    productPrice: number;
    productImage: string;
}

interface Payment {
    id: number;
    totalAmount: number;
    paymentDate: Date
}

const Home: React.FC = () => {
    const products: Product[] = [
      { id: 1, productName: "Hamburger", productPrice: 100000, productImage: "https://i.pinimg.com/736x/d5/d4/bb/d5d4bb7e8a83e3cc20f3383e4ca3e5c7.jpg" },
      { id: 2, productName: "Mì", productPrice: 200000, productImage: "https://i.pinimg.com/736x/3d/0c/a8/3d0ca8bad65d9c74d19b6a2aa794ae9d.jpg" },
      { id: 3, productName: "Trái cây", productPrice: 150000, productImage: "https://i.pinimg.com/736x/a9/6e/45/a96e45fe30bd90bb75f724082e7a5654.jpg" },
      { id: 4, productName: "Bánh kem", productPrice: 150000, productImage: "https://i.pinimg.com/736x/66/4d/7a/664d7a1f9891c8246578ddad6ec7cf49.jpg" },
      { id: 5, productName: "Gà nướng", productPrice: 150000, productImage: "https://i.pinimg.com/736x/9e/8f/74/9e8f7475ac214d7f1e1863c99eeeb307.jpg" },
      { id: 6, productName: "Cơm trứng", productPrice: 150000, productImage: "https://i.pinimg.com/736x/69/04/76/6904765c3c75723eb584da79516327c3.jpg" },
      { id: 7, productName: "Cơm thập cẩm", productPrice: 150000, productImage: "https://i.pinimg.com/736x/b1/fa/20/b1fa200bc4c18e098d72d7187f1eb857.jpg" },
      { id: 8, productName: "Bánh ngọt", productPrice: 150000, productImage: "https://i.pinimg.com/736x/1b/40/53/1b40530cc6c6de474bab5b0dabeed17e.jpg" },
      { id: 9, productName: "Bánh plan", productPrice: 150000, productImage: "https://i.pinimg.com/736x/09/a5/fb/09a5fb7ecdc8bb8cb57c130a061e193d.jpg" },
    ];

    const [cart, setCart] = useState<Product[]>([]);
    const [showCart, setShowCart] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");

    const userId = 1;
    const totalPrice = cart.reduce((total, item) => total + Number(item.productPrice), 0);
    const [paymentHistory, setPaymentHistory] = useState<Payment[]>([]);

    const fetchCartData = async () => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Token không tồn tại");
                return;
            }
    
            const response = await fetch(`http://localhost:8080/api/v1/get_cart_data/${userId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
    
            console.log("Response status:", response.status);
    

            if (!response.ok) {
                const errorResponse = await response.json(); 
                throw new Error(errorResponse.message || "Lỗi khi lấy dữ liệu giỏ hàng");
            }
    
            const responseData = await response.json();

            if (responseData.success && responseData.data) {
                setCart(responseData.data);
            } else {
                console.warn("Dữ liệu giỏ hàng không hợp lệ:", responseData);
            }
        } catch (error) {
            console.error("Lỗi khi fetch giỏ hàng:", error);
        }
    };    
    
    useEffect(() => {
        fetchCartData();
    }, []);

    const addToCart = async (product: Product) => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Token not found");
            }

            const response = await fetch("http://localhost:8080/api/v1/add_to_cart", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ 
                    productId: product.id,
                    productName: product.productName,
                    productPrice: product.productPrice,
                    productImage: product.productImage
                }),
                credentials: "include",
            });
    
            if (!response.ok) throw new Error("Lỗi khi thêm vào giỏ hàng");
    
            const newItem = await response.json();
            setCart([...cart, newItem]); 
        } catch (error) {
            console.error(error);
        }
    };

    const removeFromCart = async (id: number) => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Token not found");
            }

            const response = await fetch(`http://localhost:8080/api/v1/remove_from_cart/${id}`, {
                method: "DELETE",
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("Lỗi khi xóa sản phẩm");

            setCart(cart.filter(item => item.id !== id));
        } catch (error) {
            console.error(error);
        }
    };

    const paymentCart = async () => {
        try {
            if (!cart || cart.length === 0) {
                alert("Giỏ hàng trống, không thể thanh toán!");
                return;
            }
    
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Token not found");
                return;
            }
    
            const requestData = {
                userId,
                cart,
                totalAmount: totalPrice, 
            };
    
            console.log("Request Data:", requestData); 
    
            const response = await fetch("http://localhost:8080/api/v1/process_payment", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(requestData),
            });
    
            const data = await response.json();
            console.log("Response Data:", data); 
    
            if (response.ok) {
                alert("Payment successfully!");
                setCart([]);
            } else {
                alert(`Error: ${data.message}`);
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Payment fail!");
        }
    };

    const fetchPaymentHistory = async () => {
        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Token not found");
                return;
            }
    
            const response = await fetch("http://localhost:8080/api/v1/get_payment_history", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });
    
            const data = await response.json();
            if (response.ok) {
                setPaymentHistory(data.data.map((payment: { id: number; totalAmount: number; paymentDate: string }) => ({
                    id: payment.id,
                    totalAmount: payment.totalAmount,
                    paymentDate: new Date(payment.paymentDate) 
                })));
            } else {
                console.error("Error:", data.message);
            }
            
        } catch (error) {
            console.error("Error:", error);
        }
    };
    

    const filteredProducts = products.filter((product) =>
        product.productName.toLowerCase().includes(searchTerm.toLowerCase())
    );
    
    return (
        <div className="p-5">
            <h1 className="text-2xl font-bold mb-4">Sản phẩm nổi bật</h1>

            <Input 
                type="text"
                placeholder="Search..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full p-2 mb-4 border border-gray-300 rounded-lg"
            />

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 w-full">
                {filteredProducts.length > 0 ? (
                    filteredProducts.map((product) => (
                        <div key={product.id}  className="border border-gray-300 rounded-lg p-4">
                            <img 
                                src={product.productImage} 
                                alt={product.productName} 
                                className="w-full rounded-lg" 
                            />
                            <h2 className="text-lg font-semibold mt-2">{product.productName}</h2>
                            <p className="text-blue-500 font-bold">{product.productPrice}</p>
                            <Button
                                onClick={() => addToCart(product)}
                                className="mt-2 px-4 py-2 bg-blue-500 text-black rounded-lg"
                            >
                                Thêm vào giỏ hàng
                            </Button>
                        </div>
                    ))
                ) : (
                    <div className="ml-163 w-200">Không có sản phẩm nào</div>
                )}
            </div>


            <div
                className="fixed bottom-6 right-6 bg-blue-500 text-white p-3 rounded-full shadow-lg cursor-pointer flex items-center justify-center"
                onClick={() => setShowCart(!showCart)}
            >
                <ShoppingCart size={24} />
                  {cart.length > 0 && (
                      <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold w-5 h-5 flex items-center justify-center rounded-full">
                          {cart.length}
                      </span>
                  )}
            </div>

            {showCart && (
                <div className="fixed bottom-16 right-6 w-80 bg-white border shadow-lg rounded-lg p-4">
                    <h2 className="text-lg font-bold border-b pb-2">Giỏ hàng ({cart.length} sản phẩm)</h2>
                    <ul className="mt-2 max-h-60 overflow-y-auto">
                        {cart.map((item, index) => (
                            <li key={index} className="flex items-center justify-between border-b py-2">
                                <img src={item.productImage} alt={item.productName} className="w-10 h-10 rounded" />
                                <div className="flex-1 ml-2 flex justify-between items-center">
                                    <div>
                                        <p className="text-sm font-semibold">{item.productName}</p>
                                        <p className="text-xs text-gray-500">{item.productPrice} VND</p>
                                    </div>
                                    <DeleteIcon onClick={() => removeFromCart(item.id)} className="cursor-pointer text-red-500" />
                                </div>
                            </li>
                        ))}
                    </ul>

                    <div className="flex justify-between mt-4 font-bold">
                        <span>Tổng tiền:</span>
                        <span>{totalPrice.toLocaleString()} VND</span>
                    </div>

                    <Button 
                        onClick={paymentCart} 
                        className="mt-4 w-full bg-blue-500 text-black py-2 rounded hover:bg-blue-600"
                    >
                        Payment
                    </Button>

                    <Button 
                        onClick={fetchPaymentHistory} 
                        className="mt-2 w-full bg-gray-500 text-black py-2 rounded hover:bg-gray-600"
                    >
                        History
                    </Button>

                    {paymentHistory.length > 0 && (
                        <div className="mt-4 p-2 bg-gray-100 rounded">
                            <h3 className="font-bold">Lịch sử thanh toán</h3>
                            <ul className="mt-2 max-h-40 overflow-y-auto">
                                {paymentHistory.map((payment) => (
                                    <li key={payment.id} className="border-b py-2">
                                        <p className="text-sm">ID: {payment.id}</p>
                                        <p className="text-sm">Tổng tiền: {payment.totalAmount.toLocaleString()} VND</p>
                                        <p className="text-sm">Ngày: {new Date(payment.paymentDate).toLocaleDateString()}</p>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Home;
