import React, { useEffect } from "react";
import { Outlet } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

// Helpers
import { showToast } from "../../helpers/myHelper";

// Redux
import { RootState } from "../../redux/store";
import { clearToast } from "../../redux/slice/toastSlice";

// components
import { Aside } from "./aside";
import { Header } from "./header";

const Layout: React.FC = () => {
    const dispatch = useDispatch();
    const { message, type } = useSelector((state: RootState) => state.toast);

    useEffect(() => {
        showToast(message, type);
        dispatch(clearToast());
    }, [message, type, dispatch]);

    return (
        <div className="flex w-screen">
            <Aside />
            <div className="flex-1 bg-gray-100 flex flex-col">
                <Header />
                <div>
                    <Outlet />
                </div>
            </div>
        </div>
    );
};

export default Layout;