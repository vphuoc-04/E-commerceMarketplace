import { useEffect, useState } from "react";

const useCheckBoxState = (data: any, model: string, isLoading: boolean) => {
    const [checkedState, setCheckedState] = useState<{ [Key: number] : boolean }>({});
    const [checkedAllState, setCheckedAllState] = useState<boolean>(false);

    const handleCheckedChange = (id: number) => {
        const updateCheckedState = { checkedState, [id] : !checkedState[id] }
        const allChecked = Object.values(updateCheckedState).every(value => value)

        setCheckedState(updateCheckedState)
        setCheckedAllState(allChecked)
    }

    const handleCheckedAllChange = () => {
        const newCheckAllState = !checkedAllState
        const updateCheckedState = Object.keys(checkedState).reduce((acc: any, key: string) => {
            acc[key] = newCheckAllState;
            return acc;
        }, {})

        setCheckedState(updateCheckedState)
        setCheckedAllState(newCheckAllState)
    }

    const isAnyChecked = () => Object.values(checkedState).some(value => value)

    useEffect(() => {
        if(!isLoading && data[model]) {
            const initialCheckBoxState = data[model].reduce((acc: any, item: any) => {
                acc[item.id] = false
                return acc
            }, {})

            setCheckedState(initialCheckBoxState)
            setCheckedAllState(false)
        }
    }, [isLoading, data])

    return { checkedState, checkedAllState, handleCheckedChange, handleCheckedAllChange, isAnyChecked }
}   

export default useCheckBoxState;