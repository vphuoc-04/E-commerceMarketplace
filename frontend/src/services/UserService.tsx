import { JSX } from 'react';

// Configs
import { userServiceInstance } from '../configs/axios';

// Helpers
import { handleAxiosError } from "../helpers/axiosHelper";

// Types
import { User } from "../types/User";

const fetchUser = async(): Promise<User | null> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("Token not found");
            return null;
        }

        const response = await userServiceInstance.get("/users/me", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        console.log("User data: ", response.data.data)

        return response.data.data


    } catch (error) {
        handleAxiosError(error)
        return null
    }
}

const pagination = async (page: number | null) => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("Token not found");
            return null;
        }

        const response = await userServiceInstance.get(`/users/get_all_user?page=${page}`, {
            headers: { Authorization: `Bearer ${token}` },
        });

        console.log("API Response:", response.data); 

        return {
           users: response.data?.data?.content || []
        };
    } catch (error) {
        console.error("Failed to fetch users", error);
        return { users: [] };
    }
};


const model = 'users';

const breadcrumb = {
    title: "Quản lý người dùng",
    route: "/admin/user/index"
}

interface tableColumn {
    name: string,
    render: (item: User) => JSX.Element
}

const tableColumn: tableColumn[] = [
    {
        name: 'ID',
        render: (item: User) => <span>{item.id}</span>
    },
    {
        name: 'Họ',
        render: (item: User) => <span>{item.lastName}</span>
    },
    {
        name: 'Tên đệm',
        render: (item: User) => <span>{item?.middleName}</span>
    },
    {
        name: 'Tên',
        render: (item: User) => <span>{item.firstName}</span>
    },
    {
        name: 'Email',
        render: (item: User) => <span>{item.email}</span>
    },
    {
        name: 'Số điện thoại',
        render: (item: User) => <span>{item.phone}</span>
    }
]

export { fetchUser, pagination, tableColumn, model, breadcrumb };
