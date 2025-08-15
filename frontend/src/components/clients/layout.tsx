import React, { useEffect } from "react";
import { Outlet } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

// Helpers
import { showToast } from "../../helpers/myHelper";

// Redux
import { RootState } from "../../redux/store";
import { clearToast } from "../../redux/slice/toastSlice";

// Components
import { Header } from "./header";
import { Footer } from "./footer";

const Layout: React.FC = () => {
    const dispatch = useDispatch();
    const { message, type } = useSelector((state: RootState) => state.toast);

    useEffect(() => {
        showToast(message, type);
        dispatch(clearToast());
    }, [message, type, dispatch]);

    return (
        <div className="flex flex-col min-h-screen">
            <Header />
                <main className="flex-grow p-3">
                    <Outlet />
                </main>
            <Footer />
        </div>
    );
};

export default Layout;