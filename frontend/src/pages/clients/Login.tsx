import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, SubmitHandler } from "react-hook-form";

// Services
import { login } from "@/services/AuthService";

// Redux
import { useDispatch } from "react-redux";
import { setToast } from "@/redux/slice/toastSlice";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

type Inputs = {
    email: string;
    password: string;
};

const Login = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { register, handleSubmit, formState: { errors } } = useForm<Inputs>();
    const [loading, setLoading] = useState<boolean>(false);
    const [show, setShow] = useState<boolean>(false);

    const loginHandler: SubmitHandler<Inputs> = async (payload) => {
        setLoading(true);
        try {
            const loggedIn = await login(payload);
            if (loggedIn) {
                dispatch(setToast({ message: 'Login successfully', type: 'success' }));
                navigate('/');
            }
        } catch (error) {
            console.log(error);
            dispatch(setToast({ message: 'Invalid email or password', type: 'error' }));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-bg-cyan-500 bg-auto">
            <div className="absolute top-2/4 left-2/4 transform -translate-x-2/4 -translate-y-2/4 border-2 py-10 px-5 rounded-md">
                <h2 className="text-2xl font-semibold text-center mb-6">Login</h2>
                <form onSubmit={handleSubmit(loginHandler)} className="space-y-6">
                    <div className="relative">
                        <Input className="w-80" placeholder="Email" type="email" {...register("email", { required: true })} />
                        {errors.email && <p className="text-red-500 text-xs absolute left-[0px] top-[40px]">*Email is required</p>}
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Password" type={show ? "text" : "password"} {...register("password", { required: true })} />
                        {errors.password && <p className="text-red-500 text-xs absolute left-[0px] top-[40px]">*Password is required</p>}

                        <div className="absolute top-[6px] left-[90%] cursor-pointer">
                            <i
                                className={ show ? "fa-regular fa-eye" : "fa-regular fa-eye-slash" }
                                onClick={() => setShow(!show)}
                            />
                        </div>
                    </div>

                    <Button type="submit" className="w-full bg-blue-500 text-black py-2 rounded" disabled={loading}>
                        {loading ? <div className="spinner"></div> : "Login"}
                    </Button>
                </form>
                <p className="text-sm text-center mt-4">Don't have an account? <a href="/register" className="text-blue-500">Register</a></p>
            </div>
        </div>
    );
};

export default Login;
