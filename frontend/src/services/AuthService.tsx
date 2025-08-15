// Configs
import { authServiceInstance } from '../configs/axios';

// Helpers
import { handleAxiosError } from "../helpers/axiosHelper";

type RegisterPayload = {
    firstName: string,
    middleName: string | null,
    lastName: string,
    email: string,
    phone: string,
    password: string,
}

type LoginPayload = {
    email: string, 
    password: string,
}

const customerRegister = async (payload: RegisterPayload): Promise<boolean> => {
    try {
        const response = await authServiceInstance.post('/auth/register', {
            firstName: payload.firstName,
            middleName: payload.middleName,
            lastName: payload.lastName,
            email: payload.email,
            phone: payload.phone,
            password: payload.password
        })

        console.log(response.data)

        return response.data;

    } catch (error) {
        handleAxiosError(error);
        return false;
    }
}

const login = async (payload:LoginPayload): Promise<boolean> => {
    try {
        const response = await authServiceInstance.post('/auth/login', {
            email: payload.email,
            password: payload.password
        })

        if (response?.data) {
            localStorage.setItem('user', JSON.stringify(response.data.data.user));
            localStorage.setItem('token', response.data.data.token);
            localStorage.setItem('refresh_token', response.data.data.refreshToken);
        }

        console.log(response);

        return true;

    } catch (error) {
        handleAxiosError(error);
        return false;
    }
}

const logout = async (): Promise<boolean> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("Token not found");
        }

        await authServiceInstance.get("/auth/logout", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })

        localStorage.removeItem("user");
        localStorage.removeItem("token");
        localStorage.removeItem("refresh_token");

        return true;

    } catch (error) {
        handleAxiosError(error);
        return false;
    }
}

export { login, logout, customerRegister }