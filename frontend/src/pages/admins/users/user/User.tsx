import React, { useEffect, useState } from "react";
import { breadcrumb, model, pagination, tableColumn } from "@/services/UserService";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";

import PageHeading from "@/components/admins/heading";
import CustomTable from "@/components/admins/CustomTable";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useQuery } from "react-query";
import { Breadcrumb } from "@/types/Breadcrumb";
import Paginate from "@/components/admins/paginate";

const View: React.FC = () => {
    const breadcrumbData: Breadcrumb = breadcrumb;
    const navigate = useNavigate();
    const [searchParmas, setSearchParmas] = useSearchParams();
    const currentPage = searchParmas.get('page') ? parseInt(searchParmas.get('page')!) : 1
    const [page, setPage] = useState<number | null>(currentPage)
    const { isLoading, data, isError, refetch } = useQuery(['users', page], () => pagination(page))

    const handlePageChange = (page: number | null) => {
        setPage(page)
        navigate(`page?${page}`)
    }

    console.log("data from useQuery:", data);

    useEffect(() => {
        setSearchParmas({ page: currentPage.toString() })
        refetch()
    },[page, refetch])
    
    return (
        <>
            <PageHeading breadcrumb = {breadcrumbData} />
            <div className="container p-3">
                <Card>
                    <CardContent>
                        <CustomTable 
                            isLoading = {isLoading}
                            data={data}
                            isError={isError}
                            model={model}
                            tableColumn={tableColumn}
                        />
                    </CardContent>
                    <CardFooter>
                        <Paginate links={data?.users} />
                    </CardFooter>
                </Card>
            </div>
        </>
    );
};

export { View };