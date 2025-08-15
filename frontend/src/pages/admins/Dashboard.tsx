import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";

// Helpers
import { showToast } from "../../helpers/myHelper";

// Redux
import { RootState } from "../../redux/store";
import { clearToast } from "../../redux/slice/toastSlice";
import PageHeading from "@/components/admins/heading";

const Dashboard = () => {
    const { message, type } = useSelector((state: RootState) => state.toast);

    const dispatch = useDispatch();

    useEffect(() => {
        showToast(message, type)
        dispatch(clearToast())
    }, [message, type, dispatch])

    const breadcrumb = {
        title: "Thống kê chung",
        route: '/admin/dashboard'
    }

    return (
        <div>
            <PageHeading breadcrumb = { breadcrumb }/>
        </div>
    )
}

export default Dashboard