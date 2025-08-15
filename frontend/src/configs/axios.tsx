import axios, { AxiosInstance, AxiosRequestConfig } from "axios";

// Định nghĩa các cổng backend
export const AUTH_SERVICE_PORT = 8081;
export const USER_SERVICE_PORT = 8082;
export const USER_CATALOGUE_SERVICE_PORT = 8083;
export const PERMISSION_SERVICE_PORT = 8084;

export const serviceConfig = {
  authServiceBaseURL: `http://localhost:${AUTH_SERVICE_PORT}/api/v1`,
  userServiceBaseURL: `http://localhost:${USER_SERVICE_PORT}/api/v1`,
  userCatalogueServiceBaseURL: `http://localhost:${USER_CATALOGUE_SERVICE_PORT}/api/v1`,
  permissionServiceBaseURL: `http://localhost:${PERMISSION_SERVICE_PORT}/api/v1`,
};

export const authServiceInstance = axios.create({
  baseURL: serviceConfig.authServiceBaseURL,
  withCredentials: true,
});

export const userServiceInstance = axios.create({
  baseURL: serviceConfig.userServiceBaseURL,
  withCredentials: true,
});

export const userCatalogueServiceInstance = axios.create({
  baseURL: serviceConfig.userCatalogueServiceBaseURL,
  withCredentials: true,
});

export const permissionServiceInstance = axios.create({
  baseURL: serviceConfig.permissionServiceBaseURL,
  withCredentials: true,
});

const refreshToken = async () => {
    try {

        const refreshToken = localStorage.getItem("refresh_token");
        if (!refreshToken) {
            console.error("Refresh token not found");
            return null;
        }

        const response = await axios.post("http://localhost:8081/api/v1/auth/refresh_token", { 
                refreshToken 
            },
            {
                withCredentials: true
            }
        );

        console.log("Response:", response.data);

        if (response?.data.data.token && response?.data.data.refreshToken) {
            localStorage.setItem("token", response.data.data.token);

            localStorage.setItem("refresh_token", response.data.data.refreshToken);

            return response.data.data.token; 
        }

        return null;
    } catch (error) {
        console.log("Refresh token failed", error);
        localStorage.removeItem("user")
        localStorage.removeItem("token");
        localStorage.removeItem("refresh_token");
        return null;
    }
};

// Ví dụ interceptor cho từng instance
const setupResponseInterceptor = (instance: AxiosInstance) => {
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
      if (originalRequest.url?.includes("/auth/refresh_token")) {
        return Promise.reject(error);
      }
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        const newToken = await refreshToken();
        if (newToken) {
          originalRequest.headers = {
            ...originalRequest.headers,
            Authorization: `Bearer ${newToken}`,
          };
          return instance(originalRequest);
        } else {
          return Promise.reject(error);
        }
      }
      return Promise.reject(error);
    }
  );
};

setupResponseInterceptor(authServiceInstance);
setupResponseInterceptor(userServiceInstance);
setupResponseInterceptor(userCatalogueServiceInstance);

