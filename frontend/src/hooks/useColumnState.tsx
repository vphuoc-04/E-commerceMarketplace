import { useState } from "react"

interface ColumnState {
    [columnName: string]: boolean
}

interface UserState {
    [userId: string] : ColumnState
}

interface ColumnStateReturn {
    columnState: UserState,
    handleChecked: (userId: string, columnName: string) => void
    setInitialColumnState: (users: any[], columnName: string) => void
}

const useColumnState = (): ColumnStateReturn => {
    const [columnState, setColumnState] = useState<UserState>({})

    const handleChecked = (userId: string, columnName: string) => {
        setColumnState((prevState) => ({
            ...prevState,
            [userId]: {
                ...prevState[userId],
                [columnName]: !prevState[userId]?.[columnName]
            }
        }))
    }

    const setInitialColumnState = (users: any[], columnName: string) => {
        const initialState = users.reduce((acc: UserState, user: any) => {
            acc[user.id] = {
                [columnName]: user[columnName] === 2
            }

            return acc
        }, {})

        console.log(initialState)
    }

    return { columnState, handleChecked, setInitialColumnState }
}

export default useColumnState;