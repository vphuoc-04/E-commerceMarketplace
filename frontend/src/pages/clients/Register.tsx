import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm, SubmitHandler } from "react-hook-form";

// Services
import { customerRegister } from "@/services/AuthService";

// Redux
import { useDispatch } from "react-redux";
import { setToast } from "@/redux/slice/toastSlice";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

type Inputs = {
    firstName: string;
    middleName: string | null;
    lastName: string;
    email: string;
    phone: string;
    password: string;
};

const Register = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { register, handleSubmit, formState: { errors } } = useForm<Inputs>();
    const [loading, setLoading] = useState<boolean>(false);
    const [show, setShow] = useState<boolean>(false);

    const registerHandler: SubmitHandler<Inputs> = async (payload) => {
        setLoading(true);
        try {
            const registered = await customerRegister(payload);
            if (registered) {
                dispatch(setToast({ message: 'Register successfully', type: 'success' }));
                navigate('/login');
            }
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-teal-50 bg-auto">
            <div className="absolute top-2/4 left-2/4 transform -translate-x-2/4 -translate-y-2/4 border-2 p-10 rounded-md">
                <h2 className="text-2xl font-semibold text-center mb-6">Register</h2>
                <form onSubmit={handleSubmit(registerHandler)} className="space-y-6">
                    <div className="relative">
                        <Input className="w-80" placeholder="First name" {...register("firstName", { required: true })} />
                        {errors.firstName && <p className="text-red-500 text-xs absolute left-[0px] top-[37px]">*First name is required</p>}
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Middle name (option)" {...register("middleName")} />
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Lastname" {...register("lastName", { required: true })} />
                        {errors.lastName && <p className="text-red-500 text-xs absolute left-[0px] top-[37px]">*Last name is required</p>}
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Email" type="email" {...register("email", { required: true })} />
                        {errors.email && <p className="text-red-500 text-xs absolute left-[0px] top-[37px]">*Email is required</p>}
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Phone" type="tel" {...register("phone", { required: true })} />
                        {errors.phone && <p className="text-red-500 text-xs absolute left-[0px] top-[37px]">*Phone is required</p>}
                    </div>

                    <div className="relative">
                        <Input className="w-80" placeholder="Password" type={show ? "text" : "password"} {...register("password", { required: true })} />
                        {errors.password && <p className="text-red-500 text-xs absolute left-[0px] top-[37px]">*Password is required</p>}

                        <div className="absolute top-[8px] left-[90%] cursor-pointer">
                            <i className={ show ? "fa-regular fa-eye" : "fa-regular fa-eye-slash" } onClick={() => setShow(!show)} />
                        </div>
                    </div>
                    <Button type="submit" className="w-full bg-blue-500 text-black py-2 rounded" disabled={loading}>
                        {loading ? <div className="spinner"></div> : "Register"}
                    </Button>
                </form>
                <p className="text-sm text-center mt-4">Already have an account? <a href="/login" className="text-blue-500">Login</a></p>
            </div>
        </div>
    );
};

export default Register;
