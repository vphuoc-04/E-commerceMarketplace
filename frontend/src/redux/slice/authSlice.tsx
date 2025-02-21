import { createSlice } from "@reduxjs/toolkit"
import type { PayloadAction } from "@reduxjs/toolkit"
import { User } from "../../types/User"

export interface AuthState {
    isAuthenticated: boolean
    user: User | null
}

const initialState: AuthState = {
    isAuthenticated: false,
    user: null
}

export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {

        setAuthLogin: (state, action: PayloadAction<User | null>) => {
            console.log("setAuthLogin triggered:", action.payload);
            if (action.payload) {
                state.isAuthenticated = true;
                state.user = action.payload;
            }
        },

        setAuthLogout: (state) => {
            state.isAuthenticated = false
            state.user = null
        }
    }
})

export const { setAuthLogin, setAuthLogout } = authSlice.actions

export default authSlice.reducer