import { JSX, useEffect } from "react";

import { 
    Table, 
    TableBody, 
    TableCell, 
    TableHead, 
    TableHeader, 
    TableRow 
} from "@/components/ui/table";

import { Checkbox } from "@/components/ui/checkbox";

import useColumnState from "@/hooks/useColumnState";


interface CustomTableProps {
    data: any,
    isLoading: boolean,
    isError: boolean,
    model: string,
    tableColumn: Array<{ name: string; render: (item: any) => JSX.Element }>
}

const CustomTable = ({ isLoading, data, isError, model, tableColumn} : CustomTableProps) => {
    const { columnState, handleChecked, setInitialColumnState } = useColumnState()

    useEffect(() => {
        if (!isLoading && data[model]) {
            setInitialColumnState(data[model], 'publish')
        }
    }, [isLoading, data])

    return (
        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead><Checkbox className="border-black" /></TableHead>
                    <TableHead>ID</TableHead>
                    <TableHead>First Name</TableHead>
                    <TableHead>Middle Name</TableHead>
                    <TableHead>Last Name</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Phone</TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>

                {isLoading ? (
                    <TableRow>
                        <TableCell colSpan={9}>
                            {isLoading ? <div className="spinner"></div> : ""}
                        </TableCell>
                    </TableRow>

                ): isError ? (
                    <TableRow>
                        <TableCell colSpan={9}>
                            Có vấn đề xảy ra trong quá trình truy xuất dữ liệu. Hãy thử lại sau.
                        </TableCell>
                    </TableRow>

                //): data[model] && data[model].map((row: any, index: number) => (
                ): Array.isArray(data?.[model]) && data[model].map((row: any, index: number) => (
                    <TableRow key={index}>
                        <TableCell>
                            <Checkbox className="border-black" />
                        </TableCell>
                        {tableColumn && tableColumn.map((column, index) => (
                            <TableCell key={index}>{column.render(row)}</TableCell>
                        ))}
                        <TableCell>
                            
                        </TableCell>
                    </TableRow>

                ))}
            </TableBody>
        </Table>
    )
}

export default CustomTable;