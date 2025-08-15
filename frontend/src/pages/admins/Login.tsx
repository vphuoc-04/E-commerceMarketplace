import * as React from "react"
import { useNavigate } from "react-router-dom";

import { SubmitHandler, useForm } from "react-hook-form"

// Services
import { login } from "../../services/AuthService";

// Redux
import { useDispatch } from "react-redux";
import { setToast } from "../../redux/slice/toastSlice";

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input";

type Inptus = {
    email: string,
    password: string,
}

const Login = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    
    const { register, handleSubmit, setFocus, formState: { errors } } = useForm<Inptus>();
    const [loading, setLoading] = React.useState<boolean>(false);
    const [show, setShow] = React.useState<boolean>(false);

    React.useEffect(() => {
        setFocus("email");
        setFocus("password");
    }, [setFocus]);

    const loginHandler: SubmitHandler<Inptus> = async (payload) => {
        setLoading(true);
        try {
            const logged = await login(payload);
            if (logged) {
                dispatch(setToast({ message: 'Login successfully', type: 'success' }));
                navigate('/admin/dashboard');
            }
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-teal-50 bg-auto">
            <div className="absolute top-2/5 left-2/4 transform -translate-x-2/4 -translate-y-2/4">
                <div className="flex gap-2.5">
                    <div className="mt-12 w-[500px]">
                        <h2 className="text-gray-500">Welcome Back Admin</h2>
                        <p className="text-gray-500">
                            We are always here waiting for you to come back.<br />
                            We are so happy to have been created by you in this world.<br />
                            We wish you a good day.
                        </p>
                    </div>
                    <form onSubmit={handleSubmit(loginHandler)} className="py-10 px-5 h-[300px] w-[400px] rounded-lg bg-white border-2">
                        <div className="flex flex-col gap-6">
                            <div className="relative">
                                <Input className="w-full h-9 border border-gray-300 rounded py-5 px-3 focus:border-teal-500 transition"
                                    id="email"
                                    type="text"
                                    placeholder="Email"
                                    {...register("email", { required: true })}
                                />
                                {errors?.email && (
                                    <span className="text-red-500 text-xs absolute left-[0px] top-[45px]">*Email is required</span>
                                )}
                            </div>
                            <div className="relative">
                                <Input className="w-full h-9 border border-gray-300 rounded py-5 px-3 focus:border-teal-500 transition"
                                    id="password"
                                    type={show ? "text" : "password"}
                                    placeholder="Password"
                                    {...register("password", { required: true })}
                                />
                                {errors?.password && (
                                    <span className="text-red-500 text-xs absolute left-[0px] top-[45px]">*Password is required</span>
                                )}
                                <div className="absolute top-[8px] left-[90%] cursor-pointer">
                                    <i
                                        className={ show ? "fa-regular fa-eye" : "fa-regular fa-eye-slash" }
                                        onClick={() => setShow(!show)}
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="mt-6">
                            <Button               
                                className="w-full bg-teal-500 text-white py-2 rounded hover:bg-teal-400 cursor-pointer"
                                type="submit"
                                disabled={loading}
                            >
                                {loading ? <div className="spinner"></div> : "Login"}
                            </Button>
                        </div>
                        <div className="text-blue-500 text-xs mt-2 cursor-pointer hover:text-blue-700 transition"> Forgot password? </div>
                        <p className="text-xs mt-2">Welcome to the E-commerce Market, here is every product you want</p>
                    </form>
                </div>
            </div>
        </div>
    );

};

export default Login;