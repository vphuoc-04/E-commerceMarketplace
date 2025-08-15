import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

// Types
import { User } from "../../types/User";

// Services
import { logout } from "../../services/AuthService";
import { fetchUser } from "../../services/UserService";

// Redux
import { useDispatch } from "react-redux";
import { setAuthLogout } from "../../redux/slice/authSlice";

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
  } from "@/components/ui/dropdown-menu"

const Header = () => {
    const [user, setUser] = useState<User | null>(null);

    const navigate = useNavigate()
    const dispatch = useDispatch()

    useEffect(() => {
        const fetchUserData = async () => {
            const userData = await fetchUser();
            setUser(userData);
        };
        fetchUserData();
    }, []);


    const logoutHandler = async () => {
        const result = await logout();

        if (result) {
            dispatch(setAuthLogout());
            navigate('/admin/login');
        }
    }

    return (
        <div className="bg-white h-12 flex justify-end pr-5 relative shadow-md">
            <div className="relative mt-1 mr-10 cursor-pointer">
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <img
                            src="https://i.pinimg.com/736x/73/79/8c/73798c5c5dfb267fce136b18cf1a259c.jpg"
                            alt="Avatar"
                            className="w-10 h-10 rounded-full cursor-pointer"
                        />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="center">
                        <DropdownMenuLabel>{user?.firstName} {user?.middleName} {user?.lastName}</DropdownMenuLabel>
                        <DropdownMenuSeparator />
                        <DropdownMenuSeparator />
                        <DropdownMenuItem className="cursor-pointer">Profile</DropdownMenuItem>
                        <DropdownMenuItem className="cursor-pointer">Billing</DropdownMenuItem>
                        <DropdownMenuItem className="cursor-pointer">Team</DropdownMenuItem>
                        <DropdownMenuItem onClick={logoutHandler} className="cursor-pointer">
                            Log out
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>
        </div>
        
    );
};

export { Header };
