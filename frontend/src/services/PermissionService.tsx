// Configs
import { permissionServiceInstance } from '../configs/axios';
// Helpers
import { handleAxiosError } from "../helpers/axiosHelper";

export interface Permission {
    id: number;
    name: string;
    description?: string;
    publish: number;
}

const getAllPermissions = async (): Promise<Permission[]> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            console.error("Token not found");
            return [];
        }
        const response = await permissionServiceInstance.get(
            "/permissions/get_all",
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );
        return response.data.data || [];
    } catch (error) {
        handleAxiosError(error);
        return [];
    }
};

export const createPermission = async (name: string, publish: number, description?: string): Promise<boolean> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) return false;
        const response = await permissionServiceInstance.post(
            "/permissions/create",
            { name, publish, description },
            { headers: { Authorization: `Bearer ${token}` } }
        );
        return response.data && response.data.success;
    } catch (error) {
        handleAxiosError(error);
        return false;
    }
};

export const updatePermission = async (id: number, name: string, publish: number, description?: string): Promise<boolean> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) return false;
        const response = await permissionServiceInstance.put(
            `/permissions/update/${id}`,
            { name, publish, description },
            { headers: { Authorization: `Bearer ${token}` } }
        );
        return response.data && response.data.success;
    } catch (error) {
        handleAxiosError(error);
        return false;
    }
};

export const deletePermission = async (id: number): Promise<boolean> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) return false;
        const response = await permissionServiceInstance.delete(
            `/permissions/delete/${id}`,
            { headers: { Authorization: `Bearer ${token}` } }
        );
        return response.data && response.data.success;
    } catch (error) {
        handleAxiosError(error);
        return false;
    }
};

export const getPermissionById = async (id: number): Promise<Permission | null> => {
    try {
        const token = localStorage.getItem("token");
        if (!token) return null;
        const response = await permissionServiceInstance.get(
            `/permissions/${id}`,
            { headers: { Authorization: `Bearer ${token}` } }
        );
        return response.data.data || null;
    } catch (error) {
        handleAxiosError(error);
        return null;
    }
};

export {
    getAllPermissions
};